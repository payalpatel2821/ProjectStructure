package com.task.newapp.ui.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.appizona.yehiahd.fastsave.FastSave
import com.task.newapp.App
import com.task.newapp.R
import com.task.newapp.api.ApiClient
import com.task.newapp.databinding.ActivityLoginBinding
import com.task.newapp.models.*
import com.task.newapp.realmDB.*
import com.task.newapp.realmDB.models.*
import com.task.newapp.utils.*
import com.task.newapp.utils.Constants.Companion.MessageEvents.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import io.realm.RealmList
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
        setupKeyboardListener(binding.scrollview) // call in OnCreate or similar

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
            binding.edtUsername.setText(App.fastSave.getString(Constants.prefUserNameRemember, "").toString())

            binding.edtPassword.setText(App.fastSave.getString(Constants.prefPasswordRemember, "").toString())

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
                launchActivity<RegistrationActivity> {
                    putExtra("From", Constants.Companion.ProfileNavigation.FROM_FOLLOWERS.name)
                }
            }
            R.id.txt_forgot_password -> {
                launchActivity<ForgotPasswordActivity> { }
            }
        }
    }

    private fun saveRemember() {
        App.fastSave.saveBoolean(Constants.prefIsRemember, binding.chkRemember.isChecked)
        App.fastSave.saveString(Constants.prefUserNameRemember, if (binding.chkRemember.isChecked) binding.edtUsername.text.toString().trim() else "")
        App.fastSave.saveString(Constants.prefPasswordRemember, if (binding.chkRemember.isChecked) binding.edtPassword.text.toString().trim() else "")
    }

    private fun saveUserInfoToPref(loginResponse: LoginResponse) {
        App.fastSave.saveString(Constants.prefToken, loginResponse.data.token)
        App.fastSave.saveObject(Constants.prefUser, loginResponse.data.user)
        App.fastSave.saveInt(Constants.prefUserId, loginResponse.data.user.id)
        App.fastSave.saveString(Constants.prefUserName, loginResponse.data.user.uName)
        App.fastSave.saveBoolean(Constants.isLogin, true)
        showLog("token", loginResponse.data.token.toString())
    }

    private fun callAPILogin() {
        try {
            val hashMap: HashMap<String, Any> = hashMapOf(
                Constants.user_name to binding.edtUsername.text.toString(),
                Constants.password to binding.edtPassword.text.toString(),
                Constants.latitude to "0",
                Constants.longitude to "0",
                Constants.device_token to Constants.deviceToken,
                Constants.device_type to Constants.device_type_android
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

                            if (loginResponse.success == 1) {
                                saveRemember()
                                saveUserInfoToPref(loginResponse)
                                insertLoginResponseToDatabase(loginResponse)
                                //Next Screen
                                launchActivity<MainActivity> { }
                                finish()
                            } else {
                                showToast(loginResponse.message)
                            }
                        }

                        override fun onError(e: Throwable) {
                            Log.v("onError: ", e.toString())
                            showToast(resources.getString(R.string.session_expire))
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
                binding.inputLayoutPassword.endIconDrawable!!.setTint(ContextCompat.getColor(this, R.color.red))
                requestFocus(this, binding.edtPassword)
                flagPass = false
                checkAndEnable()
                return false
            }
            binding.edtPassword.text.toString().length < 6 -> {
                binding.inputLayoutPassword.error = getString(R.string.enter_pass_validate)
                binding.inputLayoutPassword.endIconDrawable!!.setTint(ContextCompat.getColor(this, R.color.red))
                requestFocus(this, binding.edtPassword)
                flagPass = false
                checkAndEnable()
                return false
            }
            else -> {
                binding.inputLayoutPassword.isErrorEnabled = false
                binding.inputLayoutPassword.endIconDrawable!!.setTint(ContextCompat.getColor(this, R.color.black))
                flagPass = true
                checkAndEnable()
            }
        }
        return true
    }

    private fun validateUserName(): Boolean {
        if (binding.edtUsername.text.toString().trim().isEmpty()) {
            binding.inputLayoutName.error = getString(R.string.enter_your_username_or_phone_number)
            binding.inputLayoutName.endIconDrawable!!.setTint(ContextCompat.getColor(this, R.color.red))
            flagUser = false
            checkAndEnable()
            return false
        } else {
            binding.inputLayoutName.isErrorEnabled = false
            binding.inputLayoutName.endIconDrawable!!.setTint(ContextCompat.getColor(this, R.color.black))
            flagUser = true
            checkAndEnable()
        }
        return true
    }

    private fun checkAndEnable() {
        enableOrDisableButton(this@LoginActivity, flagUser && flagPass, binding.btnLogin)
    }

    /**
     * Insert Login response data to the Realm Database tables
     *
     * @param loginResponse : response object get from the login API
     */
    private fun insertLoginResponseToDatabase(loginResponse: LoginResponse) {
        insertUserSettingsData(prepareLoggedInUserSettings(loginResponse))
        //  insertFriendRequestData(prepareRequestDataForDB(loginResponse.getAllRequest))
        val (groups, groupUser, chatList, addUserChatList, chats) = prepareAllGroupDataForDB(loginResponse.getAllGroup)
        insertGroupData(
            groups as RealmList<Groups>,
            groupUser as RealmList<GroupUser>,
            chatList as RealmList<ChatList>,
            addUserChatList as RealmList<ChatList>,
            chats as RealmList<Chats>
        )
        loginResponse.hookData.let {
            insertHookData(prepareHookData(loginResponse.hookData))
        }
        insertArchiveData(prepareArchiveData(loginResponse.archiveData))
        insertBlockUserData(prepareBlockUserDataForDB(loginResponse.blockUser))
        insertBroadcastData(prepareBroadcastData(loginResponse.broadcast))
        insertFriendSettingsData(prepareFriendSettingsDataForDB(loginResponse.friendSettings))


    }

}