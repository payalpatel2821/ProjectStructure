package com.task.newapp.ui.fragments.post

import android.app.Activity
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.paginate.Paginate
import com.task.newapp.R
import com.task.newapp.adapter.post.ThoughtColorPatternAdapter
import com.task.newapp.api.ApiClient
import com.task.newapp.databinding.FragmentThoughtsBinding
import com.task.newapp.models.CommonResponse
import com.task.newapp.models.post.ResponsePattern
import com.task.newapp.utils.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.util.*
import kotlin.collections.ArrayList


class ThoughtFragment : BottomSheetDialogFragment(), View.OnClickListener, Paginate.Callbacks,
    PostTagFriendListFragment.OnPostTagDoneClickListener {

    var patternId = 0
    var postId = 0
    var gravity = 0
    var currGravity = 1
    var isBold = 0
    var isItalic = 0
    var isUnderline = 0
    var countFont = 0
    var backgroundType = "solid"
    var colorBg = 0
    var colorFont = 0
    private lateinit var binding: FragmentThoughtsBinding
    lateinit var fragmentActivity: Activity
    lateinit var thoughtColorPatternAdapter: ThoughtColorPatternAdapter
    private val mCompositeDisposable = CompositeDisposable()
    lateinit var myBottomSheetTagFriendListFragment: PostTagFriendListFragment
    private var commaSeperatedIds: String = ""

    lateinit var fontArrayThoughts: Array<String>
    lateinit var colorFontArrayThoughts: IntArray
    lateinit var colorArrayThoughts: IntArray
    lateinit var patternArrayThoughts: ArrayList<ResponsePattern.Data>
    private var paginate: Paginate? = null
    var isloading = false
    var hasLoadedAllItems = false
    private var isAPICallRunning = false
    var onPostDoneClickListener: OnPostDoneClickListener? = null

    fun newInstance(post_id: String): ThoughtFragment {
        val f = ThoughtFragment()
        val args = Bundle()
        args.putString("postId", post_id)
        f.arguments = args
        Log.e("postId: ", post_id)
        return f
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postId = requireArguments().getInt("postId", 0)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_thoughts, container, false)
        this.fragmentActivity = requireActivity()
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initView()
    }

    private fun initPaging() {
        if (paginate != null) {
            paginate!!.unbind()
        }

        paginate = Paginate.with(binding.rvColorPattern, this)
            .setLoadingTriggerThreshold(17)
            .addLoadingListItem(false)
            .setLoadingListItemCreator(null)
            .build()
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        handleUserExit()
    }

    private fun initView() {
        binding.edtThought.setUnderlineColor(Color.TRANSPARENT)

        clickListerner()

        colorArrayThoughts = fragmentActivity.resources.getIntArray(R.array.colorArrayThoughts)
        colorFontArrayThoughts = fragmentActivity.resources.getIntArray(R.array.colorFontArrayThoughts)
        patternArrayThoughts = ArrayList()
        fontArrayThoughts = fragmentActivity.resources.getStringArray(R.array.fontArrayThoughts)

        binding.rvColorPattern.setHasFixedSize(true)
        binding.rvColorPattern.isFocusable = false

        colorBg = colorArrayThoughts[0]
        colorFont = colorFontArrayThoughts[0]

        binding.edtThought.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) binding.edtThought.hint = "Type here..." else binding.edtThought.hint = ""

            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        thoughtColorPatternAdapter = ThoughtColorPatternAdapter(fragmentActivity, colorArrayThoughts, colorFontArrayThoughts, patternArrayThoughts, 0)
        binding.rvColorPattern.adapter = thoughtColorPatternAdapter

        initPaging()
        callAPIPattern()
    }

    private fun clickListerner() {
        binding.imgSelect.setOnClickListener(this)
        binding.txtSolid.setOnClickListener(this)
        binding.txtPattern.setOnClickListener(this)
        binding.txtFontColor.setOnClickListener(this)
        binding.imgFont.setOnClickListener(this)
        binding.imgPosition.setOnClickListener(this)
        binding.imgBold.setOnClickListener(this)
        binding.imgItalik.setOnClickListener(this)
        binding.imgUnderline.setOnClickListener(this)
        binding.imgBack.setOnClickListener(this)
        binding.fabPost.setOnClickListener(this)
        binding.llTagFriend.setOnClickListener(this)
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
        Toast.makeText(requireContext(), "TODO - SAVE data or similar", Toast.LENGTH_SHORT).show()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.imgSelect -> visibilityLayout()
            R.id.imgBack -> dismiss()
            R.id.txtSolid, R.id.txtPattern, R.id.txtFontColor -> {
                when (v.id) {
                    R.id.txtSolid -> changeStyle(0)
                    R.id.txtPattern -> changeStyle(1)
                    R.id.txtFontColor -> changeStyle(2)
                }
                if (binding.rvColorPattern.visibility == View.VISIBLE) binding.rvColorPattern.visibility = View.GONE
                else binding.rvColorPattern.visibility = View.VISIBLE
            }
            R.id.imgFont -> setFont()
            R.id.imgPosition -> setPosition()
            R.id.imgBold -> setBold()
            R.id.imgItalik -> setItalic()
            R.id.imgUnderline -> setUnderline()
            R.id.fabPost -> callAPIPost()
            R.id.llTagFriend -> openTagFriendListBottomSheet()
        }
    }

    private fun changeStyle(type: Int) {
        thoughtColorPatternAdapter = ThoughtColorPatternAdapter(fragmentActivity, colorArrayThoughts, colorFontArrayThoughts, patternArrayThoughts, type)
        binding.rvColorPattern.adapter = thoughtColorPatternAdapter
        if (binding.cvColor.visibility == View.VISIBLE) binding.cvColor.visibility = View.GONE

        thoughtColorPatternAdapter.onItemClick = { color, pattern, type ->
            when (type) {
                0 -> {
                    //Solid
                    backgroundType = "solid"
                    colorBg = color!!
                    binding.rlText.setBackgroundColor(color!!)
                }
                1 -> {
                    //Pattern
                    backgroundType = "pattern"

                    patternId = pattern!!.id

                    Glide.with(requireActivity())
                        .load(pattern!!.name)
                        .into(object : CustomTarget<Drawable?>() {
                            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                            override fun onLoadCleared(@Nullable placeholder: Drawable?) {
                            }

                            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable?>?) {
                                binding.rlText.background = resource
                            }
                        })

                }
                else -> {
                    colorFont = color!!
                    binding.edtThought.setTextColor(color!!)
                }
            }
            if (binding.cvColor.visibility == View.VISIBLE) binding.cvColor.visibility = View.GONE
        }
    }

    private fun setUnderline() {
        if (isUnderline == 0) {
            isUnderline = 1
            binding.edtThought.setUnderlineColor(Color.BLACK)
        } else {
            isUnderline = 0
            binding.edtThought.setUnderlineColor(Color.TRANSPARENT)
        }
    }

    private fun setItalic() {
        if (isItalic == 0) {
            isItalic = 1
            binding.edtThought.setTypeface(null, if (isBold == 1) Typeface.BOLD_ITALIC else Typeface.ITALIC)
        } else {
            isItalic = 0
            binding.edtThought.setTypeface(null, if (isBold == 1) Typeface.BOLD else Typeface.NORMAL)
        }
    }

    private fun setBold() {
        if (isBold == 0) {
            isBold = 1
            binding.edtThought.setTypeface(null, if (isItalic == 1) Typeface.BOLD_ITALIC else Typeface.BOLD)
        } else {
            isBold = 0
            binding.edtThought.setTypeface(null, if (isItalic == 1) Typeface.ITALIC else Typeface.NORMAL)
        }
    }

    private fun setFont() {
        countFont++
        if (countFont == fontArrayThoughts.size) countFont = 0

        Log.e("countFont: ", countFont.toString())
        binding.edtThought.typeface = Typeface.createFromAsset(requireActivity().assets, fontArrayThoughts[countFont])

//        binding.edtThought.setTypeface(null, if (isItalic == 1) Typeface.ITALIC else Typeface.NORMAL)
    }

    private fun setPosition() {
        when (gravity) {
            0 -> {  // left
                binding.imgPosition.setImageResource(R.drawable.ic_aline_center)

                binding.rlText.gravity = Gravity.LEFT or Gravity.CENTER_VERTICAL
                gravity = 1
                currGravity = 0
            }
            1 -> {  // center
                binding.imgPosition.setImageResource(R.drawable.ic_aline_right)

                binding.rlText.gravity = Gravity.CENTER
                gravity = 2
                currGravity = 1
            }
            2 -> {  // right
                binding.imgPosition.setImageResource(R.drawable.ic_aline_left)

                binding.rlText.gravity = Gravity.RIGHT or Gravity.CENTER_VERTICAL
                gravity = 0
                currGravity = 2
            }
        }
    }

    private fun visibilityLayout() {
        if (binding.cvColor.visibility == View.VISIBLE) binding.cvColor.visibility = View.GONE
        else binding.cvColor.visibility = View.VISIBLE

        binding.rvColorPattern.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        mCompositeDisposable.clear()
    }

    var currentPosPattern = 0

    private fun callAPIPattern() {
        try {
            isAPICallRunning = true
            if (activity != null) {
                try {
                    openProgressDialog(activity)

                    val hashMap: HashMap<String, Any> = hashMapOf(
                        Constants.limit to resources.getString(R.string.get_comments),
                        Constants.offset to currentPosPattern.toString()
                    )

                    mCompositeDisposable.add(
                        ApiClient.create()
                            .post_pattern(hashMap)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(object : DisposableObserver<ResponsePattern>() {
                                override fun onNext(responsePattern: ResponsePattern) {
                                    Log.v("onNext: ", responsePattern.toString())
                                    if (responsePattern != null) {
                                        if (responsePattern.success == 1) {

                                            if (responsePattern.data.isNotEmpty()) {

//                                                patternArrayThoughts = ArrayList()
                                                patternArrayThoughts.addAll(responsePattern.data as ArrayList<ResponsePattern.Data>)

//                                                thoughtColorPatternAdapter = ThoughtColorPatternAdapter(fragmentActivity, colorArrayThoughts, colorFontArrayThoughts, patternArrayThoughts, 0)
//                                                binding.rvColorPattern.adapter = thoughtColorPatternAdapter

                                                isloading = false
                                                hasLoadedAllItems = false
//                                                binding.rvColorPattern.scrollToPosition(0)


                                            } else {
                                                isloading = true
                                                hasLoadedAllItems = true
                                            }

                                        } else {
                                            hasLoadedAllItems = true
                                        }

                                    }
                                    isAPICallRunning = false
                                }

                                override fun onError(e: Throwable) {
                                    Log.v("onError: ", e.toString())
                                    isAPICallRunning = false
                                }

                                override fun onComplete() {
                                    hideProgressDialog()
                                }
                            })
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    isAPICallRunning = false
                    hideProgressDialog()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            hideProgressDialog()
        }
    }

    override fun onLoadMore() {
        isloading = true

        if (!isAPICallRunning) {
            val scrollPosition: Int = thoughtColorPatternAdapter.getData()
            if (scrollPosition > 0) {
                showLog("loadmore_comment", scrollPosition.toString())
                val currentSize = scrollPosition - 1

                if (currentSize > 0) {
                    currentPosPattern += 10
                    callAPIPattern()
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

    private fun callAPIPost() {
        try {
            if (!requireActivity().isNetworkConnected()) {
                requireActivity().showToast(getString(R.string.no_internet))
                return
            }

            if (binding.edtThought.text.toString().isEmpty()) {
                requireActivity().showToast("Please add some thought text")
                return
            }

            var gravity = if (currGravity == 0) "left" else if (currGravity == 1) "center" else "right"
            var thought = if (binding.edtThought.text.toString().length > 120) "article" else "text"

            var colorBackground = '#' + Integer.toHexString(colorBg)
            var fontColor = '#' + Integer.toHexString(colorFont)

            val isComment = if (binding.switchTurnOff.isChecked) "1" else "0"

//            val hastags: RequestBody = "".toRequestBody("text/plain".toMediaTypeOrNull())
//            val title: RequestBody = "".trim().toRequestBody("text/plain".toMediaTypeOrNull())
//            val turn_off_comment: RequestBody = isComment.toRequestBody("text/plain".toMediaTypeOrNull())
//            val type: RequestBody = "thought".toRequestBody("text/plain".toMediaTypeOrNull())
//            val latitude: RequestBody = "0".toRequestBody("text/plain".toMediaTypeOrNull())
//            val longitude: RequestBody = "0".toRequestBody("text/plain".toMediaTypeOrNull())
//            val location: RequestBody = "".toRequestBody("text/plain".toMediaTypeOrNull())
//            val user_tags: RequestBody = commaSeperatedIds.toRequestBody("text/plain".toMediaTypeOrNull())

            //-----------------------------List of MultipartBody.Part-------------------------------
            val builder = MultipartBody.Builder().setType(MultipartBody.FORM)

            builder.addFormDataPart("turn_off_comment", isComment)
                .addFormDataPart("hastags", "")
                .addFormDataPart("title", "")
                .addFormDataPart("type", "thought")
                .addFormDataPart("latitude", "0")
                .addFormDataPart("longitude", "0")
                .addFormDataPart("location", "")
                .addFormDataPart("user_tags", commaSeperatedIds)

                //---------------------------------------------------------------------------------------

                .addFormDataPart("thought[font_style]", countFont.toString())
                .addFormDataPart("thought[thought_type]", thought)
                .addFormDataPart("thought[background_type]", backgroundType)
                .addFormDataPart("thought[color]", colorBackground.replace("#ff", "#"))

            if (backgroundType == "pattern") {
                builder.addFormDataPart("thought[pattern_id]", patternId.toString())
            }

            builder.addFormDataPart("thought[alignment]", gravity)
            builder.addFormDataPart("thought[is_bold]", isBold.toString())
            builder.addFormDataPart("thought[is_italic]", isItalic.toString())
            builder.addFormDataPart("thought[is_underline]", isUnderline.toString())
            builder.addFormDataPart("thought[font_color]", fontColor.replace("#ff", "#"))
            builder.addFormDataPart("thought[content]", binding.edtThought.text.toString().trim())

//            val thought_type: MultipartBody.Part = MultipartBody.Part.createFormData("thought[thought_type]", thought)
//            val background_type: MultipartBody.Part = MultipartBody.Part.createFormData("thought[background_type]", backgroundType)
//            val color: MultipartBody.Part = MultipartBody.Part.createFormData("thought[color]", colorBackground.replace("#ff", "#"))
//            val pattern_id: MultipartBody.Part = MultipartBody.Part.createFormData("thought[pattern_id]", patternId.toString())
//            val alignment: MultipartBody.Part = MultipartBody.Part.createFormData("thought[alignment]", gravity)
//            val is_bold: MultipartBody.Part = MultipartBody.Part.createFormData("thought[is_bold]", isBold.toString())
//            val is_italic: MultipartBody.Part = MultipartBody.Part.createFormData("thought[is_italic]", isItalic.toString())
//            val is_underline: MultipartBody.Part = MultipartBody.Part.createFormData("thought[is_underline]", isUnderline.toString())
//            val font_color: MultipartBody.Part = MultipartBody.Part.createFormData("thought[font_color]", fontColor.replace("#ff", "#"))
//            val content: MultipartBody.Part = MultipartBody.Part.createFormData("thought[content]", binding.edtThought.text.toString().trim())

            openProgressDialog(activity)

            val requestBody: RequestBody = builder.build()

            mCompositeDisposable.add(
                ApiClient.create().addPostThought(
//                    turn_off_comment, hastags, title, type, latitude, longitude, location, user_tags,
                    requestBody
                )
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableObserver<CommonResponse>() {
                        override fun onNext(commonResponse: CommonResponse) {

                            if (commonResponse.success == 1) {
                                activity?.showToast(commonResponse.message)

                                //Close bottom sheet and refresh post list
                                dismiss()
                                onPostDoneClickListener?.onPostClick()
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

    /**
     * interface for post done click
     *
     */
    interface OnPostDoneClickListener {
        fun onPostClick()
    }

    fun setListener(listener: OnPostDoneClickListener) {
        onPostDoneClickListener = listener
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
 