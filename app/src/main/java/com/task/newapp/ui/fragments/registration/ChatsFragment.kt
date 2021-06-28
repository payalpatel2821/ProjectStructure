package com.task.newapp.ui.fragments.registration

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.appizona.yehiahd.fastsave.FastSave
import com.bumptech.glide.Glide
import com.task.newapp.R
import com.task.newapp.databinding.FragmentChatBinding
import com.task.newapp.databinding.FragmentRegistrationStep1Binding
import com.task.newapp.interfaces.OnPageChangeListener
import com.task.newapp.utils.Constants
import com.task.newapp.utils.Constants.Companion.RegistrationStepsEnum
import com.task.newapp.utils.showLog
import com.theartofdev.edmodo.cropper.CropImage
import lv.chi.photopicker.PhotoPickerFragment
import java.io.File


class ChatsFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentChatBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_chat,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onClick(v: View?) {

    }

}