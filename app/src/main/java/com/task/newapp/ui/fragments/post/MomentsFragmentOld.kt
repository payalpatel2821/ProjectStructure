@file:Suppress("INACCESSIBLE_TYPE")

package com.task.newapp.ui.fragments.post

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.os.Environment
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
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.task.newapp.R
import com.task.newapp.adapter.post.CreatePostAdapter
import com.task.newapp.api.ApiClient
import com.task.newapp.databinding.FragmentMomentzBinding
import com.task.newapp.models.CommonResponse
import com.task.newapp.models.post.Post_Uri_Model
import com.task.newapp.service.FileUploadService
import com.task.newapp.ui.activities.post.PostPagerActivity
import com.task.newapp.utils.*
import com.task.newapp.utils.compressor.SiliCompressor
import com.vincent.videocompressor.VideoCompress
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import lv.chi.photopicker.MediaPickerFragment
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MomentsFragmentOld : BottomSheetDialogFragment(), View.OnClickListener, MediaPickerFragment.Callback,
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
    private lateinit var arrayListMedia: ArrayList<Post_Uri_Model>
    lateinit var myBottomSheetTagFriendListFragment: PostTagFriendListFragment
    private var commaSeperatedIds: String = ""

    fun newInstance(mediaItems: ArrayList<Uri>, selection: String): MomentsFragmentOld {
        val f = MomentsFragmentOld()
        val bundle = Bundle()
        bundle.putParcelableArrayList("mediaItems", mediaItems)
        bundle.putString("selection", selection)
        f.arguments = bundle
        Log.e("mediaItems: ", mediaItems.toString())
        return f
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mediaItemsArray = requireArguments().getParcelableArrayList("mediaItems")!!
        selectionType = requireArguments().getString("selection")!!

        if (this.selectionType == MediaPickerFragment.PickerType.PHOTO.name) {
            currentCountPhoto = mediaItemsArray.size
        } else {
            currentCountVideo = mediaItemsArray.size
        }

        arrayListMedia = ArrayList()
        mediaItemsArray.forEachIndexed { index, uri ->

            var postUriModel = Post_Uri_Model(
                mediaItemsArray[index].path.toString(),
                if (isImageFile(mediaItemsArray[index].path)) "image" else "video"
            )
            postUriModel?.let { arrayListMedia.add(postUriModel) }
        }
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

        //createPostAdapter = CreatePostAdapter(requireContext(), arrayListMedia)
        gridLayoutManager = GridLayoutManager(activity, 6)
        binding.rvPhotoVideo.layoutManager = gridLayoutManager
        binding.rvPhotoVideo.adapter = createPostAdapter

        itemClick()

        spanRecyclerView()
    }

    private fun spanRecyclerView() {
        gridLayoutManager.spanSizeLookup = object : SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return setItemSpan(position)
            }
        }
    }

    fun setItemSpan(position: Int): Int {
        when (arrayListMedia.size) {
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
//        activity!!.showToast("Dialog Close")
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.imgBack -> dismiss()
            R.id.txtAddPhoto -> {
                clickType = MediaPickerFragment.PickerType.PHOTO
                checkValidation()
            }
            R.id.txtAddVideo -> {
                clickType = MediaPickerFragment.PickerType.VIDEO
                checkValidation()
            }
            R.id.fabPost -> {
//                uploadPost()

                val mIntent = Intent(context, FileUploadService::class.java)
                mIntent.putExtra("caption", binding.edtCaption.text.toString().trim())
                mIntent.putExtra("commaSeperatedIds", commaSeperatedIds)
                mIntent.putExtra("mediaItemsArray", Gson().toJson(arrayListMedia))
                mIntent.putExtra("switchTurnOff", if (binding.switchTurnOff.isChecked) "1" else "0")

                dismiss()
                FileUploadService.enqueueWork(activity, mIntent)
            }
            R.id.llTagFriend -> openTagFriendListBottomSheet()
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

    override fun onMediaPicked(mediaItems: ArrayList<Uri>) {
        showLog("onMediaPicked", mediaItems.joinToString(separator = "\n") { it.toString() })

        if (mediaItems.size > 0) {
            binding.rvPhotoVideo.visibility = View.VISIBLE

            if (clickType == MediaPickerFragment.PickerType.PHOTO) {
                currentCountPhoto += mediaItems.size
            } else {
                currentCountVideo += mediaItems.size
            }

            this.mediaItemsArray.addAll(mediaItems)

            arrayListMedia = ArrayList()
            mediaItemsArray.forEachIndexed { index, uri ->

                var postUriModel = Post_Uri_Model(
                    mediaItemsArray[index].path.toString(),
                    if (isImageFile(mediaItemsArray[index].path)) "image" else "video"
                )
                postUriModel?.let { arrayListMedia.add(postUriModel) }
            }

            //createPostAdapter.setData(arrayListMedia)
            spanRecyclerView()
        } else {

            binding.rvPhotoVideo.visibility = View.GONE
        }
    }

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
                intent.putExtra("arraylist", Gson().toJson(arrayListMedia))
                intent.putExtra("position", position)
                resultLauncher.launch(intent)
            }
        }

//        val args = Bundle()
//        args.putSerializable("arraylist", arrayListMedia as Serializable?)
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data

                val type: Type = object : TypeToken<ArrayList<Post_Uri_Model>>() {}.type
                arrayListMedia = Gson().fromJson(data!!.getStringExtra("arraylist"), type)

                if (arrayListMedia.isNotEmpty()) {
                   // createPostAdapter.setData(arrayListMedia)
                    spanRecyclerView()
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
}
 