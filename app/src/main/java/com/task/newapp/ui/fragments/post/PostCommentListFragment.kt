package com.task.newapp.ui.fragments.post

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.paginate.Paginate
import com.task.newapp.App
import com.task.newapp.R
import com.task.newapp.adapter.post.PostCommentAdapter
import com.task.newapp.api.ApiClient
import com.task.newapp.databinding.FragmentPostCommentListBinding
import com.task.newapp.models.User
import com.task.newapp.models.post.PostSocket
import com.task.newapp.models.post.ResponseAddPostComment
import com.task.newapp.models.post.ResponseGetAllPostComments
import com.task.newapp.models.post.ResponseGetPostComment
import com.task.newapp.utils.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class PostCommentListFragment : BottomSheetDialogFragment(), View.OnClickListener,
    PostCommentAdapter.OnCommentItemClickListener {

    var postCommentList: ArrayList<ResponseGetAllPostComments.AllPostCommentData> = ArrayList()
    var postId: Int = 0
    lateinit var postCommentAdapter: PostCommentAdapter
    private lateinit var binding: FragmentPostCommentListBinding
    private lateinit var activity: Activity
    private val mCompositeDisposable = CompositeDisposable()
    private var paginate: Paginate? = null
    private var isAPICallRunning = false
    private var paginateComment: Paginate? = null
    var isloadingComment = false
    var hasLoadedAllItemsComment = false
    var userId: Int = 0
    var adapterPositionPost: Int = 0
    var onChangePostItem: OnChangePostItem? = null

    fun newInstance(postId: Int, userId: Int, position: Int): PostCommentListFragment {
        val f = PostCommentListFragment()
        val args = Bundle()
        args.putInt("postId", postId)
        args.putInt("userId", userId)
        args.putInt("position", position)
        f.arguments = args
        Log.e("postId: ", postId.toString())
        return f
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postId = requireArguments().getInt("postId", 0)
        userId = requireArguments().getInt("userId", 0)
        adapterPositionPost = requireArguments().getInt("position", 0)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_post_comment_list, container, false)
        this.activity = requireActivity()

//        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initView()
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        handleUserExit()
    }

    private fun initView() {
        binding.txtPost.setOnClickListener(this)
        binding.edtComment.setText("")

        //list
        var manager = GridLayoutManager(activity, 1, GridLayoutManager.VERTICAL, false)
        postCommentAdapter =
            PostCommentAdapter(activity as AppCompatActivity, postId, ArrayList(), 0)

        binding.recComments.setHasFixedSize(true)
        binding.recComments.layoutManager = manager
        binding.recComments.adapter = postCommentAdapter
        postCommentAdapter.setOnItemClickListener(this)

        binding.recComments.setOnTouchListener(View.OnTouchListener { v, event ->
            val imm =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(v.windowToken, 0)
            false
        })

        //init paging
        if (paginateComment != null) {
            paginateComment!!.unbind()
        }

        paginateComment = Paginate.with(binding.recComments, object : Paginate.Callbacks {
            override fun onLoadMore() {
                isloadingComment = true

                val scrollPosition: Int = postCommentAdapter.allPostCommentDataList.size
                if (scrollPosition > 0) {
                    showLog("loadmore", scrollPosition.toString())
                    val currentSize = scrollPosition - 1

                    if (currentSize > 0) {
                        callAllPostComment(
                            postCommentAdapter.allPostCommentDataList[currentSize].id,
                            postId
                        )
                    }
                }
            }

            override fun isLoading(): Boolean {
                return isloadingComment
            }

            override fun hasLoadedAllItems(): Boolean {
                return hasLoadedAllItemsComment
            }

        })
            .setLoadingTriggerThreshold(17)
            .addLoadingListItem(false)
            .setLoadingListItemCreator(null)
            .build()

        openProgressDialog(activity)

        callAllPostComment(0, postId = postId)
    }

    private fun handleUserExit() {
        activity!!.showToast("Dialog Close")
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.imgBack -> dismiss()
            R.id.txtPost -> {
                if (binding.edtComment.text.toString().isNotEmpty()) {
                    callAPIAddPostComment(binding.edtComment.text.toString().trim())
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mCompositeDisposable.clear()
    }

    /**
     * interface for post done click
     *
     */
    interface OnPostTagDoneClickListener {
        fun onPostTagDoneClick(commaSeperatedIds: String)
    }

//    fun setListener(listener: OnPostTagDoneClickListener) {
//        onPostTagDoneClickListener = listener
//    }

    private fun checkAndEnable(enable: Boolean) {
        //enableOrDisableButtonBgColor(requireContext(), enable, binding.btnDone)
    }

    @NonNull
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setOnShowListener {
                (this@PostCommentListFragment.dialog as BottomSheetDialog).behavior.setState(
                    BottomSheetBehavior.STATE_EXPANDED
                )
            }
        }
    }

    private fun callAllPostComment(offset: Int, postId: Int) {
        try {
            val hashMap: HashMap<String, Any> = hashMapOf(
                Constants.post_id to postId,
                Constants.offset to offset,
                Constants.limit to resources.getString(R.string.get_comments).toInt()
            )
            //openProgressDialog(activity)

            mCompositeDisposable.add(
                ApiClient.create()
                    .allpostcomment(hashMap)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableObserver<ResponseGetAllPostComments>() {
                        override fun onNext(responseGetAllPostComments: ResponseGetAllPostComments) {
                            if (responseGetAllPostComments.success == 1) {
                                Log.v("responsePostComment", "responsePostCommentDetails")

//                                if (responseGetAllPostComments.success == 1) {
//
//                                    if (responseGetAllPostComments.data.isNotEmpty()) {
                                var postByMe = 0
                                postByMe =
                                    if (App.fastSave.getInt(Constants.prefUserId, 0) == userId) {
                                        1
                                    } else {
                                        0
                                    }

                                if (responseGetAllPostComments.success == 1) {

                                    if (responseGetAllPostComments.data.isEmpty()) {
                                        hasLoadedAllItemsComment = true
                                        hideShowEmptyView(postCommentList)
                                    } else {
                                        postCommentList.addAll(responseGetAllPostComments.data)
//                                    postCommentAdapter.setData(responseGetAllPostComments.data as ArrayList<ResponseGetAllPostComments.AllPostCommentData>, postByMe)
                                        postCommentAdapter.setData(postCommentList, postByMe)
                                        isloadingComment = false
                                        hasLoadedAllItemsComment = false
                                    }

                                }


//                                        postCommentAdapter.setData(
//                                            responseGetAllPostComments.data as ArrayList<AllPostCommentData>, postByMe
//                                        )
//
//                                        postCommentAdapter = PostCommentAdapter(
//                                            activity as AppCompatActivity, postId,
//                                            responseGetAllPostComments.data as ArrayList<ResponseGetAllPostComments.AllPostCommentData>, postByMe
//                                        )
//
//                                        binding.recComments.adapter = postCommentAdapter
//
//                                        isloadingComment = false
//                                        hasLoadedAllItemsComment = false
//                                    } else {
//                                        isloadingComment = true
//                                        hasLoadedAllItemsComment = true
//                                    }

//                                showHideEmptyView()

//                            } else {
//                                hasLoadedAllItemsComment = true
//                            }
                            }
                        }

                        override fun onError(e: Throwable) {
                            Log.v("onError: ", e.toString())
                            hideProgressDialog()
                        }

                        override fun onComplete() {
                            hideProgressDialog()
                        }
                    })
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun hideShowEmptyView(postCommentList: ArrayList<ResponseGetAllPostComments.AllPostCommentData>) {
        if (postCommentList.isEmpty()) {
            binding.recComments.visibility = View.GONE
            binding.imgNoComments.visibility = View.VISIBLE
        } else {
            binding.recComments.visibility = View.VISIBLE
            binding.imgNoComments.visibility = View.GONE
        }
    }

    private fun callAPIAddPostComment(commentText: String) {
        try {
            val hashMap: HashMap<String, Any> = hashMapOf(
                Constants.comment_id to 0,
                Constants.is_comment_reply to 0,
                Constants.type to "comment",
                Constants.comment_text to commentText,
                Constants.post_id to postId,
                Constants.main_comment_id to 0
            )

            openProgressDialog(activity)

            mCompositeDisposable.add(
                ApiClient.create()
                    .add_postcomment(hashMap)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableObserver<ResponseAddPostComment>() {
                        override fun onNext(responseAddPostComment: ResponseAddPostComment) {

                            if (responseAddPostComment.success == 1) {
//                                activity?.showToast(responseAddPostComment.message)

                                binding.edtComment.setText("")

                                postCommentAdapter?.let {

                                    responseAddPostComment?.data.run {
                                        var postByMe = 0
                                        postByMe = if (App.fastSave.getInt(
                                                Constants.prefUserId,
                                                0
                                            ) == userId
                                        ) {
                                            1
                                        } else {
                                            0
                                        }

                                        val c = Calendar.getInstance()
                                        val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                        val formattedDate = df.format(c.time)

                                        val tempArrayList: ArrayList<ResponseGetAllPostComments.AllPostCommentData> =
                                            ArrayList<ResponseGetAllPostComments.AllPostCommentData>()
                                        val prefUser = App.fastSave.getObject(
                                            Constants.prefUser,
                                            User::class.java
                                        )

                                        val commentUser =
                                            ResponseGetAllPostComments.AllPostCommentData.CommentUser(
                                                id = prefUser.id,
                                                profileImage = prefUser.profileImage,
                                                firstName = prefUser.firstName,
                                                lastName = prefUser.lastName
                                            )

                                        var postDataModel =
                                            ResponseGetAllPostComments.AllPostCommentData(
                                                id = this!!.id,
                                                postId = this!!.post_id!!.toInt(),
                                                commentText = comment_text.toString(),
                                                user = commentUser,
                                                updatedAt = formattedDate,
                                                mainCommentId = 0
                                            )

                                        tempArrayList.add(postDataModel)
                                        tempArrayList.addAll(postCommentAdapter.getData())

                                        postCommentAdapter.setData(
                                            allPostCommentDataList = tempArrayList,
                                            post_by_me = postByMe
                                        )
                                        hideShowEmptyView(postCommentAdapter.getData())

                                        activity.runOnUiThread(Runnable {
                                            binding.recComments.smoothScrollToPosition(
                                                0
                                            )
                                        })

                                        //Update Post
                                        onChangePostItem!!.onChangePostItem(
                                            this.comment_text!!.toString(),
                                            this.total_comment,
                                            adapterPositionPost
                                        )
                                    }
                                }
                            }
                        }

                        override fun onError(e: Throwable) {
                            Log.v("onError: ", e.toString())
                            hideProgressDialog()
                        }

                        override fun onComplete() {
                            hideProgressDialog()
                        }
                    })
            )
        } catch (e: Exception) {
            e.printStackTrace()
            hideProgressDialog()
        }
    }

    override fun onDeleteCommentClick(position: Int) {
        DialogUtils().showConfirmationYesNoDialog(
            activity as AppCompatActivity,
            "",
            requireContext().resources.getString(R.string.delete_comment),
            object : DialogUtils.DialogCallbacks {
                override fun onPositiveButtonClick() {
                    if (activity!!.isNetworkConnected()) {
                        if (position < postCommentAdapter.getData()!!.size) {
                            var commentId = postCommentAdapter.getData()[position].id
                            deleteComment(position, "main", commentId)
                        }
                    } else {
                        Toast.makeText(context, R.string.no_internet, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onNegativeButtonClick() {

                }

                override fun onDefaultButtonClick(actionName: String) {
                }

            })
    }

    override fun onDeleteCommentReplyClick(position: Int) {
        DialogUtils().showConfirmationYesNoDialog(activity as AppCompatActivity,
            "",
            requireContext().resources.getString(R.string.delete_replay),
            object : DialogUtils.DialogCallbacks {
                override fun onPositiveButtonClick() {
                    if (activity!!.isNetworkConnected()) {
                        if (position < postCommentAdapter.getData()!!.size) {
//                            var commentId = postCommentAdapter.getData()[position].commentReply!!.id
//                            deleteComment(position, "reply", commentId)
                        }
                    } else {
                        Toast.makeText(context, R.string.no_internet, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onNegativeButtonClick() {

                }

                override fun onDefaultButtonClick(actionName: String) {
                }

            })
    }

    fun deleteComment(position: Int, reply: String, comment_id: Int) {
        try {
            openProgressDialog(activity)

            val hashMap: HashMap<String, Any> = hashMapOf(
                Constants.id to comment_id
            )

            mCompositeDisposable.add(
                ApiClient.create()
                    .commentdelete(hashMap)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableObserver<ResponseGetPostComment>() {
                        override fun onNext(responseGetPostComment: ResponseGetPostComment) {
                            Log.v("onNext: ", responseGetPostComment.toString())

                            if (responseGetPostComment.success == 1) {
                                if (reply == "reply") {
//                                    val view: View = Show_Post.getInstance().manager.findViewByPosition(position)
//                                    if (view != null) {
//                                    postCommentAdapter.getData()[position].commentReply = null
//                                    postCommentAdapter.notifyItemChanged(position)

                                    onChangePostItem!!.onChangePostItem(
                                        responseGetPostComment.data?.commentText ?: "",
                                        responseGetPostComment.totalComment, adapterPositionPost
                                    )

                                } else {
                                    //Normal Delete
                                    postCommentAdapter.allPostCommentDataList!!.removeAt(position)
//                                    comments.setText(responseGetPostComment.data.toString() + " Comments")
                                    postCommentAdapter.notifyItemRemoved(position)
                                    postCommentAdapter.notifyItemRangeChanged(
                                        position,
                                        postCommentAdapter.getData().size
                                    )

                                    hideShowEmptyView(postCommentAdapter.allPostCommentDataList)

                                    onChangePostItem!!.onChangePostItem(
                                        responseGetPostComment.data?.commentText ?: "",
                                        responseGetPostComment.totalComment,
                                        adapterPositionPost
                                    )
                                }

                                val postSocket = PostSocket(getCurrentUserId(), postId)
                                val json: String = Gson().toJson(postSocket)
                                App.socket!!.emit(Constants.post_comment, json)
                            }
                        }

                        override fun onError(e: Throwable) {
                            Log.v("onError: ", e.toString())
                            hideProgressDialog()
                        }

                        override fun onComplete() {
                            hideProgressDialog()
                        }
                    })
            )
        } catch (e: Exception) {
            e.printStackTrace()
            hideProgressDialog()
        }
    }

    interface OnChangePostItem {
        fun onChangePostItem(lastComment: String, totalComments: Int, position: Int)
    }

    fun setListener(onChangePostItem: OnChangePostItem) {
        this.onChangePostItem = onChangePostItem
    }

    override fun onCommentReplyClick(position: Int, commentId: Int, replyText: String) {
        try {
            openProgressDialog(activity)

            val hashMap: HashMap<String, Any> = hashMapOf(
                Constants.comment_id to commentId,
                Constants.is_comment_reply to 1,
                Constants.type to "comment",
                Constants.comment_text to replyText,
                Constants.post_id to postId
            )

            mCompositeDisposable.add(
                ApiClient.create()
                    .add_postcomment(hashMap)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableObserver<ResponseAddPostComment>() {
                        override fun onNext(responseAddPostComment: ResponseAddPostComment) {
                            Log.v("onNext: ", responseAddPostComment.toString())
//                                act!!.showToast(responseGetAllPost.message)

                            if (responseAddPostComment != null) {
                                if (responseAddPostComment.success == 1) {
                                    val postSocket = PostSocket(
                                        App.fastSave.getInt(Constants.prefUserId, 0),
                                        postId
                                    )
                                    val json: String = Gson().toJson(postSocket)

                                    App.socket!!.emit(Constants.post_comment, json)
//                                    replytxt.setContent(replytext)

                                    //Set reply in TextView
//                                    replytxt.text = replyText

                                    //Clear reply Ediitext and hide reply box
//                                    editText.setText("")
//                                    reply_box.visibility = View.GONE

                                    postCommentAdapter.checkedPosition = -1

//                                    postCommentAdapter.allPostCommentDataList[position].commentReply?.commentText = replyText
//                                    postCommentAdapter.notifyItemChanged(position)

                                    val data = responseAddPostComment.data

//                                    val commentReplyUser =
//                                        ResponseGetAllPostComments.AllPostCommentData.CommentReply.User(
//                                            data!!.first_name!!,
//                                            "",
//                                            0,
//                                            data!!.last_name!!,
//                                            ""
//                                        )
//
//                                    val comment_reply =
//                                        ResponseGetAllPostComments.AllPostCommentData.CommentReply(
//                                            0,
//                                            data!!.comment_text!!,
//                                            "",
//                                            data!!.id,
//                                            0,
//                                            0,
//                                            0,
//                                            "",
//                                            data!!.updated_at!!,
//                                            commentReplyUser,
//                                            0
//                                        )
//
//                                    postCommentAdapter.allPostCommentDataList!![position].commentReply = comment_reply
////                                    comments.setText(response.body().getData().getTotal_comment().toString() + " Comments")
//                                    postCommentAdapter.notifyDataSetChanged()
                                }
                            }
                        }

                        override fun onError(e: Throwable) {
                            Log.v("onError: ", e.toString())
                            hideProgressDialog()
                        }

                        override fun onComplete() {
                            hideProgressDialog()
                        }
                    })
            )
        } catch (e: Exception) {
            e.printStackTrace()
            hideProgressDialog()
        }
    }

}
 