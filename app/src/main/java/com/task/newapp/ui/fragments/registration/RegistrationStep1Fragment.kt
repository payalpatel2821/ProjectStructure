package com.task.newapp.ui.fragments.registration

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.appizona.yehiahd.fastsave.FastSave
import com.bumptech.glide.Glide
import com.task.newapp.R
import com.task.newapp.databinding.FragmentRegistrationStep1Binding
import com.task.newapp.interfaces.OnPageChangeListener
import com.task.newapp.utils.Constants
import com.task.newapp.utils.Constants.Companion.RegistrationStepsEnum
import com.task.newapp.utils.firstCharacter
import com.task.newapp.utils.setupKeyboardListener
import com.task.newapp.utils.compressor.SiliCompressor
import com.task.newapp.utils.showLog
import com.theartofdev.edmodo.cropper.CropImage
import lv.chi.photopicker.MediaPickerFragment


class RegistrationStep1Fragment : Fragment(), MediaPickerFragment.Callback, View.OnClickListener {

    private var imageUri: Uri? = null
    private lateinit var binding: FragmentRegistrationStep1Binding
    private lateinit var onPageChangeListener: OnPageChangeListener
    var flagFirst = false
    var flagLast = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onPageChangeListener = context as OnPageChangeListener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_registration_step1,
            container,
            false
        )
        // Inflate the layout for this fragment

        clearAll()

        return binding.root
    }

    private fun clearAll() {
        binding.edtFirstName.setText("")
        binding.edtLastName.setText("")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        setupKeyboardListener(binding.scrollview)

        binding.relProfile.setOnClickListener(this)
        binding.btnNext.setOnClickListener(this)
        binding.layoutBack.tvBack.setOnClickListener(this)

        binding.edtFirstName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                validateFirstName()
                if (imageUri == null) {
                    if (binding.edtFirstName.text!!.isEmpty() && binding.edtLastName.text!!.isEmpty()) {
                        binding.ivProfile.setImageResource(R.drawable.ic_user)
                        binding.ivProfile.setColorFilter(Color.argb(255, 255, 255, 255))
                        binding.txtDpName.visibility = GONE
                    } else {
                        binding.ivProfile.setImageResource(0)
                        binding.txtDpName.visibility = VISIBLE
                        binding.txtDpName.text = firstCharacter(binding.edtFirstName.text.toString().trim() + " " + binding.edtLastName.text.toString().trim())
                    }
                }
            }
        })
        binding.edtLastName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                validateLastName()
                if (imageUri == null) {
                    if (binding.edtFirstName.text!!.isEmpty() && binding.edtLastName.text!!.isEmpty()) {
                        binding.ivProfile.setImageResource(R.drawable.ic_user)
                        binding.ivProfile.setColorFilter(Color.argb(255, 255, 255, 255))
                        binding.txtDpName.visibility = GONE
                    } else {
                        binding.ivProfile.setImageResource(0)
                        binding.txtDpName.visibility = VISIBLE
                        binding.txtDpName.text = firstCharacter(binding.edtFirstName.text.toString().trim() + " " + binding.edtLastName.text.toString().trim())
                    }
                }
            }
        })
    }

    private fun openPicker() {
        MediaPickerFragment.newInstance(
            multiple = false,
            allowCamera = true,
            allowGallery = true,
            pickerType = MediaPickerFragment.PickerType.PHOTO,
            maxSelection = 1,
            theme = R.style.ChiliPhotoPicker_Light,
        ).show(childFragmentManager, "picker")
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                val resultUri = result.uri
                imageUri = resultUri
                binding.ivProfile.setColorFilter(0)
                Glide.with(requireActivity()).load(resultUri).into(binding.ivProfile)
                FastSave.getInstance().saveString(Constants.profile_image, resultUri.path.toString())
                binding.ivEditImg.setImageResource(R.drawable.ic_close)
                binding.txtDpName.visibility = GONE
                Log.e("callAPIRegister:result", resultUri.path.toString())

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }
    }

    fun validateFirstName(): Boolean {
        when {
            binding.edtFirstName.text.toString().trim().isEmpty() -> {
                binding.inputLayoutFirst.error = getString(R.string.enter_first)
                requestFocus(binding.edtFirstName)
                flagFirst = false
                checkAndEnable()
                return false
            }
            binding.edtFirstName.text.toString().length < 3 -> {
                binding.inputLayoutFirst.error = getString(R.string.enter_firstname_validate)
                requestFocus(binding.edtFirstName)
                flagFirst = false
                checkAndEnable()
                return false
            }
            else -> {
                binding.inputLayoutFirst.isErrorEnabled = false
                flagFirst = true
                checkAndEnable()
            }
        }
        return true
    }

    fun validateLastName(): Boolean {
        when {
            binding.edtLastName.text.toString().trim().isEmpty() -> {
                binding.inputLayoutLast.error = getString(R.string.enter_last)
                requestFocus(binding.edtLastName)
                flagLast = false
                checkAndEnable()
                return false
            }
            else -> {
                binding.inputLayoutLast.isErrorEnabled = false
                flagLast = true
                checkAndEnable()
            }
        }
        return true
    }

    private fun checkAndEnable() {
        if (flagFirst && flagLast) {
            binding.btnNext.background =
                requireActivity().getDrawable(R.drawable.btn_rect_rounded_bg)
            binding.btnNext.isEnabled = true
        } else {
            binding.btnNext.background =
                requireActivity().getDrawable(R.drawable.btn_rect_rounded_bg_disable)
            binding.btnNext.isEnabled = false
        }
    }

    private fun requestFocus(view: View) {
        if (view.requestFocus()) {
            requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.rel_profile -> {
                if (imageUri != null) {
                    FastSave.getInstance().saveString(Constants.profile_image, "")
                    binding.ivEditImg.setImageResource(R.drawable.ic_edit)
                    imageUri = null
                    if (binding.edtFirstName.text!!.isEmpty() && binding.edtLastName.text!!.isEmpty()) {
                        binding.ivProfile.setImageResource(R.drawable.ic_user)
                        binding.ivProfile.setColorFilter(Color.argb(255, 255, 255, 255))
                        binding.txtDpName.visibility = GONE
                    } else {
                        binding.ivProfile.setImageResource(0)
                        binding.txtDpName.visibility = VISIBLE
                        binding.txtDpName.text = firstCharacter(binding.edtFirstName.text.toString().trim() + " " + binding.edtLastName.text.toString().trim())
                    }
                } else {
                    openPicker()
                }

            }
            R.id.btn_next -> {
                if (!validateFirstName()) {
                    return
                }
                if (!validateLastName()) {
                    return
                }

                //Save Data
                FastSave.getInstance()
                    .saveString(Constants.first_name, binding.edtFirstName.text.toString().trim())
                FastSave.getInstance()
                    .saveString(Constants.last_name, binding.edtLastName.text.toString().trim())

                //Redirect to next registration step fragment
                onPageChangeListener.onPageChange(RegistrationStepsEnum.STEP_2.index)

            }
            R.id.tv_back -> {
                activity?.onBackPressed()
            }
        }
    }

    override fun onMediaPicked(photos: ArrayList<Uri>) {
        showLog("URL", photos[0].toString())
        CropImage.activity(photos[0])
            .start(requireContext(), this)
    }
}