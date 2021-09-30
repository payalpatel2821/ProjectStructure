package com.task.newapp.ui.fragments.post

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.paginate.Paginate
import com.task.newapp.App
import com.task.newapp.R
import com.task.newapp.adapter.post.PostCommentAdapter
import com.task.newapp.api.ApiClient
import com.task.newapp.databinding.FragmentPostCommentListBinding
import com.task.newapp.models.ResponsePostList
import com.task.newapp.models.User
import com.task.newapp.models.post.ResponseAddPostComment
import com.task.newapp.models.post.ResponseGetAllPost
import com.task.newapp.models.post.ResponseGetAllPostComments
import com.task.newapp.models.post.ResponseGetAllPostComments.*
import com.task.newapp.models.post.ResponseGetPostComment
import com.task.newapp.models.socket.PostSocket
import com.task.newapp.utils.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class PostCommentListFragment : BottomSheetDialogFragment(), View.OnClickListener, Paginate.Callbacks,
    OnCommentItemClickListener {

    var postCommentList: ArrayList<AllPostCommentData> = ArrayList()
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
    var allPostData: String = ""
    var onChangePostItem: OnChangePostItem? = null
    var allPostDataModel: ResponseGetAllPost.All_Post_Data? = null
    var flagAddPostClick: Boolean = false

    fun newInstance(postId: Int, userId: Int, position: Int, allPostData: String): PostCommentListFragment {
        val f = PostCommentListFragment()
        val args = Bundle()
        args.putInt("postId", postId)
        args.putInt("userId", userId)
        args.putInt("position", position)
        args.putString("allPostData", allPostData)
        f.arguments = args
        Log.e("postId: ", postId.toString())
        return f
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postId = requireArguments().getInt("postId", 0)
        userId = requireArguments().getInt("userId", 0)
        adapterPositionPost = requireArguments().getInt("position", 0)
        allPostData = requireArguments().getString("allPostData").toString()

        val type: Type = object : TypeToken<ResponseGetAllPost.All_Post_Data>() {}.type
        allPostDataModel = Gson().fromJson(allPostData, type)

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
        PostFragment.instance.isOpenCommentBottomSheet = true
        postCommentList = ArrayList()
        binding.txtPost.setOnClickListener(this)
        binding.edtComment.setText("")

        //list
        var manager = LinearLayoutManager(activity) //, 1, GridLayoutManager.VERTICAL, false)
        postCommentAdapter = PostCommentAdapter(activity as AppCompatActivity, postId, ArrayList(), 0)

        binding.recComments.adapter = null

//        manager.stackFromEnd = true
        manager.reverseLayout = true

        binding.recComments.layoutManager = manager
        binding.recComments.adapter = postCommentAdapter
        postCommentAdapter.setOnItemClickListener(this)

//        binding.recComments.setOnTouchListener(View.OnTouchListener { v, event ->
//            val imm =
//                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//            imm.hideSoftInputFromWindow(v.windowToken, 0)
//            false
//        })

        //----------------------------Show/Hide Add Comment Layout as Turn on/off comment-----------------------------------
        binding.llAddComment.visibility = if (allPostDataModel!!.turnOffComment == 0) View.VISIBLE else View.GONE

        //init paging
        initPaging()

        openProgressDialog(activity)
        callAllPostComment(0, postId = postId)

        binding.edtComment.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(getActivity())
            } else {
                showKeyboard(getActivity())
            }
        }

        //-------------------------set Post Button enable/diable --------------------------
        binding.edtComment.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                enableOrDisableImageViewTint(activity, s.toString().trim().isNotEmpty(), binding.txtPost)
            }
        })
    }

    fun showKeyboard(activity: Activity?) {
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
    }

    fun hideKeyboard(activity: Activity?) {
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }

    private fun handleUserExit() {
        PostFragment.instance.isOpenCommentBottomSheet = false
//        requireActivity().showToast("Dialog Close")
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
                Constants.limit to resources.getString(R.string.limit_20).toInt()
            )
            //openProgressDialog(activity)

            mCompositeDisposable.add(
                ApiClient.create()
                    .allpostcomment(hashMap)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableObserver<ResponseGetAllPostComments>() {
                        override fun onNext(responseGetAllPostComments: ResponseGetAllPostComments) {
//                            if (responseGetAllPostComments.success == 1) {
                            Log.v("responsePostComment", "responsePostCommentDetails")

                            if (responseGetAllPostComments.success == 1) {
                                var postByMe = 0
                                postByMe =
                                    if (getCurrentUserId() == userId) {
                                        1
                                    } else {
                                        0
                                    }

                                postCommentList.addAll(responseGetAllPostComments.data)
                                postCommentAdapter.setData(postCommentList, postByMe)
                                isloadingComment = false
                                hasLoadedAllItemsComment = false
                            } else {
                                hasLoadedAllItemsComment = true
                                hideShowEmptyView(postCommentList)
                            }

                            //--------------------------------------------------------------------------------------
//                                if (responseGetAllPostComments.data.isNotEmpty()) {
//                                    var postByMe = 0
//                                    postByMe =
//                                        if (App.fastSave.getInt(Constants.prefUserId, 0) == userId) {
//                                            1
//                                        } else {
//                                            0
//                                        }
//
//                                    if (responseGetAllPostComments.success == 1) {
//
//                                        if (responseGetAllPostComments.data.isEmpty()) {
//                                            showLog("loadmore", "finish")
//
//                                            hasLoadedAllItemsComment = true
//                                            hideShowEmptyView(postCommentList)
//
//                                        } else {
//                                            postCommentList.addAll(responseGetAllPostComments.data)
//                                            postCommentAdapter.setData(postCommentList, postByMe)
//                                            isloadingComment = false
//                                            hasLoadedAllItemsComment = false
//
//                                        }
//                                    }
//                                }
//                            }
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

    private fun hideShowEmptyView(postCommentList: ArrayList<AllPostCommentData>) {
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
//                Constants.comment_id to 0,  // if reply then only send comment id
                Constants.is_comment_reply to 0,
//                Constants.type to "comment",
                Constants.comment_text to commentText,
                Constants.post_id to postId,
                Constants.main_comment_id to 0
            )

//            openProgressDialog(activity)

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
                                        postByMe = if (getCurrentUserId() == userId) {
                                            1
                                        } else {
                                            0
                                        }

                                        val c = Calendar.getInstance()
                                        val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                        val formattedDate = df.format(c.time)

                                        val tempArrayList: ArrayList<AllPostCommentData> = ArrayList()
                                        val prefUser = App.fastSave.getObject(Constants.prefUser, User::class.java)

                                        val commentUser =
                                            AllPostCommentData.CommentUser(
                                                id = prefUser.id,
                                                profileImage = (prefUser.profileImage ?: ""),
                                                firstName = (prefUser.firstName ?: ""),
                                                lastName = (prefUser.lastName ?: ""),
                                                profileColor = (prefUser.profileColor ?: ""),
                                                accountId = (prefUser.accountId ?: "")
                                            )

                                        var postDataModel =
                                            AllPostCommentData(
                                                id = this!!.id,
                                                postId = this!!.post_id!!.toInt(),
                                                commentText = comment_text.toString(),
                                                user = commentUser,
                                                updatedAt = formattedDate,
                                                mainCommentId = 0
                                            )

//                                        tempArrayList.add(0, postDataModel)
//                                        tempArrayList.addAll(postCommentAdapter.getData())

//                                        flagAddPostClick = true

                                        postCommentList.add(0, postDataModel)

                                        postCommentAdapter.setData(
                                            allPostCommentDataList = postCommentList,
                                            post_by_me = postByMe
                                        )
//                                        postCommentAdapter.setData1(
//                                            allPostCommentDataList = postDataModel,
//                                            post_by_me = postByMe
//                                        )
                                        hideShowEmptyView(postCommentAdapter.getData())

                                        activity.runOnUiThread(Runnable {
                                            binding.recComments.scrollToPosition(
                                                0
                                            )
                                        })

                                        //Update Post
                                        onChangePostItem!!.onChangePostItem(
                                            responseAddPostComment.data!!.comment_text!!.toString(),
                                            responseAddPostComment.total_comment,
                                            adapterPositionPost
                                        )

                                        //------------------Add socket event-------------------
                                        val postSocket = PostSocket(
                                            user_id = getCurrentUserId(),
                                            post_id = postId,
                                            isDeleteComment = false,
                                            commentModel = postDataModel,
                                            totalComments = responseAddPostComment.total_comment
                                        )

                                        addPostCommentEmitEvent(postSocket)
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

    override fun onDeleteCommentClick(positionMain: Int, positionSub: Int) {
        DialogUtils().showConfirmationYesNoDialog(
            activity as AppCompatActivity,
            "",
            requireContext().resources.getString(R.string.delete_comment),
            object : DialogUtils.DialogCallbacks {
                override fun onPositiveButtonClick() {
                    if (activity!!.isNetworkConnected()) {
                        if (positionMain < postCommentAdapter.getData()!!.size) {
                            var commentId = postCommentAdapter.getData()[positionMain].id
                            deleteComment(positionMain, positionSub, "main", commentId)
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

    override fun onDeleteCommentReplyClick(positionMain: Int, positionSub: Int) {
        DialogUtils().showConfirmationYesNoDialog(activity as AppCompatActivity,
            "",
            requireContext().resources.getString(R.string.delete_replay),
            object : DialogUtils.DialogCallbacks {
                override fun onPositiveButtonClick() {
                    if (activity!!.isNetworkConnected()) {
                        if (positionMain < postCommentAdapter.getData()!!.size) {
                            if (postCommentAdapter.getData()[positionMain].commentReplyList.size > 0) {
                                var commentId = postCommentAdapter.getData()[positionMain].commentReplyList[positionSub]!!.id
                                deleteComment(positionMain, positionSub, "reply", commentId)
                            }
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

    fun deleteComment(positionMain: Int, positionSub: Int, reply: String, comment_id: Int) {
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

                            var mainCommentId = postCommentAdapter.getData()[positionMain].id
                            var replyCommentId = 0
                            replyCommentId = if (postCommentAdapter.getData()[positionMain].commentReplyList.isNotEmpty()) {
                                postCommentAdapter.getData()[positionMain].commentReplyList[positionSub].id
                            } else {
                                0
                            }

                            if (responseGetPostComment.success == 1) {
                                if (reply == "reply") {

                                    //Reply Delete
                                    postCommentAdapter.getData()[positionMain].commentReplyList.removeAt(positionSub)
                                    postCommentAdapter.notifyItemChanged(positionMain)

//                                    postCommentList[positionMain].commentReplyList.removeAt(positionSub)

                                } else {
                                    //Normal Delete
                                    postCommentAdapter.allPostCommentDataList!!.removeAt(positionMain)
                                    postCommentAdapter.notifyItemRemoved(positionMain)
                                    postCommentAdapter.notifyItemRangeChanged(
                                        positionMain,
                                        postCommentAdapter.getData().size
                                    )

                                    postCommentList.removeAt(positionMain)

                                    hideShowEmptyView(postCommentAdapter.allPostCommentDataList)
                                }

                                onChangePostItem!!.onChangePostItem(
                                    responseGetPostComment.data?.commentText ?: "",
                                    responseGetPostComment.totalComment,
                                    adapterPositionPost
                                )

                                //------------------------Socket Event----------------------------
                                val postSocket = PostSocket(
                                    user_id = getCurrentUserId(),
                                    post_id = postId,
                                    isDeleteComment = true,
                                    mainCommentId = mainCommentId,
                                    replyCommentId = replyCommentId,
                                    isReplyDelete = reply == "reply",
                                    totalComments = responseGetPostComment.totalComment
                                )
                                deletePostCommentEmitEvent(postSocket)
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

    override fun onCommentReplyClick(positionMain: Int, positionSub: Int, commentId: Int, replyText: String, main_comment_id: Int) {
        try {
            openProgressDialog(activity)

            val hashMap: HashMap<String, Any> = hashMapOf(
                Constants.post_id to postId,
                Constants.comment_text to replyText,
                Constants.comment_id to commentId,
                Constants.is_comment_reply to 1,
                Constants.main_comment_id to if (main_comment_id == 0) commentId else main_comment_id
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

//                                    replytxt.setContent(replytext)

                                    //Set reply in TextView
//                                    replytxt.text = replyText

                                    //Clear reply Ediitext and hide reply box
//                                    editText.setText("")
//                                    reply_box.visibility = View.GONE

                                    postCommentAdapter.checkedPosition = -1

//                                    postCommentAdapter.allPostCommentDataList[position].commentReply?.commentText = replyText
//                                    postCommentAdapter.notifyItemChanged(position)

                                    //--------------------------Uncomment---------------------------------

                                    val data = responseAddPostComment.data

                                    val prefUser = App.fastSave.getObject(Constants.prefUser, User::class.java)

                                    val commentReplyUser = AllPostCommentData.CommentReply.User(
                                        id = data!!.user_id!!,
                                        first_name = (prefUser.firstName ?: ""),
                                        last_name = (prefUser.lastName ?: ""),
                                        account_id = (prefUser.accountId ?: ""),
                                        profile_image = (prefUser.profileImage ?: ""),
                                        profile_color = (prefUser.profileColor ?: "")
                                    )

                                    val commentReply =
                                        AllPostCommentData.CommentReply(
                                            data!!.id!!,
                                            data!!.post_id!!,
                                            data!!.user_id!!,
                                            data!!.is_comment_reply!!,
                                            data!!.user_id!!,
                                            data!!.main_comment_id!!,
                                            data!!.comment_text!!,
                                            data!!.status!!,
                                            0,
                                            data!!.created_at!!,
                                            data!!.updated_at!!,
                                            commentReplyUser
                                        )

                                    val arraylistMainCopy: MutableList<AllPostCommentData> = ArrayList()
                                    arraylistMainCopy.addAll(postCommentAdapter.getData())

                                    //Set data in first position
                                    val tempArrayListReply: ArrayList<AllPostCommentData.CommentReply> = ArrayList()
                                    tempArrayListReply.add(commentReply)

                                    arraylistMainCopy[positionMain].commentReplyList.addAll(tempArrayListReply)

                                    var postByMe = 0
                                    postByMe = if (App.fastSave.getInt(Constants.prefUserId, 0) == userId) {
                                        1
                                    } else {
                                        0
                                    }

                                    //Add New
//                                    postCommentList[positionMain].commentReplyList.add(commentReply)

                                    postCommentAdapter.setData(
                                        allPostCommentDataList = arraylistMainCopy!! as ArrayList<AllPostCommentData>,
//                                        allPostCommentDataList = postCommentList,
                                        post_by_me = postByMe
                                    )

//                                    postCommentAdapter.notifyDatasetChanged()
                                    postCommentAdapter.notifyItemChanged(positionMain)

                                    //Update Post
                                    onChangePostItem!!.onChangePostItem(
                                        responseAddPostComment.data!!.comment_text!!,
                                        responseAddPostComment.total_comment,
                                        adapterPositionPost
                                    )

                                    //-----------------------Socket Event-------------------------
                                    val postSocket = PostSocket(
                                        user_id = getCurrentUserId(),
                                        post_id = postId,
                                        mainCommentId = arraylistMainCopy[positionMain].id,
                                        commentReplyModel = commentReply,
                                        totalComments = responseAddPostComment.total_comment
                                    )
                                    addPostCommentReplyEmitEvent(postSocket)
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

    fun addPostComment(postSocket: PostSocket) {
        Log.e("addPostComment", "addPostComment: $postSocket")

        try {
            if (App.fastSave.getInt(Constants.prefUserId, 0) != postSocket.user_id && postId == postSocket.post_id) {
                var postByMe = if (getCurrentUserId() == userId) {
                    1
                } else {
                    0
                }
                postCommentList.add(0, postSocket.commentModel!!)

                postCommentAdapter.setData(
                    allPostCommentDataList = postCommentList,
                    post_by_me = postByMe
                )
//                }

                activity.runOnUiThread(Runnable {
                    binding.recComments.smoothScrollToPosition(0)
                })

//                var totalComments = 0
//                if (postSocket.isDeleteComment) {
//                    if (allPostDataModel!!.commentsCount > 0) {
//                        totalComments = allPostDataModel!!.commentsCount - 1
//                    }
//                } else {
//                    totalComments = allPostDataModel!!.commentsCount + 1
//                }

                //Update Post
                onChangePostItem!!.onChangePostItem(
                    lastComment = allPostDataModel!!.latest_comment!!.commentText,
                    totalComments = postSocket.totalComments!!,  //totalComments,
                    adapterPositionPost
                )

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onLoadMore() {
//        if (this.flagAddPostClick) {
//            return
//        }

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

    private fun initPaging() {
        if (paginateComment != null) {
            paginateComment!!.unbind()
        }

        paginateComment = Paginate.with(binding.recComments, this)
            .setLoadingTriggerThreshold(17)
            .addLoadingListItem(false)
            .setLoadingListItemCreator(null)
            .build()


        binding.recComments.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (dy > 0) {
                    // Scrolling up
                    showLog("onScrolled", "up")
                } else {
                    // Scrolling down
                    showLog("onScrolled", "down")
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (!recyclerView.canScrollVertically(1)) {
                    flagAddPostClick = false

                    Toast.makeText(activity, "Last", Toast.LENGTH_LONG).show();

                }
            }
        })
    }

    fun deletePostComment(postSocket: PostSocket) {
        Log.e("deletePostComment", "deletePostComment: $postSocket")

        try {
            if (App.fastSave.getInt(Constants.prefUserId, 0) != postSocket.user_id && postId == postSocket.post_id) {
                var postByMe = if (getCurrentUserId() == userId) {
                    1
                } else {
                    0
                }

                //Get index of deleted comment id and remove from the array
                val findedModel = postCommentList?.find {
                    it.id == postSocket.mainCommentId
                }

                var positionOfMainCommentId = 0

                if (postSocket.isReplyDelete!!) {
                    //Reply Delete
                    //Get Reply Comment Id

                    //Get main position of adapter
                    positionOfMainCommentId = postCommentList?.indexOf(findedModel)

                    if (positionOfMainCommentId >= 0) {
                        val findedModel = postCommentList[positionOfMainCommentId].commentReplyList?.find {
                            it.id == postSocket.replyCommentId
                        }

                        findedModel?.let {
                            val positionOfReplyCommentId = postCommentList[positionOfMainCommentId].commentReplyList?.indexOf(findedModel)
                            postCommentList[positionOfMainCommentId].commentReplyList.removeAt(positionOfReplyCommentId)

                            showLog("positionOfMainCommentId", "$positionOfMainCommentId,$positionOfReplyCommentId")
                        }
                    }

                } else {
                    //Get main position of adapter
                    val positionOfMainCommentId = postCommentList?.indexOf(findedModel)
                    showLog("positionOfMainCommentId", positionOfMainCommentId.toString())

                    postCommentList.removeAt(positionOfMainCommentId)
                }

                postCommentAdapter.setData(
                    allPostCommentDataList = postCommentList,
                    post_by_me = postByMe
                )

                postCommentAdapter.notifyItemChanged(positionOfMainCommentId)

//                var totalComments = 0
//                if (postSocket.isDeleteComment) {
//                    if (allPostDataModel!!.commentsCount > 0) {
//                        totalComments = allPostDataModel!!.commentsCount - 1
//                    }
//                } else {
//                    totalComments = allPostDataModel!!.commentsCount + 1
//                }

                //Update Post
                onChangePostItem!!.onChangePostItem(
                    lastComment = allPostDataModel!!.latest_comment!!.commentText,
                    totalComments = postSocket.totalComments!!, //totalComments,
                    adapterPositionPost
                )

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun addPostCommentReply(postSocket: PostSocket) {
        Log.e("addPostCommentReply", "addPostCommentReply: $postSocket")

        try {
            if (App.fastSave.getInt(Constants.prefUserId, 0) != postSocket.user_id && postId == postSocket.post_id) {

                val findedModel = postCommentList?.find {
                    it.id == postSocket.mainCommentId
                }
                val positionOfMainCommentId = postCommentList?.indexOf(findedModel)

                val arraylistMainCopy: MutableList<AllPostCommentData> = ArrayList()
                arraylistMainCopy.addAll(postCommentAdapter.getData())

                //Set data in first position
                val tempArrayListReply: ArrayList<AllPostCommentData.CommentReply> = ArrayList()
                tempArrayListReply.add(postSocket.commentReplyModel!!)

                arraylistMainCopy[positionOfMainCommentId].commentReplyList.addAll(tempArrayListReply)

                var postByMe = if (App.fastSave.getInt(Constants.prefUserId, 0) == userId) {
                    1
                } else {
                    0
                }

                postCommentAdapter.setData(
                    allPostCommentDataList = arraylistMainCopy!! as ArrayList<AllPostCommentData>,
//                                        allPostCommentDataList = postCommentList,
                    post_by_me = postByMe
                )

                postCommentAdapter.notifyItemChanged(positionOfMainCommentId)


//                var totalComments = 0
//                if (postSocket.isDeleteComment) {
//                    if (allPostDataModel!!.commentsCount > 0) {
//                        totalComments = allPostDataModel!!.commentsCount - 1
//                    }
//                } else {
//                    totalComments = allPostDataModel!!.commentsCount + 1
//                }

                //Update Post
                onChangePostItem!!.onChangePostItem(
                    lastComment = allPostDataModel!!.latest_comment!!.commentText,
                    totalComments = postSocket.totalComments!!,  //totalComments,
                    adapterPositionPost
                )

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

}
