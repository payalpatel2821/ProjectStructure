package com.task.newapp.ui.fragments.registration

import `in`.aabhasjindal.otptextview.OTPListener
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.task.newapp.R
import com.task.newapp.databinding.FragmentRegistrationStep2Binding
import com.task.newapp.interfaces.OnPageChangeListener
import com.task.newapp.ui.activities.RegistrationActivity
import com.task.newapp.utils.Constants.Companion.RegistrationStepsEnum


class RegistrationStep2Fragment : Fragment(), View.OnClickListener {
    private lateinit var onPageChangeListener: OnPageChangeListener

    private lateinit var binding: FragmentRegistrationStep2Binding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is RegistrationActivity)
            onPageChangeListener = context as OnPageChangeListener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_registration_step2,
            container,
            false
        )
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.edtEmailOrMobile.setHint(R.string.enter_mobile_number)
        binding.segmentText.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rb_mobile -> {
                    binding.txtPhone.visibility = VISIBLE
                    binding.vLine.visibility = VISIBLE
                    binding.edtEmailOrMobile.setHint(R.string.enter_mobile_number)
                }
                R.id.rb_email -> {
                    binding.txtPhone.visibility = GONE
                    binding.vLine.visibility = GONE
                    binding.edtEmailOrMobile.setHint(R.string.enter_email_address)
                }
            }
        }
        binding.otpView.otpListener = object : OTPListener {
            override fun onInteractionListener() {
                binding.otpView.resetState()
            }

            override fun onOTPComplete(otp: String) {
                if (context is RegistrationActivity) {
                    onPageChangeListener.onPageChange(RegistrationStepsEnum.STEP_3.index)
                }
            }
        }
        binding.layoutBack.tvBack.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.tv_back -> {
                if (context is RegistrationActivity) {
                    onPageChangeListener.onPageChange(RegistrationStepsEnum.STEP_1.index)
                } else {
                    requireActivity().onBackPressed()
                }
            }
        }
    }

}