package com.task.newapp.ui.fragments.registration

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.appizona.yehiahd.fastsave.FastSave
import com.task.newapp.R
import com.task.newapp.databinding.FragmentRegistrationStep1Binding
import com.task.newapp.interfaces.OnPageChangeListener
import com.task.newapp.utils.*
import com.task.newapp.utils.Constants.Companion.RegistrationStepsEnum
import com.task.newapp.utils.compressor.SiliCompressor
import com.theartofdev.edmodo.cropper.CropImage
import lv.chi.photopicker.MediaPickerFragment
import java.io.File


class RegistrationStep1Fragment : Fragment(), MediaPickerFragment.Callback, View.OnClickListener {

    private var imageUri: String? = ""
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_registration_step1, container, false)
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
        binding.ivProfile.load(imageUri, true, binding.edtFirstName.text.toString() + " " + binding.edtLastName.text.toString(), "#6CAEC4")
        binding.ivEditImg.setOnClickListener(this)
        binding.btnNext.setOnClickListener(this)
        binding.layoutBack.tvBack.setOnClickListener(this)

        binding.edtFirstName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                validateFirstName()
                if (imageUri == "") {
                    binding.ivProfile.load(imageUri, true, binding.edtFirstName.text.toString() + " " + binding.edtLastName.text.toString(), "#6CAEC4")
                }
            }
        })
        binding.edtLastName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val text: String = binding.edtLastName.getText().toString()
                if (text.startsWith(" ")) {
                    binding.edtLastName.setText(text.trim { it <= ' ' })
                }
            }

            override fun afterTextChanged(s: Editable?) {
                validateLastName()
                if (imageUri == "") {
                    binding.ivProfile.load(imageUri, true, binding.edtFirstName.text.toString() + " " + binding.edtLastName.text.toString(), "#6CAEC4")
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
            if (resultCode == Activity.RESULT_OK) {
                imageUri = result.uri.path
                binding.ivEditImg.setImageResource(R.drawable.ic_delete_profile)
                val filePath: String = SiliCompressor.with(activity).compress(
                    imageUri,
                    File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                        "temp_profile.jpg"
                    ),
                    0
                )
                binding.ivProfile.load(filePath, true, binding.edtFirstName.text.toString() + " " + binding.edtLastName.text.toString(), "#6CAEC4")
                FastSave.getInstance().saveString(Constants.profile_image, filePath)

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
            R.id.iv_edit_img -> {
                if (imageUri != "") {
                    FastSave.getInstance().saveString(Constants.profile_image, "")
                    binding.ivEditImg.setImageResource(R.drawable.ic_gallery)
                    imageUri = ""
                    binding.ivProfile.load(imageUri, true, binding.edtFirstName.text.toString() + " " + binding.edtLastName.text.toString(), "#6CAEC4")
                } else {
                    openPicker()
                }

            }
            R.id.btn_next -> {
                hideSoftKeyboard(requireActivity())
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
            .setAspectRatio(1, 1).start(requireContext(), this)
    }
}