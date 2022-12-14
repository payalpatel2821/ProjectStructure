package com.task.newapp.ui.fragments.registration

import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.toSpannable
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.appizona.yehiahd.fastsave.FastSave
import com.task.newapp.App
import com.task.newapp.R
import com.task.newapp.api.ApiClient
import com.task.newapp.databinding.FragmentRegistrationStep4Binding
import com.task.newapp.interfaces.OnPageChangeListener
import com.task.newapp.models.ResponseGetUsername
import com.task.newapp.models.ResponseRegister
import com.task.newapp.models.ResponseVerifyOTP
import com.task.newapp.ui.activities.MainActivity
import com.task.newapp.utils.*
import com.task.newapp.utils.Constants.Companion.RegistrationStepsEnum
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


class RegistrationStep4Fragment : Fragment(), View.OnClickListener {

    private lateinit var timer: Timer
    var textChangedHandler = Handler() // declare it globally.

    //private var flagChangeUsername: Boolean = false
    private lateinit var binding: FragmentRegistrationStep4Binding
    private lateinit var onPageChangeListener: OnPageChangeListener
    private val mCompositeDisposable = CompositeDisposable()
    private val mCompositeDisposable1 = CompositeDisposable()
    private var flagUsername: Boolean = false
    private var flagTermsCondition: Boolean = false

    private val USERID_PATTERN = "^(?=.{4,12}\$)(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])\$"
    private val pattern: Pattern = Pattern.compile(USERID_PATTERN)


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
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_registration_step4,
            container,
            false
        )
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        setupKeyboardListener(binding.scrollview)
        binding.ivCopy.setOnClickListener(this)
        binding.tvChangeUsername.setOnClickListener(this)
        binding.layoutBack.tvBack.setOnClickListener(this)
        binding.btnDone.setOnClickListener(this)
        val strSpannable = binding.tvTerm.text.toSpannable() as SpannableString
        strSpannable.withClickableSpan("Terms of Use", onClickListener = ({ requireActivity().showToast("Clicked Terms of Use") }))
        strSpannable.withClickableSpan("Privacy Policy", onClickListener = ({ requireActivity().showToast("Clicked Privacy Policy") }))
        binding.tvTerm.text = strSpannable
        binding.tvTerm.movementMethod = LinkMovementMethod.getInstance();

        getUserNameFromAPI()
        checkAndEnable()
        binding.edtAccId.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(editable: Editable?) {
                //call API to verify username after delay of 2 seconds after checking if it is valid else show error
                if (binding.edtAccId.text.toString().isNotEmpty() && binding.edtAccId.text!!.trim().toString().length >= 4) {
                    if (isValid(binding.edtAccId.text.toString().trim())) {
                        flagUsername = true
                        textChangedHandler.postDelayed(runnable, 2000)
                        binding.tvErrorMessage.visibility = GONE
                    }else
                        showHideErrorMessageLayout(true, "Please enter correct username")
                } else {
                    flagUsername = false
                    hideVerificationProgressBar()
                    binding.ivIsVerifiedName.visibility = View.GONE

                    checkAndEnable()
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                //to show/hide loader icon and remove callbacks of runnable and show error is invalid username
                if (isValid(binding.edtAccId.text.toString().trim())) {
                    showVerificationProgressBar()
                    mCompositeDisposable1.clear()
                    textChangedHandler.removeCallbacksAndMessages(runnable)
                    binding.tvErrorMessage.visibility = GONE
                } else {
                    showHideErrorMessageLayout(true, "Please enter correct username")
                    return
                }
            }
        })

        binding.cbTerm.setOnCheckedChangeListener { buttonView, isChecked ->
            hideSoftKeyboard(requireActivity())
            flagTermsCondition = isChecked
            binding.edtAccId.clearFocus()
            checkAndEnable()
        }

        binding.edtAccId.clearFocus()

    }

    var runnable = Runnable {
        if (binding.edtAccId.text!!.trim().toString().length >= 4) {
            callVerifyUsername() //do whatever you want to do here.
        }
    }

    fun isValid(username: String?): Boolean {
        val matcher: Matcher = pattern.matcher(username)
        return matcher.matches()
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_copy -> {
                val clipboard = requireActivity().getSystemService(AppCompatActivity.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.text = binding.edtAccId.text.toString()

                requireActivity().showToast("Username copied...")
            }
            R.id.edt_acc_id -> {
                makeEditTextEditable()
            }
            R.id.tv_back -> {
                onPageChangeListener.onPageChange(RegistrationStepsEnum.STEP_3.index)
            }
            R.id.btn_done -> {
                //Home Screen
                if (binding.edtAccId.text.toString().trim().isEmpty()) {
                    requireActivity().showToast("Please enter username")
                } else {
                    if (binding.cbTerm.isChecked) {
                        callAPIRegister()
                    } else {
                        requireActivity().showToast(resources.getString(R.string.termmessage))
                    }
                }
            }
        }
    }

    private fun makeEditTextEditable() {
        binding.edtAccId.isFocusable = true
        binding.edtAccId.isFocusableInTouchMode = true
        binding.edtAccId.requestFocus()
        showSoftKeyboard(requireContext(), binding.edtAccId)
        binding.edtAccId.setSelection(binding.edtAccId.text!!.length)
    }

    private fun checkAndEnable() {
        enableOrDisableButton(requireContext(), flagTermsCondition && flagUsername, binding.btnDone)
    }

    private fun callAPIRegister() {
        try {
            if (!requireActivity().isNetworkConnected()) {
                requireActivity().showToast(getString(R.string.no_internet))
                return
            }
            openProgressDialog(activity)

            val flagMobile = FastSave.getInstance().getBoolean(Constants.typeCode, false)

            val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
            builder.addFormDataPart(Constants.first_name, FastSave.getInstance().getString(Constants.first_name, ""))
                .addFormDataPart(Constants.last_name, FastSave.getInstance().getString(Constants.last_name, ""))
                .addFormDataPart(Constants.password, FastSave.getInstance().getString(Constants.password, ""))
//                .addFormDataPart(Constants.password_confirmation, FastSave.getInstance().getString(Constants.password, ""))
                .addFormDataPart(Constants.latitude, "0")
                .addFormDataPart(Constants.longitude, "0")
                .addFormDataPart(Constants.device_token, Constants.deviceToken)
                .addFormDataPart(Constants.device_type, Constants.device_type_android)
                .addFormDataPart(Constants.account_id, binding.edtAccId.text.toString().trim())
                .addFormDataPart(Constants.email, if (flagMobile) "" else FastSave.getInstance().getString(Constants.mobile, ""))
                .addFormDataPart(Constants.mobile, if (flagMobile) FastSave.getInstance().getString(Constants.mobile, "") else "")

            if (flagMobile) {
                builder.addFormDataPart(Constants.register_type, resources.getString(R.string.mobile).lowercase())
            } else {
                builder.addFormDataPart(Constants.register_type, resources.getString(R.string.email).lowercase())
            }
            var imagePath = FastSave.getInstance().getString(Constants.profile_image, "")
            if (imagePath != null) {
                Log.e("callAPIRegister: ", imagePath)

                val file = File(imagePath.toString())
                if (file.exists()) {
                    val inputStream: InputStream = requireActivity().contentResolver.openInputStream(Uri.fromFile(File(imagePath))!!)!!
                    val bmp = BitmapFactory.decodeFile(file.absolutePath)
                    val bos = ByteArrayOutputStream()
                    if (bmp != null) {
                        bmp.compress(Bitmap.CompressFormat.JPEG, 100, bos)
                        builder.addFormDataPart(
                            Constants.profile_image, file.name, RequestBody.create(
                                MultipartBody.FORM,
//                                                    bos.toByteArray()
                                getBytes(inputStream)!!
//                                                    file
                            )
                        )
                    }
                }
            }

            //------------------------Call API-------------------------
            val requestBody: RequestBody = builder.build()

            mCompositeDisposable.add(
                ApiClient.create()
                    .doRegister(requestBody)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableObserver<ResponseRegister>() {
                        override fun onNext(responseRegister: ResponseRegister) {
                            hideProgressDialog()
                            activity!!.showToast(responseRegister.message)

                            if (responseRegister.success == 1) {
                                App.fastSave.saveString(Constants.prefToken, responseRegister.data.token)
                                App.fastSave.saveObject(Constants.prefUser, responseRegister.data.user)
                                App.fastSave.saveString(Constants.prefUserName, responseRegister.data.user.uName)
                                App.fastSave.saveInt(Constants.prefUserId, responseRegister.data.user.id)
                                App.fastSave.saveBoolean(Constants.isLogin, true)

                                //Main Screen
                                requireActivity().launchActivity<MainActivity> { }
                                requireActivity().finishAffinity()
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

    override fun onDestroy() {
        super.onDestroy()
        mCompositeDisposable.clear()
    }


    private fun callVerifyUsername() {
        try {
            if (!requireActivity().isNetworkConnected()) {
                requireActivity().showToast(getString(R.string.no_internet))
                return
            }

            mCompositeDisposable1.add(
                ApiClient.create()
                    .checkUsername(binding.edtAccId.text.toString().trim())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableObserver<ResponseVerifyOTP>() {
                        override fun onNext(responseVerifyOTP: ResponseVerifyOTP) {
                            if (responseVerifyOTP.success == 1) {
                                //Next Screen

                                binding.btnDone.text = resources.getString(R.string.done)
                            } else {
                                makeEditTextEditable()
                                binding.ivIsVerifiedName.visibility = View.GONE

                            }
                            showHideErrorMessageLayout(responseVerifyOTP.success == 0, "This username is already taken.")
                        }

                        override fun onError(e: Throwable) {
                            Log.v("onError: ", e.toString())
                            hideVerificationProgressBar()
                        }

                        override fun onComplete() {
                            hideVerificationProgressBar()
                        }
                    })
            )
        } catch (e: Exception) {
            e.printStackTrace()
            hideVerificationProgressBar()
        }
    }

    private fun showHideErrorMessageLayout(isError: Boolean, errorMsg: String) {
        if (isError) {
            flagUsername = false
            binding.ivIsVerifiedName.visibility = View.GONE
            binding.tvErrorMessage.visibility = View.VISIBLE
            binding.tvErrorMessage.text = errorMsg
        } else {
            flagUsername = true
            binding.tvErrorMessage.visibility = View.GONE
            binding.ivIsVerifiedName.visibility = View.VISIBLE
        }
        checkAndEnable()
    }


    private fun getUserNameFromAPI() {
        try {
            if (!requireActivity().isNetworkConnected()) {
                requireActivity().showToast(getString(R.string.no_internet))
                return
            }

            openProgressDialog(activity)

            mCompositeDisposable.add(
                ApiClient.create()
                    .getUsername(FastSave.getInstance().getString(Constants.first_name, ""))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableObserver<ResponseGetUsername>() {
                        override fun onNext(responseGetUsername: ResponseGetUsername) {
                            if (responseGetUsername.success == 1) {
                                binding.edtAccId.setText(responseGetUsername.data)
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

    private fun showVerificationProgressBar() {
        binding.pbVerification.visibility = View.VISIBLE
        binding.ivIsVerifiedName.visibility = View.GONE
    }

    private fun hideVerificationProgressBar() {
        binding.pbVerification.visibility = View.GONE
    }
}