package com.task.newapp.ui.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.appizona.yehiahd.fastsave.FastSave
import com.task.newapp.R
import com.task.newapp.api.ApiClient
import com.task.newapp.databinding.ActivityLoginBinding
import com.task.newapp.models.LoginResponse
import com.task.newapp.utils.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.util.*


class LoginActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var binding: ActivityLoginBinding
    private val mCompositeDisposable = CompositeDisposable()
    private var flagUser: Boolean = false
    private var flagPass: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        initView()
    }

    private fun initView() {
        setRememberMe()
        binding.edtUsername.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                validateUserName()
            }
        })
        binding.edtPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                validatePassword()
            }
        })
    }

    private fun setRememberMe() {
        if (FastSave.getInstance().getBoolean(Constants.prefIsRemember, false)) {
            binding.edtUsername.setText(
                FastSave.getInstance().getString(Constants.prefUserNameRemember, "").toString()
            )

            binding.edtPassword.setText(
                FastSave.getInstance().getString(Constants.prefPasswordRemember, "").toString()
            )

            flagUser = true
            flagPass = true
        }
        binding.chkRemember.isChecked = FastSave.getInstance().getBoolean(Constants.prefIsRemember, false)

        checkAndEnable()
    }

    override fun onDestroy() {
        super.onDestroy()
        mCompositeDisposable.clear()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_login -> {
                if (!isNetworkConnected()) {
                    showToast(getString(R.string.no_internet))
                    return
                }

                if (!validatePassword()) {
                    return
                }
                if (!validateUserName()) {
                    return
                }
                callAPILogin()
            }
            R.id.txt_register -> {
                launchActivity<RegistrationActivity> { }
            }
            R.id.txt_forgot_password -> {
                launchActivity<ResetPasswordActivity> { }
            }
        }
    }

    private fun saveRemember() {
        FastSave.getInstance().saveBoolean(Constants.prefIsRemember, binding.chkRemember.isChecked)

        FastSave.getInstance()
            .saveString(Constants.prefUserNameRemember, if (binding.chkRemember.isChecked) binding.edtUsername.text.toString().trim() else "")

        FastSave.getInstance()
            .saveString(Constants.prefPasswordRemember, if (binding.chkRemember.isChecked) binding.edtPassword.text.toString().trim() else "")
    }

    private fun callAPILogin() {
        try {
            val hashMap: HashMap<String, Any> = hashMapOf(
                Constants.user_name to binding.edtUsername.text.toString(),
                Constants.password to binding.edtPassword.text.toString(),
                Constants.latitude to "0",
                Constants.longitude to "0",
                Constants.device_token to Constants.deviceToken,
                Constants.device_type to "Android"
            )

            openProgressDialog(this)

            mCompositeDisposable.add(
                ApiClient.create()
                    .doLogin(hashMap)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableObserver<LoginResponse>() {
                        override fun onNext(loginResponse: LoginResponse) {
                            Log.v("onNext: ", loginResponse.toString())
                            showToast(loginResponse.message)

                            if (loginResponse.success == 1) {
                                saveRemember()

                                FastSave.getInstance().saveObject(Constants.prefToken, loginResponse.data.token)
                                FastSave.getInstance().saveObject(Constants.prefUser, loginResponse.data.user)

                                FastSave.getInstance().saveObject(Constants.userClass, loginResponse.data.user)
                                FastSave.getInstance().saveBoolean(Constants.isLogin, true)

                                //Next Screen
                            }
                        }

                        override fun onError(e: Throwable) {
                            Log.v("onError: ", e.toString())
                            hideProgressDialog()
                            FastSave.getInstance().saveBoolean(Constants.isLogin, false)
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


    fun validatePassword(): Boolean {
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

    private fun validateUserName(): Boolean {
        if (binding.edtUsername.text.toString().trim().isEmpty()) {
            binding.inputLayoutName.error = getString(R.string.enter_username)
            flagUser = false
            checkAndEnable()
            return false
        } else {
            binding.inputLayoutName.isErrorEnabled = false
            flagUser = true
            checkAndEnable()
        }
        return true
    }

    private fun checkAndEnable() {
        enableOrDisableButton(this@LoginActivity, flagUser && flagPass, binding.btnLogin)
        /* if (flagUser && flagPass) {
             enableOrDisableButton(this@LoginActivity, true, binding.btnLogin)
         } else {
             enableOrDisableButton(this@LoginActivity, false, binding.btnLogin)
         }*/
    }

}