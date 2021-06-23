package com.task.newapp.ui.fragments.registration

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.task.newapp.R
import com.task.newapp.databinding.FragmentRegistrationStep3Binding
import com.task.newapp.interfaces.OnPageChangeListener
import com.task.newapp.utils.Constants.Companion.RegistrationStepsEnum


class RegistrationStep3Fragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentRegistrationStep3Binding


    private lateinit var onPageChangeListener: OnPageChangeListener
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
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_registration_step3,
            container,
            false
        )
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.layoutBack.tvBack.setOnClickListener(this)
        binding.btnNext.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.tv_back -> {
                onPageChangeListener.onPageChange(RegistrationStepsEnum.STEP_2.index)
            }
            R.id.btn_next -> {
                //Redirect to next registration step fragment
                onPageChangeListener.onPageChange(RegistrationStepsEnum.STEP_4.index)

            }
        }
    }

}