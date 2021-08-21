package com.task.newapp.ui.fragments.registration

import android.content.Context
import android.os.Bundle
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
import com.task.newapp.databinding.FragmentRegistrationStep3Binding
import com.task.newapp.interfaces.OnPageChangeListener
import com.task.newapp.utils.*
import com.task.newapp.utils.Constants.Companion.RegistrationStepsEnum


class RegistrationStep3Fragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentRegistrationStep3Binding
    private var flagPass: Boolean = false
    private var flagConfirmPass: Boolean = false

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
        clearAll()
        return binding.root
    }

    private fun clearAll() {
        binding.edtPassword.setText("")
        binding.edtConfirmPassword.setText("")
    }

    override fun onResume() {
        super.onResume()
        clearAll()
        binding.inputLayoutPassword.isErrorEnabled = false
        binding.inputLayoutPassword.endIconDrawable!!.setTint(resources.getColor(R.color.black))
        flagPass = false
        binding.inputLayoutConfirmPassword.isErrorEnabled = false
        binding.inputLayoutConfirmPassword.endIconDrawable!!.setTint(resources.getColor(R.color.black))
        flagConfirmPass = false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupKeyboardListener(binding.scrollview)
        clearAll()
        binding.layoutBack.tvBack.setOnClickListener(this)
        binding.btnNext.setOnClickListener(this)

        binding.edtPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (binding.edtPassword.hasFocus())
                    validatePassword()
            }
        })
        binding.edtConfirmPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (binding.edtConfirmPassword.hasFocus())
                    validateConfirmPassword()
            }
        })
    }

    fun validatePassword(): Boolean {
        when {
            binding.edtPassword.text.toString().trim().isEmpty() -> {
                binding.inputLayoutPassword.error = getString(R.string.enter_password)
                binding.inputLayoutPassword.endIconDrawable!!.setTint(resources.getColor(R.color.red))
                requestFocus(binding.edtPassword)
                flagPass = false
                checkAndEnable()
                return false
            }
            binding.edtPassword.text.toString().length < 6 -> {
                binding.inputLayoutPassword.error = getString(R.string.enter_pass_validate)
                binding.inputLayoutPassword.endIconDrawable!!.setTint(resources.getColor(R.color.red))
                requestFocus(binding.edtPassword)
                flagPass = false
                checkAndEnable()
                return false
            }
            else -> {
                binding.inputLayoutPassword.isErrorEnabled = false
                binding.inputLayoutPassword.endIconDrawable!!.setTint(resources.getColor(R.color.black))
                flagPass = true
                checkAndEnable()
            }
        }
        return true
    }

    fun validateConfirmPassword(): Boolean {
        when {
            binding.edtConfirmPassword.text.toString().trim().isEmpty() -> {
                binding.inputLayoutConfirmPassword.error = getString(R.string.enter_password)
                requestFocus(binding.edtConfirmPassword)
                flagConfirmPass = false
                checkAndEnable()
                return false
            }
            binding.edtConfirmPassword.text.toString().length < 6 -> {
                binding.inputLayoutConfirmPassword.error = getString(R.string.enter_pass_validate)
                requestFocus(binding.edtConfirmPassword)
                flagConfirmPass = false
                checkAndEnable()
                return false
            }
            else -> {
                binding.inputLayoutConfirmPassword.isErrorEnabled = false
                flagConfirmPass = true
                checkAndEnable()
            }
        }
        return true
    }

    private fun checkAndEnable() {
        if (flagConfirmPass && flagPass) {
            binding.btnNext.background = requireActivity().getDrawable(R.drawable.btn_rect_rounded_bg)
            binding.btnNext.isEnabled = true
        } else {
            binding.btnNext.background = requireActivity().getDrawable(R.drawable.btn_rect_rounded_bg_disable)
            binding.btnNext.isEnabled = false
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.tv_back -> {
                onPageChangeListener.onPageChange(RegistrationStepsEnum.STEP_2.index)
            }
            R.id.btn_next -> {
                hideSoftKeyboard(requireActivity())
                if (!requireActivity().isNetworkConnected()) {
                    requireActivity().showToast(getString(R.string.no_internet))
                    return
                }

                if (!validatePassword()) {
                    return
                }
                if (!validateConfirmPassword()) {
                    return
                }

                if (binding.edtPassword.text.toString().trim() == binding.edtConfirmPassword.text.toString().trim()) {
                    FastSave.getInstance().saveString(Constants.password, binding.edtPassword.text.toString().trim())

                    //Redirect to next registration step fragment
                    onPageChangeListener.onPageChange(RegistrationStepsEnum.STEP_4.index)
                } else {
                    binding.inputLayoutConfirmPassword.error = getString(R.string.password_and_confirm_password_must_be_same)
                    requestFocus(binding.edtConfirmPassword)
                    flagConfirmPass = false
                    checkAndEnable()
                }
            }
        }
    }

    private fun requestFocus(view: View) {
        if (view.requestFocus()) {
            requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }
    }


}