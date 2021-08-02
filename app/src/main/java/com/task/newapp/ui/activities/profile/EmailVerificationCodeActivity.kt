package com.task.newapp.ui.activities.profile

import `in`.aabhasjindal.otptextview.OTPListener
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.appizona.yehiahd.fastsave.FastSave
import com.task.newapp.App
import com.task.newapp.R
import com.task.newapp.api.ApiClient
import com.task.newapp.databinding.ActivityEmailVerificationCodeBinding
import com.task.newapp.models.CommonResponse
import com.task.newapp.models.User
import com.task.newapp.utils.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit

class EmailVerificationCodeActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityEmailVerificationCodeBinding
    private var countDownTimer: CountDownTimer? = null
    private lateinit var email: String
    private val mCompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_email_verification_code)
        email = intent.getStringExtra(Constants.bundle_email)!!
        forVerify()
        binding.segmentText.setOnCheckedChangeListener { group, checkedId ->
            flipAnimation(binding.llEditview)
            binding.edtEmailOrMobile.setText("")
            binding.edtEmailOrMobile.error = null
            afterClickSwitch()
            binding.edtEmailOrMobile.setHint(R.string.enter_email_address)
            binding.edtEmailOrMobile.inputType = InputType.TYPE_CLASS_TEXT
            binding.edtEmailOrMobile.filters = arrayOf(InputFilter.LengthFilter(100))
        }
        binding.otpView.otpListener = object : OTPListener {
            override fun onInteractionListener() {
                binding.otpView.resetState()
            }

            override fun onOTPComplete(otp: String) {
                callAPIVerifyOTPEmail(otp)
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
                binding.txtChangeemail.visibility = View.GONE

                if (isValidEmail(s.toString().trim())) {
                    binding.txtSendcode.visibility = View.VISIBLE
                } else {
                    binding.txtSendcode.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
    }

    private fun forVerify() {
        startTimer()
        binding.txtResend.isEnabled = false
        clearAll()
        setupKeyboardListener(binding.scrollview)
        afterSendCode()
    }

    private fun callAPIVerifyOTPEmail(otp: String) {
        if (!isNetworkConnected()) {
            showToast(getString(R.string.no_internet))
            return
        }
        try {
            openProgressDialog(this)

            val hashMap: HashMap<String, Any> = hashMapOf(
                Constants.email to email,
                Constants.code to otp
            )

            mCompositeDisposable.add(
                ApiClient.create()
                    .verifyEmailCode(hashMap)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableObserver<CommonResponse>() {
                        @RequiresApi(Build.VERSION_CODES.O)
                        override fun onNext(responseMyProfile: CommonResponse) {
                            showToast(responseMyProfile.message)
                            if (responseMyProfile.success == 1) {
                                val prefUser = FastSave.getInstance().getObject(Constants.prefUser, User::class.java)
                                prefUser.email = email
                                App.fastSave.saveObject(Constants.prefUser, prefUser)
                                finish()
                            }
                        }

                        override fun onError(e: Throwable) {
                            Log.v("onError: ", e.toString())
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

    private fun clearAll() {
        binding.edtEmailOrMobile.setText("")
        binding.otpView.setOTP("")
    }

    fun afterSendCode() {
        binding.edtEmailOrMobile.setText(email)
        binding.rbEmail.isEnabled = false
        binding.txtSendcode.visibility = View.GONE
        binding.txtChangeemail.text = resources.getString(R.string.change_email)
        binding.txtChangeemail.visibility = View.VISIBLE
        binding.llVerify.visibility = View.VISIBLE
        binding.edtEmailOrMobile.isEnabled = false
        binding.txtChangeemail.isEnabled = false
    }

    private fun afterClickSwitch() {
        binding.llVerify.visibility = View.GONE
        binding.edtEmailOrMobile.isEnabled = true
        binding.txtChangeemail.visibility = View.GONE
        clearOtpNTimer()
    }

    private fun clearOtpNTimer() {
        binding.otpView.setOTP("")
        countDownTimer?.let {
            countDownTimer!!.cancel()
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.tv_back -> {
                onBackPressed()
            }
            R.id.txt_changeemail -> {
                binding.edtEmailOrMobile.isEnabled = true
                afterClickSwitch()
            }
            R.id.txt_sendcode, R.id.txt_resend -> {
                clearOtpNTimer()
                binding.txtResend.isEnabled = false
                if (binding.edtEmailOrMobile.text.toString().trim().isEmpty()) {

                    binding.edtEmailOrMobile.error =
                        getString(R.string.enter_email_address)
                    return
                } else {
                    if (!isValidEmail(binding.edtEmailOrMobile.text.toString().trim())) {
                        binding.edtEmailOrMobile.error =
                            getString(R.string.enter_valid_address)
                        return
                    } else {
                        binding.edtEmailOrMobile.error = null
                    }
                }
                callAPISendCodeEmail()
            }
        }
    }

    private fun startTimer() {
        binding.txtTimer.text = "00:00"

        countDownTimer = object : CountDownTimer(30000, 1000) {

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

    private fun afterTimerStop() {
        binding.rbEmail.isEnabled = true
        binding.txtChangeemail.isEnabled = true
        binding.txtResend.isEnabled = true
    }

    private fun callAPISendCodeEmail() {
        if (!isNetworkConnected()) {
            showToast(getString(R.string.no_internet))
            return
        }
        try {
            openProgressDialog(this)

            val hashMap: HashMap<String, Any> = hashMapOf(
                Constants.email to email
            )

            mCompositeDisposable.add(
                ApiClient.create()
                    .changeEmailId(hashMap)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableObserver<CommonResponse>() {
                        @RequiresApi(Build.VERSION_CODES.O)
                        override fun onNext(responseMyProfile: CommonResponse) {
                            showToast(responseMyProfile.message)
                            if (responseMyProfile.success == 1) {
                                forVerify()
                            }
                        }

                        override fun onError(e: Throwable) {
                            Log.v("onError: ", e.toString())
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

    override fun onDestroy() {
        countDownTimer?.let {
            countDownTimer!!.cancel()
        }
        super.onDestroy()
    }
}