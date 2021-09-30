@file:Suppress("INACCESSIBLE_TYPE")

package com.task.newapp.ui.fragments.post

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.util.Log
import android.util.Size
import android.view.*
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.text.toSpannable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.appizona.yehiahd.fastsave.FastSave
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.instagram.InsGallery
import com.luck.picture.lib.instagram.InstagramPreviewContainer
import com.luck.picture.lib.instagram.PictureSelectorInstagramStyleActivity
import com.luck.picture.lib.listener.OnResultCallbackListener
import com.luck.picture.lib.permissions.PermissionChecker
import com.luck.picture.lib.tools.PictureFileUtils
import com.task.newapp.utils.mentionlib.utils.QueryListener
import com.percolate.mentions.sample.adapters.RecyclerItemClickListener
import com.task.newapp.utils.mentionlib.adapters.UsersAdapter
import com.task.newapp.utils.mentionlib.models.Mention
import com.task.newapp.utils.mentionlib.utils.MentionsLoaderUtils
import com.task.newapp.R
import com.task.newapp.adapter.post.CreatePostAdapter
import com.task.newapp.api.ApiClient
import com.task.newapp.databinding.FragmentMomentzBinding
import com.task.newapp.models.post.ResponseFriendsList
import com.task.newapp.service.FileUploadService
import com.task.newapp.ui.activities.chat.ViewPagerActivity
import com.task.newapp.utils.*
import com.task.newapp.utils.instapicker.GlideCacheEngine
import com.task.newapp.utils.instapicker.GlideEngine
import com.task.newapp.utils.mentionlib.adapters.utils.SuggestionsListener
import com.task.newapp.utils.mentionlib.utils.Mentionable
import com.task.newapp.utils.mentionlib.utils.Mentions
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import lv.chi.photopicker.MediaPickerFragment
import okhttp3.MultipartBody
import java.io.File
import java.lang.ref.WeakReference
import java.lang.reflect.Type
import java.util.HashMap
import kotlin.collections.ArrayList


class MomentsFragment : BottomSheetDialogFragment(), View.OnClickListener, //MediaPickerFragment.Callback,
    PostTagFriendListFragment.OnPostTagDoneClickListener,
    QueryListener, SuggestionsListener /*Mention*/ {

    var select_captions = ArrayList<String>()
    var select_time = ArrayList<String>()
    var targetList = ArrayList<String>()
    var return_mediatype = ArrayList<Int>()

    private lateinit var mediaItemsArray: ArrayList<Uri>
    private lateinit var binding: FragmentMomentzBinding
    private lateinit var activity: Activity
    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var createPostAdapter: CreatePostAdapter
    private lateinit var selectionType: String
    var currentCountVideo: Int = 0
    var currentCountPhoto: Int = 0
    lateinit var clickType: MediaPickerFragment.PickerType
    private val mCompositeDisposable = CompositeDisposable()
    private lateinit var arrayListMedia: ArrayList<LocalMedia>
    lateinit var myBottomSheetTagFriendListFragment: PostTagFriendListFragment
    private var commaSeperatedIds: String = ""
    var maxSelectNum: Int = 9

    //Add New
//    private var gridImageAdapter: GridImageAdapter? = null

    //------------------------------------Mention-------------------------------------------
    lateinit var dialogMention: Dialog
    lateinit var popupWindow: PopupWindow
    lateinit var mentionsEmptyView: TextView
    lateinit var mentions_list_layout: FrameLayout
    lateinit var mentions_list: RecyclerView
    var jsonArrayMention = JsonArray()

    /**
     * Mention object provided by library to configure at mentions.
     */
    private var mentions: Mentions? = null

    /**
     * Adapter to display suggestions.
     */
    private var usersAdapter: UsersAdapter? = null

    /**
     * Utility class to load from a JSON file.
     */
    private var mentionsLoaderUtils: MentionsLoaderUtils? = null


    fun newInstance(mediaItems: ArrayList<Uri>, selection: String): MomentsFragment {
        val f = MomentsFragment()
        val bundle = Bundle()
        bundle.putParcelableArrayList("mediaItems", mediaItems)
        bundle.putString("selection", selection)
        f.arguments = bundle
        Log.e("mediaItems: ", mediaItems.toString())
        return f
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        mediaItemsArray = requireArguments().getParcelableArrayList("mediaItems")!!
//        selectionType = requireArguments().getString("selection")!!
//
//        if (this.selectionType == MediaPickerFragment.PickerType.PHOTO.name) {
//            currentCountPhoto = mediaItemsArray.size
//        } else {
//            currentCountVideo = mediaItemsArray.size
//        }
//
        arrayListMedia = ArrayList()
        allfriendList = ArrayList()
//        mediaItemsArray.forEachIndexed { index, uri ->
//
//            var postUriModel = Post_Uri_Model(
//                mediaItemsArray[index].path.toString(),
//                if (isImageFile(mediaItemsArray[index].path)) "image" else "video"
//            )
//            postUriModel?.let { arrayListMedia.add(postUriModel) }
//        }
    }

    private val onAddPicClickListener = object : CreatePostAdapter.onAddPicClickListener {
        override fun onAddPicClick() {
            openGallery()
        }
    }

    private fun openGallery() {
        InsGallery.setCurrentTheme(InsGallery.THEME_STYLE_DEFAULT)

        Log.e("openGallery: ", createPostAdapter!!.getData()!!.size.toString())

        InsGallery.openGallery(
            requireActivity(), GlideEngine.createGlideEngine(), GlideCacheEngine.createCacheEngine(), createPostAdapter!!.getData(),
//            OnResultCallbackListenerImpl(createPostAdapter!!)
            object : OnResultCallbackListener<LocalMedia> {
                override fun onResult(result: List<LocalMedia>) {
                    for (media in result) {
                        Log.i("MomentsFragment", "Whether to compress:" + media.isCompressed)
                        Log.i("MomentsFragment", "Compression:" + media.compressPath)
                        Log.i("MomentsFragment", "Original image:" + media.path)
                        Log.i("MomentsFragment", "Whether to cut:" + media.isCut)
                        Log.i("MomentsFragment", "Cut:" + media.cutPath)
                        Log.i("MomentsFragment", "Whether to open the original image:" + media.isOriginal)
                        Log.i("MomentsFragment", "Original image path:" + media.originalPath)
                        Log.i("MomentsFragment", "Android Q unique Path:" + media.androidQToPath)
                        Log.i("MomentsFragment", "Size: " + media.size)
                    }
//                    val adapter = createPostAdapter.get()
                    if (createPostAdapter != null) {
                        createPostAdapter.setList(result as ArrayList<LocalMedia>)
                        createPostAdapter.notifyDataSetChanged()
                        spanRecyclerView(createPostAdapter.getData())
                    }
                }

                override fun onCancel() {
                    Log.i("MomentsFragment", "PictureSelector Cancel")
                }

            }
        )
//            InsGallery.openGallery(requireActivity(), GlideEngine.createGlideEngine(), GlideCacheEngine.createCacheEngine(), mAdapter!!.getData())
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_momentz, container, false)
        this.activity = requireActivity()
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        clearCache()
        initView()
    }

    private fun initView() {
        binding.imgBack.setOnClickListener(this)
        binding.txtAddPhoto.setOnClickListener(this)
        binding.txtAddVideo.setOnClickListener(this)
        binding.fabPost.setOnClickListener(this)
        binding.llTagFriend.setOnClickListener(this)

        binding.rvPhotoVideo.setHasFixedSize(true)
        binding.rvPhotoVideo.isFocusable = false
        binding.rvPhotoVideo.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL))
        binding.rvPhotoVideo.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

//        createPostAdapter = CreatePostAdapter(requireContext(), arrayListMedia)
        gridLayoutManager = GridLayoutManager(activity, 6)
        binding.rvPhotoVideo.layoutManager = gridLayoutManager
//        binding.rvPhotoVideo.adapter = createPostAdapter

        //spanRecyclerView()

        //Add New
//        gridImageAdapter = GridImageAdapter(context, onAddPicClickListener)
        createPostAdapter = CreatePostAdapter(requireContext(), onAddPicClickListener)

        itemClick()

        // Test Arabic changes
//        List<LocalMedia> list = new ArrayList<>();
//        LocalMedia m = new LocalMedia();
//        m.setPath("https://wx1.sinaimg.cn/mw690/006e0i7xly1gaxqq5m7t8j31311g2ao6.jpg");
//        LocalMedia m1 = new LocalMedia();
//        m1.setPath("https://ww1.sinaimg.cn/bmiddle/bcd10523ly1g96mg4sfhag20c806wu0x.gif");
//        list.add(m);
//        list.add(m1);
//        mAdapter.setList(list);

        createPostAdapter!!.setSelectMax(maxSelectNum)
        binding.rvPhotoVideo.adapter = createPostAdapter

//        mAdapter!!.setOnItemClickListener { v, position ->
//            val selectList = mAdapter!!.getData()
//            if (selectList!!.isNotEmpty()) {
//                val media = selectList!![position]
//                val mimeType = media!!.mimeType
//                val mediaType = PictureMimeType.getMimeType(mimeType)
//                when (mediaType) {
//                    PictureConfig.TYPE_VIDEO ->                         // 预览视频
//                        PictureSelector.create(requireActivity())
//                            .themeStyle(R.style.picture_WeChat_style)
//                            .externalPictureVideo(media!!.path)
//                    PictureConfig.TYPE_AUDIO ->                         // 预览音频
//                        PictureSelector.create(requireActivity())
//                            .externalPictureAudio(if (PictureMimeType.isContent(media!!.path)) media!!.androidQToPath else media!!.path)
//                    else ->                         // 预览图片 可自定长按保存路径
////                        PictureWindowAnimationStyle animationStyle = new PictureWindowAnimationStyle();
////                        animationStyle.activityPreviewEnterAnimation = R.anim.picture_anim_up_in;
////                        animationStyle.activityPreviewExitAnimation = R.anim.picture_anim_down_out;
//                        PictureSelector.create(requireActivity())
//                            .themeStyle(R.style.picture_WeChat_style) // xml设置主题
//                            //.setPictureWindowAnimationStyle(animationStyle)// 自定义页面启动动画
////                            .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) // 设置相册Activity方向，不设置默认使用系统
//                            .isNotPreviewDownload(true) // 预览图片长按是否可以下载
//                            //.bindCustomPlayVideoCallback(callback)// 自定义播放回调控制，用户可以使用自己的视频播放界面
//                            .imageEngine(GlideEngine.createGlideEngine()) // 外部传入图片加载引擎，必传项
//                            .openExternalPreview(position, selectList)
//                }
//            }
//        }

//        mAdapter!!.setItemLongClickListener { holder, position, v ->
//            //如果item不是最后一个，则执行拖拽
//            needScaleBig = true
//            needScaleSmall = true
//            val size: Int = mAdapter!!.getData()!!.size()
//            if (size != maxSelectNum) {
//                mItemTouchHelper.startDrag(holder)
//                return@setItemLongClickListener
//            }
//            if (holder.layoutPosition !== size - 1) {
//                mItemTouchHelper.startDrag(holder)
//            }
//        }

        //-------------------------------Mention-----------------------------------
        initMention()
        setupMentionsList()
//        initMentionUserDialog()

        initPopupWindow(binding.edtCaption)
    }

    fun spanRecyclerView(data: List<LocalMedia?>?) {
        gridLayoutManager.spanSizeLookup = object : SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return setItemSpan(position, data)
            }
        }
    }

    fun setItemSpan(position: Int, arrayListMedia: List<LocalMedia?>?): Int {
        when (arrayListMedia!!.size) {
            5 -> return when (position) {
                0, 1 -> 3
                2, 3, 4 -> 2
                else -> 2
            }
            4 -> return when (position) {
                0, 1 -> 3
                2, 3 -> 3
                else -> 2
            }
            3 -> return when (position) {
                0 -> 6
                1, 2 -> 3
                else -> 2
            }
            2 -> return when (position) {
                0, 1 -> 3
                else -> 2
            }
            1 -> return when (position) {
                0 -> 6
                else -> 2
            }
            else ->
                return when (position) {
                    0, 1 -> 3
                    2, 3, 4 -> 2
                    else -> 2
                }
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        handleUserExit()

        //Clear All
    }

//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        val d: Dialog = super.onCreateDialog(savedInstanceState)
//        d.setOnShowListener(OnShowListener { dialog ->
//            val d = dialog as BottomSheetDialog
//            val bottomSheet: LinearLayout = d.findViewById<View>(R.id.bottom_sheet) as LinearLayout
//            val behaviour: BottomSheetBehavior<LinearLayout> = BottomSheetBehavior.from(bottomSheet)
//            behaviour.setBottomSheetCallback(object : BottomSheetCallback() {
//                override fun onStateChanged(@NonNull bottomSheet: View, newState: Int) {
//                    if (newState == BottomSheetBehavior.STATE_HIDDEN) {
//                        handleUserExit()
//                        dismiss()
//                    }
//                }
//
//                override fun onSlide(@NonNull bottomSheet: View, slideOffset: Float) {}
//            })
//        })
//        return d
//    }

    private fun handleUserExit() {
//        requireActivity().showToast("Dialog Close")
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.imgBack -> dismiss()
            R.id.txtAddPhoto -> {
                clickType = MediaPickerFragment.PickerType.PHOTO
//                checkValidation()

                //Add New Clear All
                PictureSelectorInstagramStyleActivity.arrayListLocalSize = 0
                PictureSelectorInstagramStyleActivity.flagIsNext = false
                PictureSelectorInstagramStyleActivity.arrayListLocal.clear()
                PictureSelectorInstagramStyleActivity.mainArrayList.clear()

                //-----------------------------Add New-----------------------------
                if (createPostAdapter.getData()!!.isNotEmpty()) {
                    InstagramPreviewContainer.isAspectRatio = true
                    InstagramPreviewContainer.flagIsFirst = true
                    InstagramPreviewContainer.mAspectRadioNew = FastSave.getInstance().getFloat("mAspectRadioNew", 0f)
                } else {
                    InstagramPreviewContainer.isAspectRatio = false
                    InstagramPreviewContainer.flagIsFirst = false
                    FastSave.getInstance().saveFloat("mAspectRadioNew", 0f)
                    InstagramPreviewContainer.mAspectRadioNew = 0f
                }

                openGallery()
            }
            R.id.txtAddVideo -> {
                clickType = MediaPickerFragment.PickerType.VIDEO
//                checkValidation()

                //Add New Clear All
                PictureSelectorInstagramStyleActivity.arrayListLocalSize = 0
                PictureSelectorInstagramStyleActivity.flagIsNext = false
                PictureSelectorInstagramStyleActivity.arrayListLocal.clear()
                PictureSelectorInstagramStyleActivity.mainArrayList.clear()

                //-----------------------------Add New-----------------------------
                if (createPostAdapter.getData()!!.isNotEmpty()) {
                    InstagramPreviewContainer.isAspectRatio = true
                    InstagramPreviewContainer.flagIsFirst = true
                    InstagramPreviewContainer.mAspectRadioNew = FastSave.getInstance().getFloat("mAspectRadioNew", 0f)
                } else {
                    InstagramPreviewContainer.isAspectRatio = false
                    InstagramPreviewContainer.flagIsFirst = false
                    FastSave.getInstance().saveFloat("mAspectRadioNew", 0f)
                    InstagramPreviewContainer.mAspectRadioNew = 0f
                }

                openGallery()
            }
            R.id.fabPost -> {
//                uploadPost()

                val arrayList = createPostAdapter.getData() as ArrayList<LocalMedia>

                if (arrayList.isNullOrEmpty()) {
                    activity.showToast("Please select some media for post")
                    return
                }

                FastSave.getInstance().saveFloat("mAspectRadioNew", 0f)

                arrayListMedia.addAll(createPostAdapter.getData() as ArrayList<LocalMedia>)

                val mIntent = Intent(context, FileUploadService::class.java)
                mIntent.putExtra("mention", Gson().toJson(jsonArrayMention))  //Add New
                mIntent.putExtra("caption", binding.edtCaption.text.toString().trim())
                mIntent.putExtra("commaSeperatedIds", commaSeperatedIds)
//                mIntent.putExtpra("mediaItemsArray", Gson().toJson(arrayListMedia))
                mIntent.putExtra("mediaItemsArray", Gson().toJson(createPostAdapter.getData() as ArrayList<LocalMedia>))
                mIntent.putExtra("switchTurnOff", if (binding.switchTurnOff.isChecked) "1" else "0")
                FileUploadService.shouldContinue = true

                dismiss()
                FileUploadService.enqueueWork(activity, mIntent)
            }
            R.id.llTagFriend -> openTagFriendListBottomSheet()
        }
    }

    private fun checkValidationGallery() {
        if (!arrayListMedia.isNullOrEmpty()) {

            var photoLimitSelection = 0
            var videoLimitSelection = 0
            //-------------------------------Photo----------------------------

            if (currentCountPhoto == Constants.MAX_IMAGE_COUNT) {
                activity.showToast(activity.getString(R.string.error_msg_cannot_select_more_images))
                return
            } else {
                photoLimitSelection = if (currentCountPhoto > 0) {
                    // if video select then only 15 images select
                    activity.showToast(activity.getString(R.string.error_msg_cannot_select_more_image_and_videos))
                    Constants.MAX_IMAGE_COUNT - currentCountPhoto
                } else {
                    Constants.MAX_IMAGE_COUNT_IF_VIDEO_SELECTED
                }
                if (photoLimitSelection == 0) {
                    activity.showToast(activity.getString(R.string.error_msg_cannot_select_more_image_and_videos))
                    return
                }
            }

            //-------------------------------Video----------------------------
            if (currentCountVideo == Constants.MAX_VIDEO_COUNT) {
                Constants.VIDEO_COUNT_SELECTION = 0
                activity.showToast(activity.getString(R.string.error_msg_cannot_select_more_image_and_videos))
            } else {
                if (currentCountVideo == 0) {
                    Constants.VIDEO_COUNT_SELECTION = Constants.MAX_VIDEO_COUNT
                } else {
                    Constants.VIDEO_COUNT_SELECTION = Constants.MAX_VIDEO_COUNT - currentCountVideo
                }
            }

            if (currentCountVideo > Constants.MAX_IMAGE_COUNT_IF_VIDEO_SELECTED) {  // >15
                //Remove and Select Only 5 videos
                activity.showToast(activity.getString(R.string.error_msg_cannot_select_more_image_and_videos))
                return
            }

            Constants.IMAGE_COUNT_SELECTION = photoLimitSelection
            Constants.VIDEO_COUNT_SELECTION = videoLimitSelection
            openGallery()
            //-------------------------------Video----------------------------

            if (currentCountVideo > Constants.MAX_IMAGE_COUNT_IF_VIDEO_SELECTED) {  // >15
                //Remove and Select Only 5 videos
                activity.showToast(activity.getString(R.string.error_msg_cannot_select_more_image_and_videos))
            } else {
                //Check current video size
                var limitSelection = 0
                if (currentCountVideo > 0) {

                    if (currentCountVideo >= Constants.MAX_VIDEO_COUNT) {
                        activity.showToast(activity.getString(R.string.error_msg_cannot_select_more_videos))
                    } else {
                        limitSelection = Constants.MAX_VIDEO_COUNT - currentCountVideo

                        if (limitSelection == 0) {
                            activity.showToast(activity.getString(R.string.error_msg_cannot_select_more_videos))
                        } else {
//                            openPicker(MediaPickerFragment.PickerType.VIDEO, limitSelection)
                            openGallery()
                        }
                    }
                    activity.showToast(activity.getString(R.string.error_msg_cannot_select_more_image_and_videos))
                } else {
                    openPicker(MediaPickerFragment.PickerType.VIDEO, Constants.MAX_VIDEO_COUNT)
                }
            }
        }
    }

    private fun checkValidation() {
        if (!arrayListMedia.isNullOrEmpty()) {
            if (this.selectionType == MediaPickerFragment.PickerType.PHOTO.name) {

                if (clickType == MediaPickerFragment.PickerType.PHOTO) {
                    if (currentCountPhoto == Constants.MAX_IMAGE_COUNT) {
                        activity.showToast(activity.getString(R.string.error_msg_cannot_select_more_images))
                    } else {
                        var limitSelection = if (currentCountPhoto > 0) {
                            // if video select then only 15 images select
                            activity.showToast(activity.getString(R.string.error_msg_cannot_select_more_image_and_videos))
                            Constants.MAX_IMAGE_COUNT_IF_VIDEO_SELECTED - currentCountPhoto
                        } else {
                            Constants.MAX_IMAGE_COUNT - currentCountPhoto
                        }
                        if (limitSelection == 0)
                            activity.showToast(activity.getString(R.string.error_msg_cannot_select_more_image_and_videos))
                        else
                            openPicker(MediaPickerFragment.PickerType.PHOTO, limitSelection)
                    }
                } else {
                    if (currentCountPhoto > Constants.MAX_IMAGE_COUNT_IF_VIDEO_SELECTED) {  // >15
                        //Remove and Select Only 5 videos
                        activity.showToast(activity.getString(R.string.error_msg_cannot_select_more_image_and_videos))
                    } else {
                        //Check current video size
                        var limitSelection = 0
                        if (currentCountVideo > 0) {

                            if (currentCountVideo >= Constants.MAX_VIDEO_COUNT) {
                                activity.showToast(activity.getString(R.string.error_msg_cannot_select_more_videos))
                            } else {
                                limitSelection = Constants.MAX_VIDEO_COUNT - currentCountVideo

                                if (limitSelection == 0)
                                    activity.showToast(activity.getString(R.string.error_msg_cannot_select_more_videos))
                                else
                                    openPicker(MediaPickerFragment.PickerType.VIDEO, limitSelection)
                            }
                            activity.showToast(activity.getString(R.string.error_msg_cannot_select_more_image_and_videos))
                        } else {
                            openPicker(MediaPickerFragment.PickerType.VIDEO, Constants.MAX_VIDEO_COUNT)
                        }
                    }
                }

            } else {
                if (clickType == MediaPickerFragment.PickerType.PHOTO) {
                    if (currentCountVideo > 0) {

                        if (currentCountVideo >= Constants.MAX_VIDEO_COUNT) {
                            openPicker(MediaPickerFragment.PickerType.PHOTO, Constants.MAX_IMAGE_COUNT_IF_VIDEO_SELECTED)
                        } else {
                            var limitSelection = 0
                            limitSelection = Constants.MAX_IMAGE_COUNT_IF_VIDEO_SELECTED - currentCountPhoto

                            if (limitSelection == 0) {
                                activity.showToast(activity.getString(R.string.error_msg_cannot_select_more_image_and_videos))
                            } else {
                                openPicker(MediaPickerFragment.PickerType.PHOTO, limitSelection)
                            }
                        }
                        activity.showToast(activity.getString(R.string.error_msg_cannot_select_more_image_and_videos))
                    } else {
                        openPicker(MediaPickerFragment.PickerType.PHOTO, Constants.MAX_IMAGE_COUNT_IF_VIDEO_SELECTED)
                    }
                } else {
                    val limitSelectionVideo = Constants.MAX_VIDEO_COUNT - currentCountVideo

                    if (limitSelectionVideo == 0)
                        activity.showToast(activity.getString(R.string.error_msg_cannot_select_more_videos))
                    else
                        openPicker(MediaPickerFragment.PickerType.VIDEO, limitSelectionVideo)
                }
            }
        }
    }

    private fun openPicker(type: MediaPickerFragment.PickerType, count: Int) {
        MediaPickerFragment.newInstance(
            multiple = true,
            allowCamera = false,
            allowGallery = true,
            pickerType = type,
            maxSelection = count,
            theme = R.style.ChiliPhotoPicker_Light,
        ).show(childFragmentManager, "picker")
    }

//    override fun onMediaPicked(mediaItems: ArrayList<Uri>) {
//        showLog("onMediaPicked", mediaItems.joinToString(separator = "\n") { it.toString() })
//
//        if (mediaItems.size > 0) {
//            binding.rvPhotoVideo.visibility = View.VISIBLE
//
//            if (clickType == MediaPickerFragment.PickerType.PHOTO) {
//                currentCountPhoto += mediaItems.size
//            } else {
//                currentCountVideo += mediaItems.size
//            }
//
//            this.mediaItemsArray.addAll(mediaItems)
//
//            arrayListMedia = ArrayList()
//            mediaItemsArray.forEachIndexed { index, uri ->
//
//                var postUriModel = Post_Uri_Model(
//                    mediaItemsArray[index].path.toString(),
//                    if (isImageFile(mediaItemsArray[index].path)) "image" else "video"
//                )
//                postUriModel?.let { arrayListMedia.add(postUriModel) }
//            }
//
//            createPostAdapter.setData(arrayListMedia)
//            spanRecyclerView()
//        } else {
//
//            binding.rvPhotoVideo.visibility = View.GONE
//        }
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        currentCountPhoto = 0
        currentCountVideo = 0
    }

    override fun onDestroy() {
        super.onDestroy()
        mCompositeDisposable.clear()
    }

    var captionarray: ArrayList<MultipartBody.Part> = ArrayList<MultipartBody.Part>()
    var typearray: ArrayList<MultipartBody.Part> = ArrayList<MultipartBody.Part>()
    var thumbarray: ArrayList<MultipartBody.Part> = ArrayList<MultipartBody.Part>()
    var imagearray: ArrayList<MultipartBody.Part> = ArrayList<MultipartBody.Part>()
    var count = 0

    private fun itemClick() {
        createPostAdapter?.let {
            createPostAdapter.onItemClick = { position ->

//                val path = "/storage/emulated/0/DCIM/Camera/PXL_20210814_053234516.PORTRAIT.jpg"
//                val path = "/storage/emulated/0/1611831077734.png"
//                val path = createPostAdapter.getData()!![position]!!.path
//
//                val intent = Intent(activity, EditImageActivity::class.java)
//                intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
//                intent.putExtra("filepath", path)
//                startActivity(intent)

//                val intent = Intent(activity, PostPagerActivity::class.java)
////                intent.putExtra("arraylist", Gson().toJson(arrayListMedia))
//                intent.putExtra("arraylist", Gson().toJson(createPostAdapter.getData()))
//                intent.putExtra("position", position)
//                resultLauncher.launch(intent)

                //-----------------------------Add New---------------------------------
                openViewPagerActivity(createPostAdapter.getData() as List<LocalMedia>, position)
            }
        }
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data

                val type: Type = object : TypeToken<ArrayList<LocalMedia>>() {}.type
                arrayListMedia = Gson().fromJson(data!!.getStringExtra("arraylist"), type)

                if (arrayListMedia.isNotEmpty()) {
                    createPostAdapter.setData(arrayListMedia)
                    spanRecyclerView(createPostAdapter.getData())
                } else {
                    binding.rvPhotoVideo.visibility = View.GONE
                }
            }
        }

    private fun openTagFriendListBottomSheet() {
        try {
            myBottomSheetTagFriendListFragment = PostTagFriendListFragment().newInstance(commaSeperatedIds)
            myBottomSheetTagFriendListFragment.setListener(this)
            myBottomSheetTagFriendListFragment.show(childFragmentManager, myBottomSheetTagFriendListFragment.tag)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onPostTagDoneClick(commaSeperatedIds: String) {
        if (commaSeperatedIds.isNotEmpty()) {
            showLog("commaSeperatedIds", commaSeperatedIds)
            this.commaSeperatedIds = commaSeperatedIds

            //Show Count of tag friend list
            binding.txtTagCount.text = commaSeperatedIds.split(",").size.toString()
        } else {
            this.commaSeperatedIds = ""
            binding.txtTagCount.text = ""
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PictureConfig.CHOOSE_REQUEST -> {
                    // 图片选择结果回调
                    val selectList = PictureSelector.obtainMultipleResult(data)
                    // 例如 LocalMedia 里面返回五种path
                    // 1.media.getPath(); 原图path
                    // 2.media.getCutPath();裁剪后path，需判断media.isCut();切勿直接使用
                    // 3.media.getCompressPath();压缩后path，需判断media.isCompressed();切勿直接使用
                    // 4.media.getOriginalPath()); media.isOriginal());为true时此字段才有值
                    // 5.media.getAndroidQToPath();Android Q版本特有返回的字段，但如果开启了压缩或裁剪还是取裁剪或压缩路径；注意：.isAndroidQTransform 为false 此字段将返回空
                    // 如果同时开启裁剪和压缩，则取压缩路径为准因为是先裁剪后压缩
                    for (media in selectList) {
                        Log.i("MomentsFragment", "是否压缩:" + media.isCompressed)
                        Log.i("MomentsFragment", "压缩:" + media.compressPath)
                        Log.i("MomentsFragment", "原图:" + media.path)
                        Log.i("MomentsFragment", "是否裁剪:" + media.isCut)
                        Log.i("MomentsFragment", "裁剪:" + media.cutPath)
                        Log.i("MomentsFragment", "是否开启原图:" + media.isOriginal)
                        Log.i("MomentsFragment", "原图路径:" + media.originalPath)
                        Log.i("MomentsFragment", "Android Q 特有Path:" + media.androidQToPath)
                        Log.i("MomentsFragment", "Size: " + media.size)
                    }
                    createPostAdapter!!.setList(selectList as ArrayList<LocalMedia>)
                    createPostAdapter!!.notifyDataSetChanged()
                }
            }
        }
    }

    open class OnResultCallbackListenerImpl(adapter: CreatePostAdapter) : OnResultCallbackListener<LocalMedia> {
        private val mAdapter: WeakReference<CreatePostAdapter> = WeakReference(adapter)
        override fun onResult(result: List<LocalMedia>) {
            for (media in result) {
                Log.i("MomentsFragment", "Whether to compress:" + media.isCompressed)
                Log.i("MomentsFragment", "Compression:" + media.compressPath)
                Log.i("MomentsFragment", "Original image:" + media.path)
                Log.i("MomentsFragment", "Whether to cut:" + media.isCut)
                Log.i("MomentsFragment", "Cut:" + media.cutPath)
                Log.i("MomentsFragment", "Whether to open the original image:" + media.isOriginal)
                Log.i("MomentsFragment", "Original image path:" + media.originalPath)
                Log.i("MomentsFragment", "Android Q unique Path:" + media.androidQToPath)
                Log.i("MomentsFragment", "Size: " + media.size)
            }
            val adapter = mAdapter.get()
            if (adapter != null) {
                adapter.setList(result as ArrayList<LocalMedia>)
                adapter.notifyDataSetChanged()
            }
        }

        override fun onCancel() {
            Log.i("MomentsFragment", "PictureSelector Cancel")
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (createPostAdapter != null && createPostAdapter!!.getData() != null && createPostAdapter!!.getData()!!.isNotEmpty()) {
            outState.putParcelableArrayList(
                "selectorList",
                createPostAdapter!!.getData() as ArrayList<out Parcelable?>?
            )
        }
    }

    private fun clearCache() {
        // 清空图片缓存，包括裁剪、压缩后的图片 注意:必须要在上传完成后调用 必须要获取权限
        if (PermissionChecker.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            //PictureFileUtils.deleteCacheDirFile(this, PictureMimeType.ofImage());
            PictureFileUtils.deleteAllCacheDirFile(context)
        } else {
            PermissionChecker.requestPermissions(
                requireActivity(), arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PictureConfig.APPLY_STORAGE_PERMISSIONS_CODE
            )
        }
    }

    private fun openViewPagerActivity(mediaItems: List<LocalMedia>, currPosition: Int) {
        showLog("item_selected: ", mediaItems.toString())

        clearAll()

        mediaItems!!.forEach { localMedia ->
            targetList.add(Uri.fromFile(File(localMedia.path.toString())).toString())

            if (isImageFile(localMedia.path)) {
                return_mediatype.add(1)
            } else {
                return_mediatype.add(0)
            }
        }

        val captionarr = Array(mediaItems.size) { "" }
        select_captions.addAll(captionarr)

        var timearr = Array(mediaItems.size) { "" }
        select_time.addAll(timearr)

        val intent = Intent(activity, ViewPagerActivity::class.java)
        intent.putStringArrayListExtra("select_urls", targetList /*Gson().toJson(mediaItems)*/)
        intent.putStringArrayListExtra("select_captions", select_captions)
        intent.putStringArrayListExtra("select_time", select_time)
        intent.putIntegerArrayListExtra("urls_mediatype", return_mediatype)
        intent.putExtra("currPosition", currPosition)
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        viewPagerResultLauncher.launch(intent)
    }

    private var viewPagerResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                targetList = ArrayList()
                select_captions = ArrayList()
                select_time = ArrayList()
                return_mediatype = ArrayList()

                targetList = result.data!!.getStringArrayListExtra("select_urls")!!
                select_captions = result.data!!.getStringArrayListExtra("select_captions")!!
                select_time = result.data!!.getStringArrayListExtra("select_time")!!
                return_mediatype = result.data!!.getIntegerArrayListExtra("urls_mediatype")!!

                showLog("data", "$targetList $select_captions $select_time $return_mediatype")

                //------------------Add New----------------------
//                val data: Intent? = result.data

//                val type: Type = object : TypeToken<ArrayList<LocalMedia>>() {}.type
//                arrayListMedia = Gson().fromJson(data!!.getStringExtra("arraylist"), type)

                arrayListMedia.clear()

                targetList.forEachIndexed { index, path ->
                    var localMedia = LocalMedia()
                    localMedia.path = path
                    arrayListMedia.add(localMedia)
                }

                if (arrayListMedia.isNotEmpty()) {
                    createPostAdapter.setData(arrayListMedia)
                    spanRecyclerView(createPostAdapter.getData())
                } else {
                    binding.rvPhotoVideo.visibility = View.GONE
                }
            }
        }

    private fun clearAll() {
        targetList = ArrayList()
        select_captions = ArrayList()
        select_time = ArrayList()
        return_mediatype = ArrayList()
    }

    //------------------------------------Mention-------------------------------------------
    /**
     * Initialize views and utility objects.
     */
    private fun initMention() {
//        edt_caption = ViewUtils.findViewById(this, com.percolate.mentions.sample.R.id.comment_field)
//        sendCommentButton = ViewUtils.findViewById(this, com.percolate.mentions.sample.R.id.send_comment)
        mentions = Mentions.Builder(activity, binding.edtCaption)
            .suggestionsListener(this)
            .queryListener(this)
            .build()
        mentionsLoaderUtils = MentionsLoaderUtils(activity)
    }

    /**
     * Setups the mentions suggestions list. Creates and sets and adapter for
     * the mentions list and sets the on item click listener.
     */
    private fun setupMentionsList() {
        binding.mentionsList.layoutManager = LinearLayoutManager(activity)
        usersAdapter = UsersAdapter(activity)
        binding.mentionsList.adapter = usersAdapter

        binding.mentionsList.addOnItemTouchListener(RecyclerItemClickListener(activity, object : RecyclerItemClickListener.OnItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
                val user = usersAdapter!!.getItem(position)

                user?.let {
                    val mention = Mention()
                    mention.mentionName = user.firstName + " " + user.lastName
                    mention.userId = user.id.toString()  //Add New
                    mention.mentionAccountId = user.accountId //Add New
                    mentions!!.insertMention(mention)
//                    Log.e("onItemClick: ", mentions.toString())

                    //----------------------------Add New----------------------------
                    highlightMentions(commentTextView = binding.edtCaption, mentions = mentions!!.insertedMentions)

                    //------------------Create JSONArray-----------------
                    jsonArrayMention = JsonArray()
                    mentions!!.insertedMentions.forEach { mentionable ->

//                        val jsonRequest = JsonObject()
//                        jsonRequest.addProperty("id", mentionable.userId)
//                        jsonRequest.addProperty("mention", mentionable.mentionAccountId)
//
//                        jsonArrayMention.add(jsonRequest)

                        val jsonObject = JsonObject()
                        jsonObject.addProperty("id", mentionable.userId)
                        jsonObject.addProperty("accountId", mentionable.mentionAccountId)
                        jsonObject.addProperty("name", mentionable.mentionName)
                        jsonArrayMention.add(jsonObject)
                    }
                    Log.e("onItemClick: jsonArray", Gson().toJson(jsonArrayMention))
                }
            }
        }))
    }

    override fun displaySuggestions(display: Boolean) {
        if (display) {
            //Add New
            //showDialogMention()

//            binding.mentionsListLayout.visibility = View.VISIBLE
            mentions_list_layout.visibility = View.VISIBLE
        } else {
            //Add New
            //hideDialogMention()

//            binding.mentionsListLayout.visibility = View.GONE
            mentions_list_layout.visibility = View.GONE
        }
    }

    /**
     * Toggle the mentions list's visibility if there are search results returned for search
     * query. Shows the empty list view
     *
     * @param display boolean   true if the mentions list should be shown or false if
     * the empty suggestions list view should be shown.
     */
    private fun showMentionsList(display: Boolean) {
        //Add New
        //showDialogMention()

//        binding.mentionsListLayout.visibility = View.VISIBLE
//        if (display) {
//            binding.mentionsList.visibility = View.VISIBLE
//            binding.mentionsEmptyView.visibility = View.GONE
//
//        } else {
//            binding.mentionsList.visibility = View.GONE
//            binding.mentionsEmptyView.visibility = View.VISIBLE
//        }

        mentions_list_layout.visibility = View.VISIBLE
        if (display) {
            showPopupWindow(binding.edtCaption)
            mentionsEmptyView.visibility = View.GONE
        } else {
            popupWindow?.let {
                mentionsEmptyView.visibility = View.VISIBLE
                it.dismiss()
            }
        }
    }

    override fun onQueryReceived(query: String?) {
        Log.e("onQueryReceived: ", query!!)

        getFriendList(0, query!!) { users ->
            if (users.isNotEmpty()) {
                if (users.isNotEmpty()) {
                    usersAdapter!!.clear()
                    usersAdapter!!.setCurrentQuery(query!!)
                    usersAdapter!!.addAll(users)
                    showMentionsList(true)
                } else {
                    showMentionsList(false)
                }
            }
        }
    }

    private lateinit var allfriendList: ArrayList<ResponseFriendsList.Data>

    private fun getFriendList(currentSize: Int, searchText: String, callback: ((ArrayList<ResponseFriendsList.Data>) -> Unit)? = null) {
        try {
            try {
                //openProgressDialog(activity)

                val hashMap: HashMap<String, Any> = hashMapOf(
                    Constants.flag to "mention",
                    Constants.term to searchText
//                        Constants.limit to requireActivity().getString(R.string.limit_20),
//                        Constants.offset to currentSize.toString()
                )

                mCompositeDisposable.add(
                    ApiClient.create()
                        .search_contacts(hashMap)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableObserver<ResponseFriendsList>() {
                            override fun onNext(responseFriendsList: ResponseFriendsList) {
                                Log.v("onNext: ", responseFriendsList.toString())
                                if (responseFriendsList != null && responseFriendsList.success == 1) {

                                    if (responseFriendsList.data.isNotEmpty()) {

                                        allfriendList?.let { it.clear() }
                                        allfriendList.addAll(responseFriendsList.data as ArrayList<ResponseFriendsList.Data>)
                                    }
                                }
                            }

                            override fun onError(e: Throwable) {
                                hideProgressDialog()
                                Log.v("onError: ", e.toString())
                                callback?.invoke(allfriendList)
                            }

                            override fun onComplete() {
                                hideProgressDialog()
                                callback?.invoke(allfriendList)
                            }
                        })
                )

            } catch (e: Exception) {
                e.printStackTrace()
                hideProgressDialog()
                callback?.invoke(allfriendList)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            hideProgressDialog()
        }
    }

    private fun highlightMentions(commentTextView: EditText?, mentions: List<Mentionable>?) {
        if (commentTextView != null && mentions != null && mentions.isNotEmpty()) {
//            val spannable: Spannable = SpannableString(commentTextView.text)
            for (mention in mentions) {
                if (mention != null) {
                    val start = mention.mentionOffset
                    val end = start + mention.mentionLength
                    if (commentTextView.length() >= end) {
//                        spannable.setSpan(ForegroundColorSpan(orange), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
//                        commentTextView.setText(spannable, TextView.BufferType.SPANNABLE)

                        //-----------------------Add New-------------------------
                        val strSpannable = commentTextView.text.toSpannable() as SpannableString
                        strSpannable.withClickableSpan(mention.mentionName!!, onClickListener = ({
                            Toast.makeText(context, mention.mentionName, Toast.LENGTH_SHORT).show()
                            
                            openProfileActivity(activity, mention.userId!!.toInt())
                        }))
                        commentTextView.movementMethod = LinkMovementMethod.getInstance()
//                        commentTextView.text = strSpannable
                        commentTextView.setText(strSpannable)

                    } else {
                        //Something went wrong.  The expected text that we're trying to highlight does not
                        // match the actual text at that position.
                        Log.w("Mentions Sample", "Mention lost. [$mention]")
                    }
                }
            }
        }
    }

    //---------------------------------Mention Dialog----------------------------------
//    private fun initMentionUserDialog() {
//        dialogMention = Dialog(activity)
//        dialogMention.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
//        dialogMention.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        dialogMention.setContentView(R.layout.dialog_mention_user)
//
//        val mentionsList: RecyclerView = dialogMention.findViewById(R.id.mentions_list)!!
//        val edtCaption: AppCompatEditText = dialogMention.findViewById(R.id.edt_caption)!!
//
////        dialogMention.show()
//
//        //------------------------Add New---------------------------
//        //init mention
//        mentions = Mentions.Builder(activity, edtCaption)
//            .suggestionsListener(this)
//            .queryListener(this)
//            .build()
//        mentionsLoaderUtils = MentionsLoaderUtils(activity)
//
//        //init recyclerview
//        mentionsList.layoutManager = LinearLayoutManager(activity)
//        usersAdapter = UsersAdapter(activity)
//        mentionsList.adapter = usersAdapter
//
//        mentionsList.addOnItemTouchListener(RecyclerItemClickListener(activity, object : RecyclerItemClickListener.OnItemClickListener {
//            override fun onItemClick(view: View?, position: Int) {
//                val user = usersAdapter!!.getItem(position)
//
//                user?.let {
//                    val mention = Mention()
//                    mention.mentionName = user.firstName + " " + user.lastName
//                    mention.userId = user.id.toString()  //Add New
//                    mention.mentionAccountId = user.accountId //Add New
//                    mentions!!.insertMention(mention)
////                    Log.e("onItemClick: ", mentions.toString())
//
//                    //----------------------------Add New----------------------------
////                    highlightMentions(commentTextView = binding.edtCaption, mentions = mentions!!.insertedMentions)
//
////                    highlightMentions(commentTextView = edtCaption, mentions = mentions!!.insertedMentions)
//
//                    //------------------Create JSONArray-----------------
//                    jsonArrayMention = JsonArray()
//                    mentions!!.insertedMentions.forEach { mentionable ->
//
////                        val jsonRequest = JsonObject()
////                        jsonRequest.addProperty("id", mentionable.userId)
////                        jsonRequest.addProperty("mention", mentionable.mentionAccountId)
////
////                        jsonArrayMention.add(jsonRequest)
//
//                        val jsonObject = JsonObject()
//                        jsonObject.addProperty("id", mentionable.userId)
//                        jsonObject.addProperty("accountId", mentionable.mentionAccountId)
//                        jsonObject.addProperty("name", mentionable.mentionName)
//                        jsonArrayMention.add(jsonObject)
//                    }
//                    Log.e("onItemClick: jsonArray", Gson().toJson(jsonArrayMention))
//
//                    //----------hide dialog and append value in moment edittext
//                    hideDialogMention()
//
//                    binding.edtCaption.setText(binding.edtCaption.text.toString().plus(edtCaption.text.toString()))
//
//                }
//            }
//        }))
//    }

//    private fun hideDialogMention() {
//        dialogMention?.let {
//            if (dialogMention.isShowing) it.hide()
//        }
//    }
//
//    private fun showDialogMention() {
//        dialogMention?.let {
//            it.show()
//        }
//    }

    private fun initPopupWindow(anchor: View) {
        popupWindow = PopupWindow(anchor.context).apply {
            isOutsideTouchable = true
            val inflater = LayoutInflater.from(anchor.context)
            contentView = inflater.inflate(R.layout.mention_popup_layout, null)/*.apply {
                measure(
                    View.MeasureSpec.makeMeasureSpec(WindowManager.LayoutParams.MATCH_PARENT, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                )
            }*/

            width = WindowManager.LayoutParams.MATCH_PARENT
            height = 800

            mentionsEmptyView = contentView.findViewById(R.id.mentions_empty_view)
            mentions_list_layout = contentView.findViewById(R.id.mentions_list_layout)
            mentions_list = contentView.findViewById(R.id.mentions_list)
            mentions_list.setBackgroundDrawable(ContextCompat.getDrawable(activity, R.drawable.rect_rounded_bg_white))

//            mentions_list_layout.foreground.alpha = 0

            setupMentionsListForPopup(mentions_list)

        }.also { popupWindow ->
            popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            // Absolute location of the anchor view
//            val location = IntArray(2).apply {
//                anchor.getLocationOnScreen(this)
//            }
//            val size = Size(
//                popupWindow.contentView.measuredWidth,
//                200/*popupWindow.contentView.measuredHeight*/
//            )
//            popupWindow.showAtLocation(
//                anchor,
//                Gravity.TOP or Gravity.START,
//                0,/*location[0] - (size.width - anchor.width) / 2,*/
//                anchor.bottom-50/*location[1] - size.height*/
//            )
        }
    }

    private fun showPopupWindow(anchor: View) {
        popupWindow?.let {
//            val location = IntArray(2).apply {
//                anchor.getLocationOnScreen(this)
//            }

//            popupWindow.showAsDropDown(anchor, 10, -950, Gravity.TOP)
            popupWindow.showAsDropDown(anchor)/*, 10, -(location[1] - anchor.height), Gravity.TOP)*/

            //            popupWindow.showAtLocation(
//                anchor,
//                Gravity.TOP or Gravity.START,
//                0,/*location[0] - (size.width - anchor.width) / 2,*/
//                anchor.bottom-50/*location[1] - size.height*/
//            )
        }
    }

    private fun setupMentionsListForPopup(mentionsList: RecyclerView) {
        mentionsList.layoutManager = LinearLayoutManager(activity)
        usersAdapter = UsersAdapter(activity)
        mentionsList.adapter = usersAdapter

        mentionsList.addOnItemTouchListener(RecyclerItemClickListener(activity, object : RecyclerItemClickListener.OnItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
                val user = usersAdapter!!.getItem(position)

                user?.let {
                    val mention = Mention()
                    mention.mentionName = user.firstName + " " + user.lastName
                    mention.userId = user.id.toString()  //Add New
                    mention.mentionAccountId = user.accountId //Add New
                    mentions!!.insertMention(mention)
//                    Log.e("onItemClick: ", mentions.toString())

                    //----------------------------Add New----------------------------
                    highlightMentions(commentTextView = binding.edtCaption, mentions = mentions!!.insertedMentions)

                    //------------------Create JSONArray-----------------
                    jsonArrayMention = JsonArray()
                    mentions!!.insertedMentions.forEach { mentionable ->

//                        val jsonRequest = JsonObject()
//                        jsonRequest.addProperty("id", mentionable.userId)
//                        jsonRequest.addProperty("mention", mentionable.mentionAccountId)
//
//                        jsonArrayMention.add(jsonRequest)

                        val jsonObject = JsonObject()
                        jsonObject.addProperty("id", mentionable.userId)
                        jsonObject.addProperty("accountId", mentionable.mentionAccountId)
                        jsonObject.addProperty("name", mentionable.mentionName)
                        jsonArrayMention.add(jsonObject)
                    }
                    Log.e("onItemClick: jsonArray", Gson().toJson(jsonArrayMention))
                }
            }
        }))
    }
}

