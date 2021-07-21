package com.task.newapp.ui.fragments.registration

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.github.nkzawa.socketio.client.Socket
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.paginate.Paginate
import com.task.newapp.App
import com.task.newapp.App.Companion.fastSave
import com.task.newapp.R
import com.task.newapp.adapter.Post_Comment_Adapter
import com.task.newapp.adapter.Post_Frag_Adapter
import com.task.newapp.api.ApiClient
import com.task.newapp.databinding.FragmentPostBinding
import com.task.newapp.models.CommonResponse
import com.task.newapp.models.post.*
import com.task.newapp.models.post.ResponseGetAllPost.All_Post_Data
import com.task.newapp.utils.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.util.*
import kotlin.collections.ArrayList


class PostFragment : Fragment(), View.OnClickListener, Paginate.Callbacks {

    companion object {
        lateinit var instance: PostFragment
    }

    lateinit var myBottomSheetMoment: BottomSheetDialogFragment
    lateinit var myBottomSheetThought: BottomSheetDialogFragment

    var isreplybox: Boolean = false
    private lateinit var binding: FragmentPostBinding
    var act: FragmentActivity? = null
    private lateinit var socket: Socket
    private var isAPICallRunning = false
    var linearLayoutManager: LinearLayoutManager? = null
    private val mCompositeDisposable = CompositeDisposable()
    lateinit var post_Frag_adapter: Post_Frag_Adapter
    lateinit var post_comment_adapter: Post_Comment_Adapter

    var isloading = false
    var isloadingComment = false
    var hasLoadedAllItems = false
    var hasLoadedAllItemsComment = false
    private var paginate: Paginate? = null
    private var paginateComment: Paginate? = null
    var post_id: Int = 0
    private lateinit var sheetBehavior: BottomSheetBehavior<LinearLayout>

    fun getInstance(): PostFragment {
        return instance
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_post, container, false
        )
        act = activity
        instance = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //initSocketListeners()
        initView()
        initBottomSheet()
    }

    private fun initPaging() {
        if (paginate != null) {
            paginate!!.unbind()
        }

        paginate = Paginate.with(binding.recPost, this)
            .setLoadingTriggerThreshold(17)
            .addLoadingListItem(false)
            .setLoadingListItemCreator(null)
            .build()
    }

    private fun initView() {
        socket = App.getSocketInstance()
       // initSocketListeners()
        binding.llMomentsPhotos.setOnClickListener(this)
        binding.llMomentsVideos.setOnClickListener(this)
        binding.llThoughts.setOnClickListener(this)

        binding.recPost.setHasFixedSize(true)
        binding.recPost.setItemViewCacheSize(20)
        binding.recPost.isDrawingCacheEnabled = true
        binding.recPost.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH

        binding.recPost.isFocusable = false
        post_Frag_adapter = Post_Frag_Adapter(requireContext(), ArrayList())
        linearLayoutManager = LinearLayoutManager(activity)
        linearLayoutManager!!.isAutoMeasureEnabled = false
        binding.recPost.layoutManager = linearLayoutManager
        binding.recPost.adapter = post_Frag_adapter

        binding.swipeContainer.setOnRefreshListener {
            binding.swipeContainer.isRefreshing = true
            if (!isAPICallRunning) {
                showLog("initView", "get_all_post")
                get_all_post(0)
            }
        }
        get_all_post(0)

//        binding.swipeContainer.post {
//            binding.swipeContainer.isRefreshing = true
//            if (!isAPICallRunning) get_all_post(0, "main")
//        }

        binding.swipeContainer.setColorSchemeResources(
            R.color.colorPrimary,
            android.R.color.holo_green_dark,
            android.R.color.holo_orange_dark,
            android.R.color.holo_blue_dark
        )

        initPaging()

        setProfilePic()
    }

    private fun setProfilePic() {
        Glide.with(requireActivity()).load(fastSave.getString(Constants.profile_image, ""))
            .error(R.drawable.default_dp).into(binding.imgUser)
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.ll_moments_photos -> {
//                val ft: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
//                val fragmentDemo: MomentsFragment = MomentsFragment.newInstance(5, "my title")
//                ft.replace(R.id.your_placeholder, fragmentDemo)
//                ft.commit()

                myBottomSheetMoment = MomentsFragment().newInstance(post_id.toString())
                myBottomSheetMoment.show(childFragmentManager, myBottomSheetMoment.tag)
            }
            R.id.ll_moments_photos -> {
//                val ft: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
//                val fragmentDemo: MomentsFragment = MomentsFragment.newInstance(5, "my title")
//                ft.replace(R.id.your_placeholder, fragmentDemo)
//                ft.commit()

                myBottomSheetMoment = MomentsFragment().newInstance(post_id.toString())
                myBottomSheetMoment.show(childFragmentManager, myBottomSheetMoment.tag)
            }
            R.id.ll_thoughts -> {
                myBottomSheetThought = ThoughtFragment().newInstance(post_id.toString())
                myBottomSheetThought.show(childFragmentManager, myBottomSheetThought.tag)
            }
        }
    }

    fun get_all_post(currentpos: Int) {
        isAPICallRunning = true
        if (activity != null) {
            try {
//                openProgressDialog(activity)

                mCompositeDisposable.add(
                    ApiClient.create()
                        .get_all_posts(resources.getString(R.string.get_al_post).toInt(), currentpos)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableObserver<ResponseGetAllPost>() {
                            override fun onNext(responseGetAllPost: ResponseGetAllPost) {
                                Log.v("onNext: ", responseGetAllPost.toString())
//                                act!!.showToast(responseGetAllPost.message)

                                if (responseGetAllPost != null) {
                                    if (responseGetAllPost.success == 1) {

                                        if (responseGetAllPost.data.isNotEmpty()) {

                                            if (binding.swipeContainer.isRefreshing) post_Frag_adapter.clear()

                                            post_Frag_adapter.setdata(responseGetAllPost.data as ArrayList<All_Post_Data>)
                                            isloading = false
                                            hasLoadedAllItems = false
                                        } else {
                                            isloading = true
                                            hasLoadedAllItems = true
                                        }

                                        post_Frag_adapter.onItemClick = { view, position ->
                                            if (post_Frag_adapter.getdata().isNotEmpty()) {

                                                val allPostData = post_Frag_adapter.getdata()[position]
                                                post_id = allPostData.id

                                                when (view.id) {
                                                    R.id.imgLike -> {
                                                       // initSocketListeners()

                                                        val imgLike: ImageView = view as ImageView

                                                        val postSocket = PostSocket(
                                                            fastSave.getInt(Constants.prefUserId, 0),
                                                            post_id
                                                        )

                                                        var likeType = ""
                                                        when (allPostData.isLike) {
                                                            0 -> {
                                                                likeType = "like"
                                                                imgLike.setImageResource(R.drawable.ic_like)

                                                                if (allPostData.likesCount >= 0) {
                                                                    val count = allPostData.likesCount + 1

                                                                    post_Frag_adapter.updateLikesCount(count, position, 1)
                                                                }
                                                            }
                                                            1 -> {
                                                                likeType = "unlike"
                                                                imgLike.setImageResource(R.drawable.ic_not_like)

                                                                if (allPostData.likesCount > 0) {
                                                                    val count = allPostData.likesCount - 1

                                                                    post_Frag_adapter.updateLikesCount(count, position, 0)
                                                                }
                                                            }
                                                        }

                                                        Log.e("onNext: ", Gson().toJson(postSocket))
                                                        socket.emit(Constants.post_like, Gson().toJson(postSocket))

                                                        //Call API for like
                                                        callAPIPostLikeDislike(likeType = likeType, postId = post_id.toString())
                                                    }

                                                    R.id.imgSave -> {
                                                        val imgSave: ImageView = view as ImageView

                                                        var saveType = ""

                                                        when (allPostData.isSave) {
                                                            0 -> {
                                                                saveType = "save"
                                                                imgSave.setImageResource(R.drawable.ic_save)

                                                                post_Frag_adapter.updateSave(1, position)
                                                            }
                                                            1 -> {
                                                                saveType = "unsave"
                                                                imgSave.setImageResource(R.drawable.ic_nonsave)

                                                                post_Frag_adapter.updateSave(0, position)
                                                            }
                                                        }

                                                        //Call API for save
                                                        callAPIPostSave(saveType = saveType, postId = post_id.toString())
                                                    }

                                                    R.id.txtPost -> {
//                                                        val imgSave: ImageView = view as ImageView

                                                        val itemView = binding.recPost.findViewHolderForAdapterPosition(position)!!.itemView;
                                                        var edt_comment: EditText = itemView.findViewById(R.id.edt_comment) as EditText

                                                        if (edt_comment.text.toString().isNotEmpty()) {
                                                            //Call API for add comment
                                                            callAPIPostComment(commentText = edt_comment.text.toString(), postId = post_id.toString(), position)
                                                        }
                                                    }

                                                    R.id.llComment -> expandCollapse(post_id)

                                                    R.id.more_iv -> showPostDialog()
                                                }

                                                //val contents: List<All_Post_contents?> = post_Frag_adapter.getdata()[position].contents
                                                val contents: List<All_Post_Data.Content?> = post_Frag_adapter.getdata()[position].contents
//                                                        val intent = Intent(context, Show_Post::class.java)
//                                                        intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
//                                                        intent.putExtra("post_id", post_Frag_adapter.getdata()[position].getId())
//                                                        intent.putExtra("positions", position)
//                                                        intent.putExtra(AppConstants.by_notification, 0)
//                                                        if (prefs.getInt(Constants.user, 0) === post_Frag_adapter.getdata()[position].userId) {
//                                                            intent.putExtra("post_by_me", 1)
//                                                        } else {
//                                                            intent.putExtra("post_by_me", 0)
//                                                        }
//                                                        intent.putExtra("by_activity", 0)
//                                                        intent.putExtra("content", contents as Serializable)
//                                                        startActivityForResult(intent, 1002)
                                            }
                                        }

                                    } else {
                                        hasLoadedAllItems = true

                                        binding.noPosts.visibility = View.VISIBLE
                                        binding.swipeContainer.isRefreshing = false
                                    }

                                    binding.swipeContainer.isRefreshing = false

                                    if (post_Frag_adapter.getdata().size != 0) {
                                        binding.noPosts.visibility = View.GONE
                                    } else {
                                        binding.noPosts.visibility = View.VISIBLE
                                    }

                                } else {
                                    binding.noPosts.visibility = View.VISIBLE
                                }
                                isAPICallRunning = false
                            }

                            override fun onError(e: Throwable) {
                                Log.v("onError: ", e.toString())
//                                hideProgressDialog()

                                isAPICallRunning = false
                                binding.swipeContainer.isRefreshing = false
                                binding.noPosts.visibility = View.VISIBLE
                            }

                            override fun onComplete() {
//                                hideProgressDialog()
                            }
                        })
                )
            } catch (e: Exception) {
                e.printStackTrace()

                isAPICallRunning = false
                e.printStackTrace()
                binding.noPosts.visibility = View.VISIBLE
            }
        }
    }

    private fun expandCollapse(post_id: Int) {
        if (sheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
            sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        else
            sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        //init paging
        if (paginateComment != null) {
            paginateComment!!.unbind()
        }

        paginateComment = Paginate.with(binding.layoutBottomSheet.recComments, object : Paginate.Callbacks {
            override fun onLoadMore() {
                isloadingComment = true

                val scrollPosition: Int = post_comment_adapter.dataList.size
                if (scrollPosition > 0) {
                    showLog("loadmore", scrollPosition.toString())
                    val currentSize = scrollPosition - 1

                    if (currentSize > 0) {
                        callAllPostComment(post_comment_adapter.dataList[currentSize].id, post_id.toString())
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

        //get all post comment
        callAllPostComment(0, postId = post_id.toString())
    }

    override fun onDestroy() {
        super.onDestroy()
        mCompositeDisposable.clear()
       // destroySocketListeners()
    }

    override fun onLoadMore() {
        isloading = true

        if (!isAPICallRunning) {
            val scrollPosition: Int = post_Frag_adapter.getdata().size
            if (scrollPosition > 0) {
                showLog("loadmore_comment", scrollPosition.toString())
                val currentSize = scrollPosition - 1

                if (currentSize > 0) {
                    get_all_post(post_Frag_adapter.getdata()[currentSize].id)
                }
            }
        }
    }

    override fun isLoading(): Boolean {
        return isloading
    }

    override fun hasLoadedAllItems(): Boolean {
        return hasLoadedAllItems
    }

    private fun callAPIPostLikeDislike(likeType: String, postId: String) {
        try {
            val hashMap: HashMap<String, Any> = hashMapOf(
                Constants.typeCode to likeType,
                Constants.post_id to postId
            )

//            openProgressDialog(activity)

            mCompositeDisposable.add(
                ApiClient.create()
                    .add_postlikeunlike(hashMap)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableObserver<ResponseGetPostLikeUnlike>() {
                        override fun onNext(responseGetPostLikeUnlike: ResponseGetPostLikeUnlike) {

                            if (responseGetPostLikeUnlike.success == 1) {
                                Log.v("GetPostLikeUnlike", "GetPostLikeUnlike")
//                                post_Frag_adapter.notifyDataSetChanged()
                            }
                        }

                        override fun onError(e: Throwable) {
                            Log.v("onError: ", e.toString())
//                            hideProgressDialog()
                        }

                        override fun onComplete() {
//                            hideProgressDialog()
                        }
                    })
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /*  override fun onDestroyView() {
          super.onDestroyView()
          destroySocketListeners()
      }*/


    private fun callAPIPostSave(saveType: String, postId: String) {
        try {
            val hashMap: HashMap<String, Any> = hashMapOf(
                Constants.typeCode to saveType,
                Constants.post_id to postId
            )

//            openProgressDialog(activity)

            mCompositeDisposable.add(
                ApiClient.create()
                    .postSaveUnsave(hashMap)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableObserver<CommonResponse>() {
                        override fun onNext(commonResponse: CommonResponse) {

                            if (commonResponse.success == 1) {
                                Log.v("callAPIPostSave", "callAPIPostSave")
                                post_Frag_adapter.notifyDataSetChanged()
                            }
                        }

                        override fun onError(e: Throwable) {
                            Log.v("onError: ", e.toString())
//                            hideProgressDialog()
                        }

                        override fun onComplete() {
//                            hideProgressDialog()
                        }
                    })
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initBottomSheet() {
        sheetBehavior = BottomSheetBehavior.from<LinearLayout>(binding.layoutBottomSheet.bottomSheet)

        sheetBehavior.setBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {

                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // React to dragging events
            }
        })

        sheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        //list
        var manager = GridLayoutManager(activity, 1, GridLayoutManager.VERTICAL, false)

        post_comment_adapter = Post_Comment_Adapter(requireActivity(), 0, ArrayList(), 0)

        binding.layoutBottomSheet.recComments.setHasFixedSize(true)
        binding.layoutBottomSheet.recComments.layoutManager = manager
        binding.layoutBottomSheet.recComments.adapter = post_comment_adapter
        binding.layoutBottomSheet.recComments.setOnTouchListener(OnTouchListener { v, event ->
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(v.windowToken, 0)
            false
        })

    }

    private fun callAllPostComment(offset: Int, postId: String) {
        try {
            val hashMap: HashMap<String, Any> = hashMapOf(
                Constants.post_id to "632",  //postId,
                Constants.offset to offset,
                Constants.limit to resources.getString(R.string.get_comments).toInt()
            )
            //openProgressDialog(activity)

            mCompositeDisposable.add(
                ApiClient.create()
                    .allpostcomment(hashMap)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableObserver<ResponsePostCommentDetails>() {
                        override fun onNext(responsePostCommentDetails: ResponsePostCommentDetails) {
                            if (responsePostCommentDetails.success == 1) {
                                Log.v("responsePostComment", "responsePostCommentDetails")

                                if (responsePostCommentDetails.success == 1) {

                                    if (responsePostCommentDetails.data.isNotEmpty()) {
                                        post_comment_adapter.setdata(responsePostCommentDetails.data as ArrayList<ResponsePostCommentDetails.Data>)
                                        isloading = false
                                        hasLoadedAllItems = false
                                    } else {
                                        isloading = true
                                        hasLoadedAllItems = true
                                    }

                                } else {
                                    hasLoadedAllItems = true
                                }
                            }
                        }

                        override fun onError(e: Throwable) {
                            Log.v("onError: ", e.toString())
                            //hideProgressDialog()
                        }

                        override fun onComplete() {
                            //hideProgressDialog()
                        }
                    })
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun callAPIPostComment(commentText: String, postId: String, position: Int) {
        try {
            val hashMap: HashMap<String, Any> = hashMapOf(
                Constants.post_id to postId,
                Constants.comment_text to commentText
            )

            openProgressDialog(activity)

            mCompositeDisposable.add(
                ApiClient.create()
                    .post_comment(hashMap)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableObserver<ResponsePostComment>() {
                        override fun onNext(responsePostComment: ResponsePostComment) {

                            if (responsePostComment.success == 1) {
                                Log.v("callAPIPostComment", "callAPIPostComment")
                                post_Frag_adapter.updateComment(responsePostComment.data, position)
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

    private fun showPostDialog() {
        DialogUtils().showConfirmationIOSDialog(activity as AppCompatActivity, "", "", object : DialogUtils.DialogCallbacks {
            override fun onPositiveButtonClick() {

            }

            override fun onNegativeButtonClick() {

            }

            override fun onDefaultButtonClick(actionName: String) {
                when (actionName) {
                    DialogUtils.PostDialogActionName.REPORT.value -> {

                    }
                }
            }
        })
    }

  /*  private fun initSocketListeners() {
        socket.on(Constants.post_like_response, onPostLikeResponse)
    }

    private fun destroySocketListeners() {
        socket.off(Constants.post_like_response, onPostLikeResponse)
    }*/
/*
    private val onPostLikeResponse = Emitter.Listener { args ->
        val data = args[0] as String
        Log.e("onPostLikeResponse", args.toString())
        *//*activity?.runOnUiThread(Runnable {

            val data = args[0] as String
            if (data != null) {
                val postSocket = Gson().fromJson(data, PostSocket::class.java)
                if (fastSave.getInt(Constants.prefUserId, 0) != postSocket.user_id
                    && post_id == postSocket.post_id
                ) {
                    //post_data_count(postSocket.getPost_id())
                }
            }
        })*//*
    }*/

    fun postLikeDislike() {
        Log.e("onPostLikeResponse", "postLikeDislike")
    }
}