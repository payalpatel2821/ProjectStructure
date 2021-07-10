package com.task.newapp.ui.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.task.newapp.R
import com.task.newapp.api.ApiClient
import com.task.newapp.databinding.ActivityResetPasswordBinding
import com.task.newapp.models.CommonResponse
import com.task.newapp.utils.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.util.*

class ResetPasswordActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var binding: ActivityResetPasswordBinding
    private val mCompositeDisposable = CompositeDisposable()
    private var flagPass: Boolean = false
    private var flagConfirmPass: Boolean = false
    private var userName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_reset_password)
        userName = intent.getStringExtra(Constants.user_name).toString()
        initView()
    }

    private fun initView() {
        binding.layoutBack.tvBack.setOnClickListener(this)
        binding.edtPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                validatePassword()
            }
        })
        binding.edtConfirmPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                validateConfirmPassword()
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        mCompositeDisposable.clear()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_submit -> {
                if (!isNetworkConnected()) {
                    showToast(getString(R.string.no_internet))
                    return
                }
                if (!validatePassword()) {
                    return
                }
                if (!validateConfirmPassword()) {
                    return
                }
                callAPIResetPassword()
            }
            R.id.tv_back -> {
                finish()
            }
        }
    }

    private fun validatePassword(): Boolean {
        when {
            binding.edtPassword.text.toString().trim().isEmpty() -> {
                binding.inputLayoutPassword.error = getString(R.string.enter_password)
                requestFocus(this, binding.edtPassword)
                flagPass = false
                checkAndEnable()
                return false
            }
            binding.edtPassword.text.toString().length < 6 -> {
                binding.inputLayoutPassword.error = getString(R.string.enter_pass_validate)
                requestFocus(this, binding.edtPassword)
                flagPass = false
                checkAndEnable()
                return false
            }
            else -> {
                binding.inputLayoutPassword.isErrorEnabled = false
                flagPass = true
                checkAndEnable()
            }
        }
        return true
    }

    private fun validateConfirmPassword(): Boolean {
        when {

            binding.edtConfirmPassword.text.toString().trim().isEmpty() -> {
                binding.inputLayoutConfirmPassword.error = getString(R.string.enter_confirm_password)
                requestFocus(this, binding.edtConfirmPassword)
                flagConfirmPass = false
                checkAndEnable()
                return false
            }
            !binding.edtConfirmPassword.text.toString().equals(binding.edtPassword.text.toString()) -> {
                binding.inputLayoutConfirmPassword.error = getString(R.string.password_and_confirm_password_not_match)
                requestFocus(this, binding.edtConfirmPassword)
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
        enableOrDisableButton(this@ResetPasswordActivity, flagPass && flagConfirmPass, binding.btnSubmit)

    }

    private fun callAPIResetPassword() {
        try {
            val hashMap: HashMap<String, Any> = hashMapOf(
                Constants.user_name to userName,
                Constants.password to binding.edtPassword.text.toString()
            )

            openProgressDialog(this)

            mCompositeDisposable.add(
                ApiClient.create()
                    .doResetPassword(hashMap)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableObserver<CommonResponse>() {
                        override fun onNext(commonResponse: CommonResponse) {
                            Log.v("onNext: ", commonResponse.toString())
                            showToast(commonResponse.message)

                            if (commonResponse.success == 1) {
                                //Next Screen
                                launchActivity<LoginActivity> { }
                                finishAffinity()
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
}