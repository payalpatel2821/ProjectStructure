package com.task.newapp.ui.fragments.registration

import `in`.aabhasjindal.otptextview.OTPListener
import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.appizona.yehiahd.fastsave.FastSave
import com.task.newapp.R
import com.task.newapp.api.ApiClient
import com.task.newapp.databinding.FragmentRegistrationStep2Binding
import com.task.newapp.interfaces.OnPageChangeListener
import com.task.newapp.models.ResponseSendCode
import com.task.newapp.models.ResponseVerifyOTP
import com.task.newapp.ui.activities.RegistrationActivity
import com.task.newapp.ui.activities.ResetPasswordActivity
import com.task.newapp.utils.*
import com.task.newapp.utils.Constants.Companion.RegistrationStepsEnum
import com.task.newapp.utils.Constants.Companion.user_name
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit


class RegistrationStep2Fragment : Fragment(), View.OnClickListener {

    var flagMobile = true
    private var countDownTimer: CountDownTimer? = null
    private val mCompositeDisposable = CompositeDisposable()
    private lateinit var onPageChangeListener: OnPageChangeListener
    private lateinit var binding: FragmentRegistrationStep2Binding
    var fromForgotPass = false


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

        fromForgotPass = requireArguments().getBoolean("fromForgotPass")
        binding.txtResend.isEnabled = false

        clearAll()
        return binding.root
    }

    private fun clearAll() {
        binding.edtEmailOrMobile.setText("")
        binding.otpView.setOTP("")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupKeyboardListener(binding.scrollview)
        binding.edtEmailOrMobile.setHint(R.string.enter_mobile_number)
        binding.segmentText.setOnCheckedChangeListener { group, checkedId ->

            flipAnimation(binding.llEditview)
            binding.edtEmailOrMobile.setText("")
            binding.edtEmailOrMobile.error = null
            afterClickSwitch()
            when (checkedId) {
                R.id.rb_mobile -> {
                    flagMobile = true
                    binding.txtPhone.visibility = VISIBLE
                    binding.vLine.visibility = VISIBLE
                    binding.edtEmailOrMobile.setHint(R.string.enter_mobile_number)
                    binding.edtEmailOrMobile.inputType =
                        InputType.TYPE_CLASS_PHONE or InputType.TYPE_NUMBER_VARIATION_PASSWORD
                    binding.edtEmailOrMobile.filters = arrayOf(InputFilter.LengthFilter(10))
                }
                R.id.rb_email -> {
                    flagMobile = false
                    binding.txtPhone.visibility = GONE
                    binding.vLine.visibility = GONE
                    binding.edtEmailOrMobile.setHint(R.string.enter_email_address)
                    binding.edtEmailOrMobile.inputType = InputType.TYPE_CLASS_TEXT
                    binding.edtEmailOrMobile.filters = arrayOf(InputFilter.LengthFilter(100))
                }
            }
        }
        binding.otpView.otpListener = object : OTPListener {
            override fun onInteractionListener() {
                binding.otpView.resetState()
            }

            override fun onOTPComplete(otp: String) {
                hideSoftKeyboard(requireActivity())
                if (fromForgotPass) {
                    callAPIVerifyOTPForgotPass(otp)
                } else {
                    callAPIVerifyOTPNormal(otp)
                }
            }
        }
        binding.layoutBack.tvBack.setOnClickListener(this)
        binding.txtSendcode.setOnClickListener(this)
        binding.txtResend.setOnClickListener(this)
        binding.txtChangeemail.setOnClickListener(this)

        binding.edtEmailOrMobile.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.txtChangeemail.visibility = GONE
                if (!flagMobile) {
                    if (isValidEmail(s.toString().trim())) {
                        binding.txtSendcode.visibility = VISIBLE
                    } else {
                        binding.txtSendcode.visibility = GONE
                    }
                } else {
                    if (isValidPhoneNumber(s.toString().trim())) {
                        binding.txtSendcode.visibility = VISIBLE
                    } else {
                        binding.txtSendcode.visibility = GONE
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
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
            R.id.txt_changeemail -> {
                binding.edtEmailOrMobile.isEnabled = true
                afterClickSwitch()
            }
            R.id.txt_sendcode, R.id.txt_resend -> {
                hideSoftKeyboard(requireActivity())
                clearOtpNTimer()
                binding.txtResend.isEnabled = false

                if (binding.edtEmailOrMobile.text.toString().trim().isEmpty()) {

                    binding.edtEmailOrMobile.error =
                        if (flagMobile) getString(R.string.enter_mobile_number) else getString(R.string.enter_email_address)
                    return
                } else if (!flagMobile) {
                    if (!isValidEmail(binding.edtEmailOrMobile.text.toString().trim())) {
                        binding.edtEmailOrMobile.error =
                            getString(R.string.enter_valid_address)
                        return
                    } else {
                        binding.edtEmailOrMobile.error = null
                    }
                }

                if (fromForgotPass) {
                    callAPISendCodeForgot()
                } else {
                    callAPISendCodeNormal()
                }
            }
        }
    }

    private fun startTimer() {
//        if (countDownTimer != null) {
//            countDownTimer.cancel()
        binding.txtTimer.text = "00:00"
//        }

        countDownTimer = object : CountDownTimer(120000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                binding.txtTimer.text = (millisUntilFinished / 1000).toString()

                binding.txtTimer.text = getString(
                    R.string.text_count_down_timer,
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))
                )
            }

            override fun onFinish() {
                binding.txtTimer.text = getString(R.string.text_count_down_reset)

                afterTimerStop()
            }
        }
        countDownTimer!!.start()
    }

    override fun onDestroy() {
        super.onDestroy()

        mCompositeDisposable.clear()
    }

    private fun callAPISendCodeForgot() {
        try {
            val hashMap: HashMap<String, Any> = hashMapOf(
                Constants.typeCode to if (flagMobile) Constants.mobile else Constants.email,
                if (flagMobile)
                    Constants.mobile to binding.edtEmailOrMobile.text.toString().trim()
                else
                    Constants.email to binding.edtEmailOrMobile.text.toString().trim()
            )

            openProgressDialog(activity)

            mCompositeDisposable.add(
                ApiClient.create()
                    .sendCodeForgotPass(hashMap)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableObserver<ResponseSendCode>() {
                        override fun onNext(responseSendCode: ResponseSendCode) {
                            activity!!.showToast(responseSendCode.message)

                            if (responseSendCode.success == 1) {
                                startTimer()
                                afterSendCode()
                            }
                        }

                        override fun onError(e: Throwable) {
                            Log.v("onError: ", e.toString())
                            activity!!.showToast(resources.getString(R.string.session_expire))
                            hideProgressDialog()
                        }

                        override fun onComplete() {
                            hideProgressDialog()
                        }
                    })
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun callAPIVerifyOTPForgotPass(otp: String) {
        try {
            val hashMap: HashMap<String, Any> = hashMapOf(
                Constants.user_name to binding.edtEmailOrMobile.text.toString().trim(),
                Constants.code to otp
            )

            openProgressDialog(activity)

            mCompositeDisposable.add(
                ApiClient.create()
                    .verifyOTPForgotPass(hashMap)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableObserver<ResponseVerifyOTP>() {
                        override fun onNext(responseVerifyOTP: ResponseVerifyOTP) {
                            activity!!.showToast(responseVerifyOTP.message)

                            if (responseVerifyOTP.success == 1) {
                                //Next Screen
                                if (context is RegistrationActivity) {
                                    FastSave.getInstance().saveBoolean(Constants.typeCode, flagMobile)
                                    FastSave.getInstance().saveString(Constants.mobile, binding.edtEmailOrMobile.text.toString().trim())

                                    if (context is RegistrationActivity) {
                                        clearAll()
                                        afterTimerStop()
                                        afterClickSwitch()
                                        onPageChangeListener.onPageChange(RegistrationStepsEnum.STEP_3.index)
                                    }
                                } else {
                                    //Reset Password
                                    requireActivity().launchActivity<ResetPasswordActivity> {
                                        putExtra(user_name, binding.edtEmailOrMobile.text.toString().trim())

                                    }
                                }
                            }
                        }

                        override fun onError(e: Throwable) {
                            Log.v("onError: ", e.toString())
                            activity!!.showToast(resources.getString(R.string.session_expire))
                            hideProgressDialog()
                        }

                        override fun onComplete() {
                            hideProgressDialog()
                        }
                    })
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun callAPISendCodeNormal() {
        try {
            val hashMap: HashMap<String, Any> = hashMapOf(
                Constants.typeCode to if (flagMobile) Constants.mobile else Constants.email,
                if (flagMobile)
                    Constants.mobile to binding.edtEmailOrMobile.text.toString().trim()
                else
                    Constants.email to binding.edtEmailOrMobile.text.toString().trim()
            )

            openProgressDialog(activity)

            mCompositeDisposable.add(
                ApiClient.create()
                    .sendCodeNormalUrl(hashMap)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableObserver<ResponseSendCode>() {
                        override fun onNext(responseSendCode: ResponseSendCode) {
                            activity!!.showToast(responseSendCode.message)

                            if (responseSendCode.success == 1) {
                                startTimer()
                                afterSendCode()
                            }
                        }

                        override fun onError(e: Throwable) {
                            Log.v("onError: ", e.toString())
                            activity!!.showToast(resources.getString(R.string.session_expire))
                            hideProgressDialog()
                        }

                        override fun onComplete() {
                            hideProgressDialog()
                        }
                    })
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun afterSendCode() {
        binding.rbEmail.isEnabled = false
        binding.rbMobile.isEnabled = false

        binding.txtSendcode.visibility = GONE
        if (flagMobile){
            binding.txtChangeemail.text=resources.getString(R.string.change_mobile)
        }else{
            binding.txtChangeemail.text=resources.getString(R.string.change_email)
        }
        binding.txtChangeemail.visibility = VISIBLE
        binding.llVerify.visibility = VISIBLE

        binding.edtEmailOrMobile.isEnabled = false
        binding.txtChangeemail.isEnabled = false
    }

    fun afterTimerStop() {
        binding.rbEmail.isEnabled = true
        binding.rbMobile.isEnabled = true
        binding.txtChangeemail.isEnabled = true
        binding.txtResend.isEnabled = true
    }

    fun afterClickSwitch() {
        binding.llVerify.visibility = GONE
        binding.edtEmailOrMobile.isEnabled = true
        binding.txtChangeemail.visibility = GONE
        clearOtpNTimer()
//        binding.txtSendcode.visibility = VISIBLE
    }

    fun clearOtpNTimer(){
        binding.otpView.setOTP("")
        countDownTimer?.let {
            countDownTimer!!.cancel()
        }
    }

    override fun onDetach() {
        countDownTimer?.let {
            countDownTimer!!.cancel()
        }
        super.onDetach()
    }

    private fun callAPIVerifyOTPNormal(otp: String) {
        try {
            val hashMap: HashMap<String, Any> = hashMapOf(
                Constants.user_name to binding.edtEmailOrMobile.text.toString().trim(),
                Constants.code to otp
            )

            openProgressDialog(activity)

            mCompositeDisposable.add(
                ApiClient.create()
                    .verifyOTPNormal(hashMap)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableObserver<ResponseVerifyOTP>() {
                        override fun onNext(responseVerifyOTP: ResponseVerifyOTP) {
                            if (responseVerifyOTP.success == 1) {
                                //Next Screen
                                if (context is RegistrationActivity) {
                                    FastSave.getInstance().saveBoolean(Constants.typeCode, flagMobile)
                                    FastSave.getInstance().saveString(Constants.mobile, binding.edtEmailOrMobile.text.toString().trim())

                                    if (context is RegistrationActivity) {
                                        clearAll()
                                        afterTimerStop()
                                        afterClickSwitch()
                                        onPageChangeListener.onPageChange(RegistrationStepsEnum.STEP_3.index)
                                    }
                                }
                            } else {
                                activity!!.showToast(responseVerifyOTP.message)
                            }
                        }

                        override fun onError(e: Throwable) {
                            Log.v("onError: ", e.toString())
                            activity!!.showToast(resources.getString(R.string.session_expire))
                            hideProgressDialog()
                            binding.otpView.setOTP("")
                        }

                        override fun onComplete() {
                            hideProgressDialog()
                        }
                    })
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}