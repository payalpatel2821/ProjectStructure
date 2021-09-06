@file:Suppress("INACCESSIBLE_TYPE")

package com.task.newapp.ui.fragments.post

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import com.appizona.yehiahd.fastsave.FastSave
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
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
import com.task.newapp.App
import com.task.newapp.R
import com.task.newapp.adapter.post.CreatePostAdapter
import com.task.newapp.api.ApiClient
import com.task.newapp.databinding.FragmentMomentzBinding
import com.task.newapp.models.CommonResponse
import com.task.newapp.models.post.Post_Uri_Model
import com.task.newapp.service.FileUploadService
import com.task.newapp.ui.activities.post.PostPagerActivity
import com.task.newapp.utils.*
import com.task.newapp.utils.instapicker.GlideCacheEngine
import com.task.newapp.utils.instapicker.GlideEngine
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import lv.chi.photopicker.MediaPickerFragment
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.lang.ref.WeakReference
import java.lang.reflect.Type
import kotlin.collections.ArrayList


class MomentsFragment : BottomSheetDialogFragment(), View.OnClickListener, //MediaPickerFragment.Callback,
    PostTagFriendListFragment.OnPostTagDoneClickListener {

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
        requireActivity().showToast("Dialog Close")
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
                mIntent.putExtra("caption", binding.edtCaption.text.toString().trim())
                mIntent.putExtra("commaSeperatedIds", commaSeperatedIds)
//                mIntent.putExtra("mediaItemsArray", Gson().toJson(arrayListMedia))
                mIntent.putExtra("mediaItemsArray", Gson().toJson(createPostAdapter.getData() as ArrayList<LocalMedia>))
                mIntent.putExtra("switchTurnOff", if (binding.switchTurnOff.isChecked) "1" else "0")

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

//    private fun uploadPost() {
//        try {
//            count = 0
//            //get all data and compress if required
//            if (!mediaItemsArray.isNullOrEmpty()) {
//                arrayListMedia = ArrayList()
//
//                mediaItemsArray.forEachIndexed { index, uri ->
//
//                    var postUriModel = Post_Uri_Model(
//                        mediaItemsArray[index].path.toString(),
//                        if (isImageFile(mediaItemsArray[index].path)) "image" else "video"
//                    )
//                    postUriModel?.let { arrayListMedia.add(postUriModel) }
//                }
//                Log.e("uploadPost_arrayList:", arrayListMedia.toString())
//
//                //----------------------------Check and compress---------------------------------
//
//                captionarray = ArrayList()
//                typearray = ArrayList()
//                thumbarray = ArrayList()
//                imagearray = ArrayList()
//
//                openProgressDialog(activity)
//
//                arrayListMedia.forEachIndexed { index, postUriModel ->
//                    try {
//                        val cap_name = "contents[$index][caption]"
//                        val type_name = "contents[$index][type]"
//                        val thumb_name = "contents[$index][thumb]"
//                        val image_name = "contents[$index][file]"
//
//                        captionarray.add(MultipartBody.Part.createFormData(cap_name, binding.edtCaption.text.toString()))
//                        typearray.add(MultipartBody.Part.createFormData(type_name, postUriModel.type))
//
//                        val myfile: File = File(postUriModel.file_path)
//
//                        val mainFolder = File(Environment.getExternalStorageDirectory().absolutePath + "/HOW")
//                        if (!mainFolder.exists()) {
//                            mainFolder.mkdir()
//                            mainFolder.mkdirs()
//                        }
//                        val fileImage = File(mainFolder.absolutePath + "/.temp")
//                        if (!fileImage.exists()) {
//                            fileImage.mkdir()
//                            fileImage.mkdirs()
//                        }
//
//                        val storedThumbPath: File = File(fileImage, System.currentTimeMillis().toString() + "_thumb.jpg")
//
//                        //Check image or video
//                        if (postUriModel.type == "image") {
//
////                            if (myfile.length() / 1024 > 25600) {  // More than 25 MB
//                            Log.e("uploadPost: >25MB ", myfile.toString())
//
//                            //Compress and Stored in .temp folder
//                            val filePath: String = SiliCompressor.with(getActivity()).compress(
//                                postUriModel.file_path,
//                                storedThumbPath,
//                                0
//                            )
//                            thumbarray.add(prepareFilePart(thumb_name, filePath, "image/*"))
//                            //thumbarray.add(prepareFilePart(thumb_name, postUriModel.file_path, "image/*"))
////                            }
////                            else {
////                                Log.e("uploadPost: ", postUriModel.file_path.toString())
////                                thumbarray.add(prepareFilePart(thumb_name, postUriModel.file_path, "image/*"))
////                            }
//                            //Original
//                            imagearray.add(prepareFilePart(image_name, postUriModel.file_path, "image/*"))
//                            count++
//
//                            if (count == arrayListMedia.size) {
//                                showLog("VideoCompress", "callAPIPost_$count")
//                                callAPIPost(binding.edtCaption.text.toString().trim())
//                            }
//
//                        } else {
//                            //Video
//
//                            if (myfile.length() / 1024 > 51200) {  // More than 51 MB
//                                Log.e("uploadPost: >51MB ", myfile.toString())
//                                //Compress
//
//                                //Stored in .temp folder
////                                val filePath: String = SiliCompressor.with(activity).compress(
////                                    postUriModel.file_path,
////                                    storedThumbPath
////                                )
////                                thumbarray.add(prepareFilePart(thumb_name, filePath, "image/*"))
//
//                                //-----------------------Compress Video-----------------------------
//                                val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
//                                val outputName = "compress$timeStamp.mp4"
//
//                                val outputFile: String = checkCompressfolder().toString() + "/" + outputName  // /HOW/.compressvideo
//
//                                //Stored in .compressvideo folder
////                                val compressVideoPath: String = SiliCompressor.with(activity).compressVideo(
////                                    postUriModel.file_path,
////                                    outputFile
////                                )
//
//                                val destPath: String = outputFile
//
//                                VideoCompress.compressVideoLow(postUriModel.file_path, destPath, object : VideoCompress.CompressListener {
//                                    override fun onStart() {
//                                        showLog("VideoCompress", "onStart")
////                                        tv_indicator.setText(
////                                            "Compressing..." + "\n"
////                                                    + "Start at: " + SimpleDateFormat("HH:mm:ss", getLocale()).format(Date())
////                                        )
////                                        pb_compress.setVisibility(View.VISIBLE)
////                                        startTime = System.currentTimeMillis()
////                                        Util.writeFile(this@MainActivity, "Start at: " + SimpleDateFormat("HH:mm:ss", getLocale()).format(Date()) + "\n")
//                                    }
//
//                                    override fun onSuccess() {
//                                        showLog("VideoCompress", "onSuccess")
////                                        val previous: String = tv_indicator.getText().toString()
////                                        tv_indicator.setText(
////                                            (previous + "\n"
////                                                    + "Compress Success!" + "\n"
////                                                    + "End at: " + SimpleDateFormat("HH:mm:ss", getLocale()).format(Date()))
////                                        )
////                                        pb_compress.setVisibility(View.INVISIBLE)
////                                        endTime = System.currentTimeMillis()
////                                        Util.writeFile(this@MainActivity, "End at: " + SimpleDateFormat("HH:mm:ss", getLocale()).format(Date()) + "\n")
////                                        Util.writeFile(this@MainActivity, "Total: " + ((endTime - startTime) / 1000) + "s" + "\n")
////                                        Util.writeFile(this@MainActivity)
//
//                                        //---------------------------New added-----------------------------
//                                        val retriever = MediaMetadataRetriever()
//                                        retriever.setDataSource(destPath)
//                                        val extractedImage = retriever.getFrameAtTime(100, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
//                                        val bitmap: Bitmap = if (extractedImage!!.height > 500 && extractedImage.width > 500) {
//                                            Bitmap.createScaledBitmap(extractedImage, extractedImage.width / 3, extractedImage.height / 3, true)
//                                        } else {
//                                            Bitmap.createScaledBitmap(extractedImage, extractedImage.width, extractedImage.height, true)
//                                        }
//
//                                        //------------------------------thumb------------------------------
//                                        if (storeImage(bitmap, storedThumbPath)) {
//                                            thumbarray.add(prepareFilePart(thumb_name, storedThumbPath.path, "image/*"))
//                                        }
//
//                                        //Original
//                                        imagearray.add(prepareFilePart(image_name, destPath, "video/*"))
//
//                                        count++
//                                        if (count == arrayListMedia.size) {
//                                            showLog("VideoCompress", "callAPIPost_$count")
//                                            callAPIPost(binding.edtCaption.text.toString().trim())
//                                        }
//                                    }
//
//                                    override fun onFail() {
//                                        count++
//                                        showLog("VideoCompress", "onFail")
//
//                                        if (count == arrayListMedia.size) {
//                                            showLog("VideoCompress", "callAPIPost_$count")
//                                            callAPIPost(binding.edtCaption.text.toString().trim())
//                                        }
//
////                                        tv_indicator.setText("Compress Failed!")
////                                        pb_compress.setVisibility(View.INVISIBLE)
////                                        endTime = System.currentTimeMillis()
////                                        Util.writeFile(this@MainActivity, "Failed Compress!!!" + SimpleDateFormat("HH:mm:ss", getLocale()).format(Date()))
//                                    }
//
//                                    override fun onProgress(percent: Float) {
//                                        showLog("VideoCompress", "onProgress_$percent")
////                                        tv_progress.setText("$percent%")
//                                    }
//                                })
//
////                                //---------------------------New added-----------------------------
////                                val retriever = MediaMetadataRetriever()
////                                retriever.setDataSource(destPath)
////                                val extractedImage = retriever.getFrameAtTime(100, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
////                                val bitmap: Bitmap = if (extractedImage!!.height > 500 && extractedImage.width > 500) {
////                                    Bitmap.createScaledBitmap(extractedImage, extractedImage.width / 3, extractedImage.height / 3, true)
////                                } else {
////                                    Bitmap.createScaledBitmap(extractedImage, extractedImage.width, extractedImage.height, true)
////                                }
////
////                                //------------------------------thumb------------------------------
////                                if (storeImage(bitmap, storedThumbPath)) {
////                                    thumbarray.add(prepareFilePart(thumb_name, storedThumbPath.path, "image/*"))
////                                }
////
////                                //Original
////                                imagearray.add(prepareFilePart(image_name, destPath, "video/*"))
//
//                            } else {
//                                Log.e("uploadPost: ", postUriModel.file_path.toString())
//
//                                //Stored in .temp folder
////                                val filePath: String = SiliCompressor.with(activity).compressVideo(
////                                    postUriModel.file_path,
////                                    storedThumbPath.path
////                                )
//
//                                VideoCompress.compressVideoLow(postUriModel.file_path, storedThumbPath.path, object : VideoCompress.CompressListener {
//                                    override fun onStart() {
//                                        showLog("VideoCompress", "onStart")
////                                        tv_indicator.setText(
////                                            "Compressing..." + "\n"
////                                                    + "Start at: " + SimpleDateFormat("HH:mm:ss", getLocale()).format(Date())
////                                        )
////                                        pb_compress.setVisibility(View.VISIBLE)
////                                        startTime = System.currentTimeMillis()
////                                        Util.writeFile(this@MainActivity, "Start at: " + SimpleDateFormat("HH:mm:ss", getLocale()).format(Date()) + "\n")
//                                    }
//
//                                    override fun onSuccess() {
//                                        count++
//                                        showLog("VideoCompress", "onSuccess")
//
//                                        if (count == arrayListMedia.size) {
//                                            showLog("VideoCompress", "callAPIPost_$count")
//                                            callAPIPost(binding.edtCaption.text.toString().trim())
//                                        }
//
////                                        val previous: String = tv_indicator.getText().toString()
////                                        tv_indicator.setText(
////                                            (previous + "\n"
////                                                    + "Compress Success!" + "\n"
////                                                    + "End at: " + SimpleDateFormat("HH:mm:ss", getLocale()).format(Date()))
////                                        )
////                                        pb_compress.setVisibility(View.INVISIBLE)
////                                        endTime = System.currentTimeMillis()
////                                        Util.writeFile(this@MainActivity, "End at: " + SimpleDateFormat("HH:mm:ss", getLocale()).format(Date()) + "\n")
////                                        Util.writeFile(this@MainActivity, "Total: " + ((endTime - startTime) / 1000) + "s" + "\n")
////                                        Util.writeFile(this@MainActivity)
//
//                                        //---------------------------New added-----------------------------
//                                        thumbarray.add(prepareFilePart(thumb_name, storedThumbPath.path, "image/*"))
//                                    }
//
//                                    override fun onFail() {
//                                        count++
//                                        showLog("VideoCompress", "onFail")
//
//                                        if (count == arrayListMedia.size) {
//                                            showLog("VideoCompress", "callAPIPost_$count")
//                                            callAPIPost(binding.edtCaption.text.toString().trim())
//                                        }
////                                        tv_indicator.setText("Compress Failed!")
////                                        pb_compress.setVisibility(View.INVISIBLE)
////                                        endTime = System.currentTimeMillis()
////                                        Util.writeFile(this@MainActivity, "Failed Compress!!!" + SimpleDateFormat("HH:mm:ss", getLocale()).format(Date()))
//                                    }
//
//                                    override fun onProgress(percent: Float) {
//                                        showLog("VideoCompress", "onProgress_$percent")
////                                        tv_progress.setText("$percent%")
//                                    }
//                                })
//
////                                thumbarray.add(prepareFilePart(thumb_name, filePath, "image/*"))
////
////                                //Original
//                                imagearray.add(prepareFilePart(image_name, postUriModel.file_path, "video/*"))
//                            }
//                        }
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                        count++
//
//                        if (count == arrayListMedia.size) {
//                            showLog("VideoCompress", "callAPIPost_$count")
//                            callAPIPost(binding.edtCaption.text.toString().trim())
//                        }
//                    }
//                }
//
//            }
//
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }

    private fun callAPIPost(title: String) {
        try {
            val isComment = if (binding.switchTurnOff.isChecked) "1" else "0"

            val turn_off_comment: RequestBody = isComment.toRequestBody("text/plain".toMediaTypeOrNull())
            val hastags: RequestBody = "".toRequestBody("text/plain".toMediaTypeOrNull())
            val title: RequestBody = title.toRequestBody("text/plain".toMediaTypeOrNull())
            val type: RequestBody = "simple".toRequestBody("text/plain".toMediaTypeOrNull())
            val latitude: RequestBody = "0".toRequestBody("text/plain".toMediaTypeOrNull())
            val longitude: RequestBody = "0".toRequestBody("text/plain".toMediaTypeOrNull())
            val location: RequestBody = "".toRequestBody("text/plain".toMediaTypeOrNull())
            val user_tags: RequestBody = commaSeperatedIds.toRequestBody("text/plain".toMediaTypeOrNull())

            //openProgressDialog(activity)
            mCompositeDisposable.add(
                ApiClient.create()
                    .addPost(turn_off_comment, hastags, title, type, latitude, longitude, location, user_tags, captionarray, typearray, thumbarray, imagearray)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableObserver<CommonResponse>() {
                        override fun onNext(commonResponse: CommonResponse) {

//                            if (commonResponse.success == 1) {
                            activity.showToast(commonResponse.message)

                            //Close bottom sheet and refresh post list
                            dismiss()
//                            onPostDoneClickListener?.onPostClick()

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
            hideProgressDialog()
        }
    }

//    /**
//     * interface for post done click
//     *
//     */
//    interface OnPostDoneClickListener {
//        fun onPostClick()
//    }
//
//    fun setListener(listener: OnPostDoneClickListener) {
//        onPostDoneClickListener = listener
//    }

    private fun itemClick() {
        createPostAdapter?.let {
            createPostAdapter.onItemClick = { position ->
                val intent = Intent(activity, PostPagerActivity::class.java)
//                intent.putExtra("arraylist", Gson().toJson(arrayListMedia))
                intent.putExtra("arraylist", Gson().toJson(createPostAdapter.getData()))
                intent.putExtra("position", position)
                resultLauncher.launch(intent)
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

}
