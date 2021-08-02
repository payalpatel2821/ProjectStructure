package com.task.newapp.ui.fragments.registration

import android.app.Activity
import android.content.DialogInterface
import android.content.res.TypedArray
import android.graphics.*
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.task.newapp.R
import com.task.newapp.adapter.ThoughtColorPatternAdapter
import com.task.newapp.databinding.FragmentThoughtsBinding
import com.task.newapp.utils.setUnderlineColor


class ThoughtFragment : BottomSheetDialogFragment(), View.OnClickListener {

    var postId = 0
    var gravity = 0
    var isBold = 0
    var isItalic = 0
    var isUnderline = 0
    var countFont = 0
    private lateinit var binding: FragmentThoughtsBinding
    lateinit var fragmentActivity: Activity
    lateinit var thoughtColorPatternAdapter: ThoughtColorPatternAdapter

    lateinit var fontArrayThoughts: Array<String>
    lateinit var colorFontArrayThoughts: IntArray
    lateinit var colorArrayThoughts: IntArray
    lateinit var patternArrayThoughts: TypedArray

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

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        handleUserExit()
    }

    private fun initView() {
        binding.edtThought.setUnderlineColor(Color.TRANSPARENT)

        clickListerner()

        colorArrayThoughts = fragmentActivity.resources.getIntArray(R.array.colorArrayThoughts)
        colorFontArrayThoughts = fragmentActivity.resources.getIntArray(R.array.colorFontArrayThoughts)
        patternArrayThoughts = fragmentActivity.resources.obtainTypedArray(R.array.patternArrayThoughts)
        fontArrayThoughts = fragmentActivity.resources.getStringArray(R.array.fontArrayThoughts)


        binding.rvColorPattern.setHasFixedSize(true)
        binding.rvColorPattern.isFocusable = false

        //binding.edtThought.setOnFocusChangeListener { v, hasFocus -> binding.edtThought.hint = "" }

        binding.edtThought.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) binding.edtThought.hint = "Type here..." else binding.edtThought.hint = ""

            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
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
            }
            R.id.imgFont -> setFont()
            R.id.imgPosition -> setPosition()
            R.id.imgBold -> setBold()
            R.id.imgItalik -> setItalic()
            R.id.imgUnderline -> setUnderline()
        }
    }

    private fun changeStyle(type: Int) {
        thoughtColorPatternAdapter = ThoughtColorPatternAdapter(fragmentActivity, colorArrayThoughts, colorFontArrayThoughts, patternArrayThoughts, type)
        binding.rvColorPattern.adapter = thoughtColorPatternAdapter
        if (binding.cvColor.visibility == View.VISIBLE) binding.cvColor.visibility = View.GONE

        thoughtColorPatternAdapter.onItemClick = { color, position, type ->
            when (type) {
                0 -> {
                    binding.rlText.setBackgroundColor(color)
                }
                1 -> {
                    binding.rlText.setBackgroundResource(color)
                }
                else -> {
                    binding.edtThought.setTextColor(color)
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
        binding.edtThought.typeface = Typeface.createFromAsset(fragmentActivity.assets, fontArrayThoughts[countFont])

//        binding.edtThought.setTypeface(null, if (isItalic == 1) Typeface.ITALIC else Typeface.NORMAL)
    }

    private fun setPosition() {
        when (gravity) {
            0 -> {  // left
                binding.imgPosition.setImageResource(R.drawable.ic_aline_center)

                binding.rlText.gravity = Gravity.CENTER
                gravity = 1
            }
            1 -> {  // center
                binding.imgPosition.setImageResource(R.drawable.ic_aline_right)

                binding.rlText.gravity = Gravity.RIGHT or Gravity.CENTER_VERTICAL
                gravity = 2
            }
            2 -> {  // right
                binding.imgPosition.setImageResource(R.drawable.ic_aline_left)

                binding.rlText.gravity = Gravity.LEFT or Gravity.CENTER_VERTICAL
                gravity = 0
            }
        }
    }

    private fun visibilityLayout() {
        if (binding.cvColor.visibility == View.VISIBLE) {
            binding.cvColor.visibility = View.GONE
//            binding.edtThought.isEnabled = true
        } else {
            binding.cvColor.visibility = View.VISIBLE
//            binding.edtThought.isEnabled = false
        }
    }

}
 