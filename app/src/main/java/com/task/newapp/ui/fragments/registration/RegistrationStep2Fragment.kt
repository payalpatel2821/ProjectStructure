package com.task.newapp.ui.fragments.registration

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

class RegistrationStep2Fragment : Fragment() {

    private lateinit var binding: FragmentRegistrationStep2Binding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
    }

}