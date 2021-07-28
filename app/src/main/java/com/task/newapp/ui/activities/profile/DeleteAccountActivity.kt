package com.task.newapp.ui.activities.profile

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.task.newapp.R
import com.task.newapp.api.ApiClient
import com.task.newapp.databinding.ActivityDeleteAccountBinding
import com.task.newapp.models.CommonResponse
import com.task.newapp.utils.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.util.*

class DeleteAccountActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDeleteAccountBinding
    private val mCompositeDisposable = CompositeDisposable()
    var isEmail:Boolean=false
    var isPassword:Boolean=false
    var isReason:Boolean=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete_account)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_delete_account)
        initView()
    }

    private fun initView() {
        binding.edtEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                isEmail = binding.edtEmail.text.toString().trim().isNotEmpty()
                binding.inputLayoutEmail.isErrorEnabled = false
                checkEnableOrDisable()
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
        binding.edtPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                isPassword = binding.edtPassword.text.toString().trim().isNotEmpty()
                binding.inputLayoutPassword.isErrorEnabled = false
                checkEnableOrDisable()
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
        binding.edtReason.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                isReason = binding.edtReason.text.toString().trim().isNotEmpty()
                binding.inputLayoutReason.isErrorEnabled = false
                checkEnableOrDisable()
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
        checkEnableOrDisable()
    }

    private fun checkEnableOrDisable() {
        enableOrDisableButton(this@DeleteAccountActivity, isEmail&&isPassword&&isReason, binding.btnDeleteAccount)
    }

    private fun callAPIDeleteAccount() {
        if (!isNetworkConnected()) {
            showToast(getString(R.string.no_internet))
            return
        }
        try {
            openProgressDialog(this)

            val hashMap: HashMap<String, Any> = hashMapOf(
                Constants.email to binding.edtEmail.text.toString(),
                Constants.password to binding.edtPassword.text.toString(),
                Constants.delete_reason to binding.edtReason.text.toString()
            )

            mCompositeDisposable.add(
                ApiClient.create()
                    .deleteAccount(hashMap)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableObserver<CommonResponse>() {
                        @RequiresApi(Build.VERSION_CODES.O)
                        override fun onNext(responseMyProfile: CommonResponse) {
                            showToast(responseMyProfile.message)
                            if (responseMyProfile.success == 1) {
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

    fun onClick(view: View) {
        when (view.id) {
            R.id.btn_delete_account -> {
                when {
                    binding.edtEmail.text.isNullOrBlank() -> {
                        binding.inputLayoutEmail.error = resources.getString(R.string.enter_email_address)
                        return
                    }
                    !isValidEmail(binding.edtEmail.text.toString())->{
                        binding.inputLayoutEmail.error = resources.getString(R.string.enter_valid_address)
                        return
                    }
                    binding.edtPassword.text.isNullOrBlank() -> {
                        binding.inputLayoutPassword.error = resources.getString(R.string.enter_password)
                        return
                    }
                    binding.edtReason.text.isNullOrBlank() -> {
                        binding.inputLayoutReason.error = resources.getString(R.string.enter_reason)
                        return
                    }
                    else -> {
                        DialogUtils().showConfirmationYesNoDialog(this, resources.getString(R.string.delete_account), resources.getString(R.string.confirm_delete_msg), object : DialogUtils.DialogCallbacks {
                            override fun onPositiveButtonClick() {
                                callAPIDeleteAccount()
                            }

                            override fun onNegativeButtonClick() {
                            }

                            override fun onDefaultButtonClick(actionName: String) {
                            }

                        })
                    }
                }
            }
            R.id.tv_back -> {
                onBackPressed()
            }
        }
    }

}