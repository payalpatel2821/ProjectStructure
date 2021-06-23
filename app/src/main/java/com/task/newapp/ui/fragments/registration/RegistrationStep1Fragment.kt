package com.task.newapp.ui.fragments.registration

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.task.newapp.R
import com.task.newapp.databinding.FragmentRegistrationStep1Binding
import com.task.newapp.utils.showLog
import com.theartofdev.edmodo.cropper.CropImage
import lv.chi.photopicker.PhotoPickerFragment


class RegistrationStep1Fragment : Fragment(), PhotoPickerFragment.Callback, View.OnClickListener {

    private lateinit var binding: FragmentRegistrationStep1Binding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.relProfile.setOnClickListener(this)
    }

    private fun openPicker() {
        PhotoPickerFragment.newInstance(
            multiple = false,
            allowCamera = true,
            maxSelection = 1,
            theme = R.style.ChiliPhotoPicker_Light
        ).show(childFragmentManager, "picker")
    }


    override fun onImagesPicked(photos: ArrayList<Uri>) {
        showLog("URL", photos[0].toString())
        CropImage.activity(photos[0])
            .start(requireContext(), this)

    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.rel_profile -> {
                openPicker()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                val resultUri = result.uri
                Glide.with(requireActivity()).load(resultUri).into(binding.ivProfile)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }
    }

}