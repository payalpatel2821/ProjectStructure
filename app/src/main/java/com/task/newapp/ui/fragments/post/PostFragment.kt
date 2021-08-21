package com.task.newapp.ui.fragments.post

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.Socket
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.luck.picture.lib.instagram.InsGallery
import com.paginate.Paginate
import com.task.newapp.App
import com.task.newapp.App.Companion.fastSave
import com.task.newapp.R
import com.task.newapp.adapter.post.PostCommentAdapter
import com.task.newapp.adapter.post.PostFragAdapter
import com.task.newapp.api.ApiClient
import com.task.newapp.databinding.FragmentPostBinding
import com.task.newapp.models.CommonResponse
import com.task.newapp.models.post.PostSocket
import com.task.newapp.models.post.ResponseGetAllPost
import com.task.newapp.models.post.ResponseGetAllPost.All_Post_Data
import com.task.newapp.models.post.ResponseGetPostLikeUnlike
import com.task.newapp.models.post.ResponsePostComment
import com.task.newapp.service.FileUploadService
import com.task.newapp.ui.activities.post.ShowPostActivity
import com.task.newapp.utils.*
import com.task.newapp.utils.instapicker.GlideCacheEngine
import com.task.newapp.utils.instapicker.GlideEngine
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import lv.chi.photopicker.MediaPickerFragment
import java.util.*
import kotlin.collections.ArrayList


class PostFragment : Fragment(), View.OnClickListener, Paginate.Callbacks, MediaPickerFragment.Callback,
    ThoughtFragment.OnPostDoneClickListener, FileUploadService.OnPostDoneClickListenerService, PostCommentListFragment.OnChangePostItem {

    companion object {
        lateinit var instance: PostFragment
    }

    var myBottomSheetMoment: MomentsFragment? = null
    var myBottomSheetThought: ThoughtFragment? = null
    lateinit var myBottomSheetPostCommentListFragment: PostCommentListFragment

    private var onHideShowBottomSheet: OnHideShowBottomSheet? = null

    var selectionType = ""
    var isreplybox: Boolean = false
    private lateinit var binding: FragmentPostBinding
    var act: FragmentActivity? = null
    private var isAPICallRunning = false
    var linearLayoutManager: LinearLayoutManager? = null
    private val mCompositeDisposable = CompositeDisposable()
    lateinit var postFragAdapter: PostFragAdapter
    lateinit var postCommentAdapter: PostCommentAdapter
    lateinit var socket: Socket

    var isloading = false
    var isloadingComment = false
    var hasLoadedAllItems = false
    var hasLoadedAllItemsComment = false
    private var paginate: Paginate? = null
    private var paginateComment: Paginate? = null
    var post_id: Int = 0
    var user_id: Int = 0
    private lateinit var sheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var allPostArrayList: ArrayList<All_Post_Data>
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
        allPostArrayList = ArrayList<All_Post_Data>()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        initView()
//        initBottomSheet()
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
//        initSocketListeners()
        binding.llMomentsPhotos.setOnClickListener(this)
        binding.llMomentsVideos.setOnClickListener(this)
        binding.llThoughts.setOnClickListener(this)

        binding.recPost.setHasFixedSize(true)
        binding.recPost.setItemViewCacheSize(20)
        binding.recPost.isDrawingCacheEnabled = true
        binding.recPost.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH

        binding.recPost.isFocusable = false
        postFragAdapter = PostFragAdapter(requireActivity(), ArrayList())
        linearLayoutManager = LinearLayoutManager(activity)
        linearLayoutManager!!.isAutoMeasureEnabled = false
        binding.recPost.layoutManager = linearLayoutManager
        binding.recPost.adapter = postFragAdapter

        binding.swipeContainer.setOnRefreshListener {
            binding.swipeContainer.isRefreshing = true
            if (!isAPICallRunning) {
                showLog("initView", "get_all_post")
                getAllPost(0)
            }
        }
        getAllPost(0)

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
        registerReceiver()
    }

    private fun setProfilePic() {
        Glide.with(requireActivity()).load(fastSave.getString(Constants.profile_image, ""))
            .error(R.drawable.default_dp).into(binding.imgUser)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.ll_moments_photos -> {
//                selectionType = MediaPickerFragment.PickerType.PHOTO.name
//                openPicker(MediaPickerFragment.PickerType.PHOTO, Constants.MAX_IMAGE_COUNT)

                myBottomSheetMoment = MomentsFragment().newInstance(ArrayList(), selectionType)
//            myBottomSheetMoment.setListener(this)
                myBottomSheetMoment!!.show(childFragmentManager, myBottomSheetMoment!!.tag)

            }
            R.id.ll_moments_videos -> {
//                val ft: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
//                val fragmentDemo: MomentsFragment = MomentsFragment.newInstance(5, "my title")
//                ft.replace(R.id.your_placeholder, fragmentDemo)
//                ft.commit()

//                selectionType = MediaPickerFragment.PickerType.VIDEO.name
//                openPicker(MediaPickerFragment.PickerType.VIDEO, Constants.MAX_VIDEO_COUNT)

                myBottomSheetMoment = MomentsFragment().newInstance(ArrayList(), selectionType)
//            myBottomSheetMoment.setListener(this)
                myBottomSheetMoment!!.show(childFragmentManager, myBottomSheetMoment!!.tag)
            }
            R.id.ll_thoughts -> {
                myBottomSheetThought = ThoughtFragment().newInstance(post_id.toString())
                myBottomSheetThought!!.setListener(this)
                myBottomSheetThought!!.show(childFragmentManager, myBottomSheetThought!!.tag)
            }

        }
    }

    fun getAllPost(currentpos: Int) {
        isAPICallRunning = true
        if (activity != null) {
            try {
                openProgressDialog(activity)

                mCompositeDisposable.add(
                    ApiClient.create()
                        .get_all_posts(resources.getString(R.string.get_all_post).toInt(), currentpos)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableObserver<ResponseGetAllPost>() {
                            override fun onNext(responseGetAllPost: ResponseGetAllPost) {
                                Log.v("onNext: ", responseGetAllPost.toString())
//                                act!!.showToast(responseGetAllPost.message)

                                if (responseGetAllPost != null) {
                                    if (responseGetAllPost.success == 1) {

                                        if (responseGetAllPost.data.isNotEmpty()) {

                                            if (binding.swipeContainer.isRefreshing) {
                                                allPostArrayList.clear()
                                                allPostArrayList = ArrayList()
                                            }
                                            if (currentpos == 0) {
                                                allPostArrayList.clear()
                                                allPostArrayList = ArrayList()
                                            }

//                                            allPostArrayList = responseGetAllPost.data as ArrayList<All_Post_Data>
                                            allPostArrayList.addAll(responseGetAllPost.data as ArrayList<All_Post_Data>)

                                            postFragAdapter.setdata(allPostArrayList, currentpos == 0)
                                            isloading = false
                                            hasLoadedAllItems = false

                                            if (currentpos == 0) {
                                                binding.recPost.scrollToPosition(0)
                                            }

                                        } else {
                                            isloading = true
                                            hasLoadedAllItems = true
                                        }

                                        postFragAdapter.onItemClick = { view, position, comment ->
                                            if (postFragAdapter.getdata().isNotEmpty()) {

                                                val allPostData = postFragAdapter.getdata()[position]
                                                post_id = allPostData.id
                                                user_id = allPostData.userId

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

                                                                    postFragAdapter.updateLikesCount(count, position, 1)
                                                                }
                                                            }
                                                            1 -> {
                                                                likeType = "unlike"
                                                                imgLike.setImageResource(R.drawable.ic_not_like)

                                                                if (allPostData.likesCount > 0) {
                                                                    val count = allPostData.likesCount - 1

                                                                    postFragAdapter.updateLikesCount(count, position, 0)
                                                                }
                                                            }
                                                        }

                                                        Log.e("onNext: ", Gson().toJson(postSocket))
                                                        socket.emit(SocketConstant.post_like, Gson().toJson(postSocket))

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

                                                                postFragAdapter.updateSave(1, position)
                                                            }
                                                            1 -> {
                                                                saveType = "unsave"
                                                                imgSave.setImageResource(R.drawable.ic_nonsave)

                                                                postFragAdapter.updateSave(0, position)
                                                            }
                                                        }

                                                        //Call API for save
                                                        callAPIPostSave(saveType = saveType, postId = post_id.toString())
                                                    }

                                                    R.id.txtPost -> {
//                                                        val imgSave: ImageView = view as ImageView

//                                                        val itemView = binding.recPost.findViewHolderForAdapterPosition(position)!!.itemView
//                                                        var edt_comment: AppCompatEditText = itemView.findViewById(R.id.edt_comment) as EditText

//                                                        var comment = (binding.recPost.getChildAt(position) as AppCompatEditText).text.toString()

//                                                        var comment = post_Frag_adapter.getCommentsList()[position]

//                                                        if (edt_comment.text.toString().isNotEmpty()) {
                                                        if (comment.isNotEmpty()) {
                                                            //Call API for add comment
//                                                            callAPIPostComment(commentText = edt_comment.text.toString(), postId = post_id.toString(), position)
                                                            callAPIPostComment(commentText = comment, postId = post_id.toString(), position)
                                                        }
                                                    }

                                                    R.id.llComment -> openCommentBottomSheet(position)

                                                    R.id.more_iv -> showPostDialog()

                                                    R.id.rlMain -> showPostDetails(position)
                                                }

                                                //val contents: List<All_Post_contents?> = post_Frag_adapter.getdata()[position].contents
                                                // val contents: List<All_Post_Data.Content?> = post_Frag_adapter.getdata()[position].contents
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
                                        binding.recPost.visibility = View.GONE
                                        binding.swipeContainer.isRefreshing = false
                                    }

                                    binding.swipeContainer.isRefreshing = false

                                    if (postFragAdapter.getdata().size != 0) {
                                        binding.noPosts.visibility = View.GONE
                                        binding.recPost.visibility = View.VISIBLE
                                    } else {
                                        binding.noPosts.visibility = View.VISIBLE
                                        binding.recPost.visibility = View.GONE
                                    }

                                } else {
                                    binding.noPosts.visibility = View.VISIBLE
                                    binding.recPost.visibility = View.GONE
                                }
                                isAPICallRunning = false
                            }

                            override fun onError(e: Throwable) {
                                Log.v("onError: ", e.toString())
//                                hideProgressDialog()

                                isAPICallRunning = false
                                binding.swipeContainer.isRefreshing = false
                                binding.noPosts.visibility = View.VISIBLE
                                binding.recPost.visibility = View.GONE
                            }

                            override fun onComplete() {
                                hideProgressDialog()
                            }
                        })
                )
            } catch (e: Exception) {
                e.printStackTrace()

                isAPICallRunning = false
                e.printStackTrace()
                binding.noPosts.visibility = View.VISIBLE
                binding.noPosts.visibility = View.GONE

                hideProgressDialog()
            }
        }
    }

    private fun openCommentBottomSheet(position: Int) {
        try {
            myBottomSheetPostCommentListFragment = PostCommentListFragment().newInstance(post_id, user_id, position)
            myBottomSheetPostCommentListFragment.setListener(this)
            myBottomSheetPostCommentListFragment.show(childFragmentManager, myBottomSheetPostCommentListFragment.tag)
        } catch (e: Exception) {
            e.printStackTrace()
        }

//        expandCommentSheet()
//        binding.layoutBottomSheet.edtComment.text = ""
//
//        postCommentAdapter = PostCommentAdapter(activity as AppCompatActivity, post_id, ArrayList(), 0)
//        binding.layoutBottomSheet.recComments.adapter = postCommentAdapter
//
//        //init paging
//        if (paginateComment != null) {
//            paginateComment!!.unbind()
//        }
//
//        paginateComment = Paginate.with(binding.layoutBottomSheet.recComments, object : Paginate.Callbacks {
//            override fun onLoadMore() {
//                isloadingComment = true
//
//                val scrollPosition: Int = postCommentAdapter.allPostCommentDataList.size
//                if (scrollPosition > 0) {
//                    showLog("loadmore", scrollPosition.toString())
//                    val currentSize = scrollPosition - 1
//
//                    if (currentSize > 0) {
//                        callAllPostComment(postCommentAdapter.allPostCommentDataList[currentSize].id, post_id.toString())
//                    }
//                }
//            }
//
//            override fun isLoading(): Boolean {
//                return isloadingComment
//            }
//
//            override fun hasLoadedAllItems(): Boolean {
//                return hasLoadedAllItemsComment
//            }
//
//        })
//            .setLoadingTriggerThreshold(17)
//            .addLoadingListItem(false)
//            .setLoadingListItemCreator(null)
//            .build()
//
//        callAllPostComment(0, postId = post_id.toString())
    }

    override fun onDestroy() {
        super.onDestroy()
        mCompositeDisposable.clear()
        if (serviceBroadRequestReceiver != null && serviceBroadRequestReceiver.isOrderedBroadcast) {
            activity?.unregisterReceiver(serviceBroadRequestReceiver)
        }
    }

    override fun onLoadMore() {
        isloading = true

        if (!isAPICallRunning) {
            val scrollPosition: Int = postFragAdapter.getdata().size
            if (scrollPosition > 0) {
                showLog("loadmore_comment", scrollPosition.toString())
                val currentSize = scrollPosition - 1

                if (currentSize > 0) {
                    getAllPost(postFragAdapter.getdata()[currentSize].id)
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

    override fun onDestroyView() {
        super.onDestroyView()
//        destroySocketListeners()
    }

    private val onPostLikeResponse = Emitter.Listener { args ->
        activity?.runOnUiThread(Runnable {
            Log.e("onPostLikeResponse", args.toString())
            val data = args[0] as String
            if (data != null) {
                val postSocket = Gson().fromJson(data, PostSocket::class.java)
                if (fastSave.getInt(Constants.prefUserId, 0) != postSocket.user_id
                    && post_id == postSocket.post_id
                ) {
                    //post_data_count(postSocket.getPost_id())
                }
            }
        })
    }

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

//    private fun initBottomSheet() {
//        sheetBehavior = BottomSheetBehavior.from<LinearLayout>(binding.layoutBottomSheet.bottomSheet)
//
//        sheetBehavior.setBottomSheetCallback(object : BottomSheetCallback() {
//            override fun onStateChanged(bottomSheet: View, newState: Int) {
//                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
//                    onHideShowBottomSheet?.hideShowBottomSheet(View.GONE)
//                } else if (newState == BottomSheetBehavior.STATE_HIDDEN) {
//                    onHideShowBottomSheet?.hideShowBottomSheet(View.VISIBLE)
//                }
//            }
//
//            override fun onSlide(bottomSheet: View, slideOffset: Float) {
//                // React to dragging events
//            }
//        })
//
//        sheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
//
//        //list
//        var manager = GridLayoutManager(activity, 1, GridLayoutManager.VERTICAL, false)
//        postCommentAdapter = PostCommentAdapter(activity as AppCompatActivity, post_id, ArrayList(), 0)
//
//        binding.layoutBottomSheet.recComments.setHasFixedSize(true)
//        binding.layoutBottomSheet.recComments.layoutManager = manager
//        binding.layoutBottomSheet.recComments.adapter = postCommentAdapter
//        binding.layoutBottomSheet.recComments.setOnTouchListener(OnTouchListener { v, event ->
//            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//            imm.hideSoftInputFromWindow(v.windowToken, 0)
//            false
//        })
//
//        binding.layoutBottomSheet.txtPost.setOnClickListener {
//            if (binding.layoutBottomSheet.edtComment.text.toString().isNotEmpty()) {
//                callAPIAddPostComment(binding.layoutBottomSheet.edtComment.text.toString().trim())
//            }
//        }
//
//    }

//    private fun callAllPostComment(offset: Int, postId: String) {
//        try {
//            binding.layoutBottomSheet.recComments.visibility = View.VISIBLE
//            binding.layoutBottomSheet.imgNoComments.visibility = View.GONE
//
//            val hashMap: HashMap<String, Any> = hashMapOf(
//                Constants.post_id to postId,
//                Constants.offset to offset,
//                Constants.limit to resources.getString(R.string.get_comments).toInt()
//            )
//            openProgressDialog(activity)
//
//            mCompositeDisposable.add(
//                ApiClient.create()
//                    .allpostcomment(hashMap)
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribeWith(object : DisposableObserver<ResponseGetAllPostComments>() {
//                        override fun onNext(responseGetAllPostComments: ResponseGetAllPostComments) {
//                            if (responseGetAllPostComments.success == 1) {
//                                Log.v("responsePostComment", "responsePostCommentDetails")
//
//                                if (responseGetAllPostComments.success == 1) {
//
//                                    if (responseGetAllPostComments.data.isNotEmpty()) {
//
//                                        binding.layoutBottomSheet.recComments.visibility = View.VISIBLE
//                                        binding.layoutBottomSheet.imgNoComments.visibility = View.GONE
//
//                                        var postByMe = 0
//                                        postByMe = if (fastSave.getInt(Constants.prefUserId, 0) == user_id) {
//                                            1
//                                        } else {
//                                            0
//                                        }
//
////                                        postCommentAdapter.setData(
////                                            responseGetAllPostComments.data as ArrayList<AllPostCommentData>, postByMe
////                                        )
//
//                                        postCommentAdapter = PostCommentAdapter(
//                                            activity as AppCompatActivity, post_id,
//                                            responseGetAllPostComments.data as ArrayList<AllPostCommentData>, postByMe
//                                        )
//
//                                        binding.layoutBottomSheet.recComments.adapter = postCommentAdapter
//
//                                        isloading = false
//                                        hasLoadedAllItems = false
//                                    } else {
//                                        isloading = true
//                                        hasLoadedAllItems = true
//
//                                        binding.layoutBottomSheet.recComments.visibility = View.GONE
//                                        binding.layoutBottomSheet.imgNoComments.visibility = View.VISIBLE
//                                    }
//
//                                } else {
//                                    hasLoadedAllItems = true
//                                }
//                            }
//                        }
//
//                        override fun onError(e: Throwable) {
//                            Log.v("onError: ", e.toString())
//                            hideProgressDialog()
//                        }
//
//                        override fun onComplete() {
//                            hideProgressDialog()
//                        }
//                    })
//            )
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }

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
                                postFragAdapter.updateComment(responsePostComment.data, position)
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

    private fun openPicker(type: MediaPickerFragment.PickerType, count: Int) {
        MediaPickerFragment.newInstance(
            multiple = true,
            allowCamera = false,
            allowGallery = false,
            pickerType = type,
            maxSelection = count,
            theme = R.style.ChiliPhotoPicker_Light,
        ).show(childFragmentManager, "picker")
    }

    override fun onMediaPicked(mediaItems: ArrayList<Uri>) {
        showLog("onMediaPicked", mediaItems.joinToString(separator = "\n") { it.toString() })

        //Open and Show in Bottom Sheet
        if (mediaItems.size > 0) {
            myBottomSheetMoment = MomentsFragment().newInstance(mediaItems, selectionType)
//            myBottomSheetMoment.setListener(this)
            myBottomSheetMoment!!.show(childFragmentManager, myBottomSheetMoment!!.tag)
        }

    }

    override fun onPostClickService() {
        showLog("onPostClickService", "Refresh_Post_List")
        //Refresh Post List
        mCompositeDisposable?.clear()
        getAllPost(0)
    }

    override fun onPostClick() {
        showLog("onPostClick", "Refresh_Post_List")
        //Refresh Post List
        mCompositeDisposable?.clear()
        getAllPost(0)
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

    override fun onPause() {
        super.onPause()
        if (serviceBroadRequestReceiver != null && serviceBroadRequestReceiver.isOrderedBroadcast) {
            activity?.unregisterReceiver(serviceBroadRequestReceiver)
        }
    }

    var isRegister = false

    override fun onStop() {
        super.onStop()
        if (isRegister && serviceBroadRequestReceiver != null && serviceBroadRequestReceiver.isOrderedBroadcast) {
            activity?.unregisterReceiver(serviceBroadRequestReceiver)
            isRegister = false
        }
    }

    private val serviceBroadRequestReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Constants.INTENT_SERVICE_PROGRESS) {
                //Show post process
                showLog("BroadcastReceiver", "INTENT_SERVICE_PROGRESS")

                binding.progressLay.visibility = View.VISIBLE
                binding.recPost.smoothScrollToPosition(0)

            } else if (intent.action == Constants.INTENT_SERVICE_COMPLETE) {
                //Complete post process
                showLog("BroadcastReceiver", "INTENT_SERVICE_COMPLETE")

                //Hide Progress
                binding.progressLay.visibility = View.GONE
                binding.recPost.smoothScrollToPosition(0)

                //Refresh Post List
                mCompositeDisposable?.clear()
                getAllPost(0)
            }
        }
    }

    private fun registerReceiver() {
        val filter = IntentFilter()
        filter.addAction(Constants.INTENT_SERVICE_PROGRESS)
        filter.addAction(Constants.INTENT_SERVICE_COMPLETE)

        isRegister = true
        activity?.registerReceiver(serviceBroadRequestReceiver, filter)
    }

    interface OnHideShowBottomSheet {
        fun hideShowBottomSheet(visibility: Int)
    }

    fun setOnHideShowBottomSheet(listener: OnHideShowBottomSheet) {
        this.onHideShowBottomSheet = listener
    }

//    private fun callAPIAddPostComment(commentText: String) {
//        try {
//            val hashMap: HashMap<String, Any> = hashMapOf(
//                Constants.comment_id to 0,
//                Constants.is_comment_reply to 0,
//                Constants.type to "comment",
//                Constants.comment_text to commentText,
//                Constants.post_id to post_id
//            )
//
//            openProgressDialog(activity)
//
//            mCompositeDisposable.add(
//                ApiClient.create()
//                    .add_postcomment(hashMap)
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribeWith(object : DisposableObserver<ResponseAddPostComment>() {
//                        override fun onNext(responseAddPostComment: ResponseAddPostComment) {
//
//                            if (responseAddPostComment.success == 1) {
////                                activity?.showToast(responseAddPostComment.message)
//
//                                binding.layoutBottomSheet.edtComment.setText("")
//
//                                postCommentAdapter?.let {
//
//                                    responseAddPostComment?.data.run {
//                                        var postByMe = 0
//                                        postByMe = if (fastSave.getInt(Constants.prefUserId, 0) == user_id) {
//                                            1
//                                        } else {
//                                            0
//                                        }
//
//                                        val c = Calendar.getInstance()
//                                        val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
//                                        val formattedDate = df.format(c.time)
//
//                                        val tempArrayList: ArrayList<AllPostCommentData> = ArrayList<AllPostCommentData>()
//                                        val prefUser = fastSave.getObject(Constants.prefUser, User::class.java)
//
//                                        val commentUser = AllPostCommentData.CommentUser(
//                                            id = prefUser.id,
//                                            profileImage = prefUser.profileImage,
//                                            firstName = prefUser.firstName,
//                                            lastName = prefUser.lastName
//                                        )
//
//                                        var postDataModel = AllPostCommentData(
//                                            id = this!!.id,
//                                            postId = this!!.post_id!!.toInt(),
//                                            commentText = comment_text.toString(),
//                                            user = commentUser,
//                                            updatedAt = formattedDate,
//                                        )
//
//                                        tempArrayList.add(postDataModel)
//                                        tempArrayList.addAll(postCommentAdapter.getData())
//
//                                        postCommentAdapter.setData(allPostCommentDataList = tempArrayList, post_by_me = postByMe)
//                                        binding.layoutBottomSheet.recComments.smoothScrollToPosition(0)
//                                    }
//                                }
//                            }
//                        }
//
//                        override fun onError(e: Throwable) {
//                            Log.v("onError: ", e.toString())
//                            hideProgressDialog()
//                        }
//
//                        override fun onComplete() {
//                            hideProgressDialog()
//                        }
//                    })
//            )
//        } catch (e: Exception) {
//            e.printStackTrace()
//            hideProgressDialog()
//        }
//    }


//    fun isInitializedMoment(): Boolean {
//        myBottomSheetMoment?.let {
//            return this::myBottomSheetMoment.isInitialized
//        }
//        return false
//    }
//
//    fun isInitializedThought(): Boolean {
//        myBottomSheetThought?.let {
//            return this::myBottomSheetThought.isInitialized
//        }
//        return false
//    }

    fun expandCommentSheet() {
        if (this::myBottomSheetPostCommentListFragment.isInitialized)
            myBottomSheetPostCommentListFragment.dismiss()
    }

    override fun onResume() {
        super.onResume()
        showLog("onResume", "PostFragment")
    }

    override fun onChangePostItem(lastComment: String, totalComments: Int, adapterPositionPost: Int) {
        postFragAdapter?.let {

            var dataComment = ResponsePostComment.Data(
                commentText = lastComment,
                totalComment = totalComments,
            )
            postFragAdapter.updateComment(dataComment, adapterPositionPost)
        }
    }

    private fun showPostDetails(position: Int) {
        try {
            if (postFragAdapter.getdata()[position].postContents[0].type == "thought") {
                //Show Thought


            } else {
                //Show Photo and Video

                requireActivity().launchActivity<ShowPostActivity> {
                    putExtra("postId", post_id)
                    putExtra("position", position)

                    var postByMe = 0
                    postByMe = if (fastSave.getInt(Constants.prefUserId, 0) == user_id) {
                        1
                    } else {
                        0
                    }
                    putExtra("postByMe", postByMe)
                    putExtra("content", Gson().toJson(postFragAdapter.getdata()[position].postContents))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}