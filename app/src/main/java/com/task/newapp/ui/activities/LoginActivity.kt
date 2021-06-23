package com.task.newapp.ui.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        FastSave.init(this)

        //loginClick()
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
                if (isValid()) {
                    callAPILogin()
                }
            }
            R.id.txt_forgot_password->{
                launchActivity<ForgotPasswordActivity> {  }
            }
        }
    }

    private fun isValid(): Boolean {
        if (binding.edtEmailOrMobile.text!!.isNotEmpty()) {
//            if (isValidEmail(binding.edtUsername.text.toString())) {
            if (binding.edtPassword.text!!.isNotEmpty()) {
                if (binding.edtPassword.text.toString().length > 6) {
                    return true
                } else {
                    //showToast(getString(R.string.enter_pass_validate))
                    binding.inputLayoutPassword.error = getString(R.string.enter_pass_validate)
                }
            } else {
                //showToast(getString(R.string.enter_pass))
                binding.inputLayoutPassword.error = getString(R.string.enter_password)
            }
//            } else {
//                showToast(getString(R.string.enter_valid_address))
//            }
        } else {
            //showToast(getString(R.string.enter_email_address))
            binding.inputLayoutName.error = getString(R.string.enter_email_address)
        }
        return false
    }

    private fun callAPILogin() {
        val hashMap: HashMap<String, Any> = hashMapOf(
            Constants.device_token to "dcW4j1KARIe0LS-_SbsbyD:APA91bEcIbE4WeHntuqBBzzghCLcHaUNhgE0G-cFIKmFK0QZewMRkscfX6hrGM-of5A_TETydHnzv981xKd2NYxr13xE0BqWqQ6Hu18SuZM2-dMt2g_xheFCYBhottjyYTVfUCwrCM25",
            Constants.device_type to "Android",
            Constants.email to "payaladbiz@gmail.com",
            Constants.latitude to "21.14805",
            Constants.longitude to "72.7602871",
            Constants.password to "123123"
        )

        mCompositeDisposable.add(
            ApiClient.create()
                .doLogin(hashMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableObserver<LoginResponse>() {
                    override fun onNext(loginResponse: LoginResponse) {
                        Log.v("onNext: ", loginResponse.toString())
                        FastSave.getInstance().saveObject("user", loginResponse.data.user)
                    }

                    override fun onError(e: Throwable) {
                        Log.v("onError: ", e.toString())
                    }

                    override fun onComplete() {
                        Toast.makeText(this@LoginActivity, "Success", Toast.LENGTH_SHORT).show()
                        Log.v("onComplete: ", "onComplete")
                    }
                })
        )
    }

}