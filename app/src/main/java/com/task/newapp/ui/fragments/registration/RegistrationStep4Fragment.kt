package com.task.newapp.ui.fragments.registration

import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.appizona.yehiahd.fastsave.FastSave
import com.task.newapp.R
import com.task.newapp.api.ApiClient
import com.task.newapp.databinding.FragmentRegistrationStep4Binding
import com.task.newapp.interfaces.OnPageChangeListener
import com.task.newapp.models.ResponseRegister
import com.task.newapp.models.ResponseVerifyOTP
import com.task.newapp.utils.Constants
import com.task.newapp.utils.Constants.Companion.RegistrationStepsEnum
import com.task.newapp.utils.hideProgressDialog
import com.task.newapp.utils.openProgressDialog
import com.task.newapp.utils.showToast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream


class RegistrationStep4Fragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentRegistrationStep4Binding
    private lateinit var onPageChangeListener: OnPageChangeListener
    private val mCompositeDisposable = CompositeDisposable()

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
        binding.edtAccId.isEnabled = false
        binding.ivCopy.setOnClickListener(this)
        binding.tvChangeUsername.setOnClickListener(this)
        binding.layoutBack.tvBack.setOnClickListener(this)
        binding.btnDone.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_copy -> {
                val clipboard = requireActivity().getSystemService(AppCompatActivity.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.text = binding.edtAccId.text.toString()

                requireActivity().showToast("Username copied...")
            }
            R.id.tv_change_username -> {
                binding.edtAccId.isEnabled = true
                binding.edtAccId.isFocusable = true
                binding.edtAccId.requestFocus()
                binding.edtAccId.setSelection(binding.edtAccId.text!!.length)
            }
            R.id.tv_back -> {
                onPageChangeListener.onPageChange(RegistrationStepsEnum.STEP_3.index)
            }
            R.id.btn_done -> {
                //Home Screen
                if (binding.edtAccId.text.toString().trim().isEmpty()) {
                    requireActivity().showToast("Please enter username")
                } else {
                    //check username exists
                    callVerifyUsername()
                }
            }
        }
    }

    private fun callAPIRegister() {
        try {
            val flagMobile = FastSave.getInstance().getBoolean(Constants.typeCode, false)

            val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
            builder.addFormDataPart(Constants.first_name, FastSave.getInstance().getString(Constants.first_name, ""))
                .addFormDataPart(Constants.last_name, FastSave.getInstance().getString(Constants.last_name, ""))
                .addFormDataPart(Constants.email, if (flagMobile) "" else FastSave.getInstance().getString(Constants.mobile, ""))
                .addFormDataPart(Constants.mobile, if (flagMobile) FastSave.getInstance().getString(Constants.mobile, "") else "")
                .addFormDataPart(Constants.password, FastSave.getInstance().getString(Constants.password, ""))
                .addFormDataPart(Constants.password_confirmation, FastSave.getInstance().getString(Constants.password, ""))
                .addFormDataPart(Constants.latitude, "0")
                .addFormDataPart(Constants.longitude, "0")
                .addFormDataPart(Constants.device_token, Constants.deviceToken)
                .addFormDataPart(Constants.device_type, "Android")
                .addFormDataPart(Constants.account_id, binding.edtAccId.text.toString().trim())

            var imagePath = FastSave.getInstance().getString(Constants.profile_image, "")
            if (imagePath != null) {
                Log.e("callAPIRegister: ", imagePath)

                val file = File(imagePath.toString())
                if (file.exists()) {
                    val inputStream: InputStream =
                        requireActivity().contentResolver.openInputStream(Uri.fromFile(File(imagePath))!!)!!

                    val bmp =
                        BitmapFactory.decodeFile(file.absolutePath)
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

            openProgressDialog(activity)

            mCompositeDisposable.add(
                ApiClient.create()
                    .doRegister(requestBody)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableObserver<ResponseRegister>() {
                        override fun onNext(responseRegister: ResponseRegister) {
                            activity!!.showToast(responseRegister.message)

                            if (responseRegister.success == 1) {
                                FastSave.getInstance().saveObject(Constants.userClass, responseRegister.data.user)
                                FastSave.getInstance().saveBoolean(Constants.isLogin, true)

                                //Home Screen

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

    @Throws(IOException::class)
    fun getBytes(inputStream: InputStream): ByteArray? {
        val byteBuff = ByteArrayOutputStream()
        val buffSize = 1024
        val buff = ByteArray(buffSize)
        var len = 0
        while (inputStream.read(buff).also { len = it } != -1) {
            byteBuff.write(buff, 0, len)
        }
        return byteBuff.toByteArray()
    }

    private fun callVerifyUsername() {
        try {
            openProgressDialog(activity)

            mCompositeDisposable.add(
                ApiClient.create()
                    .checkUsername(binding.edtAccId.text.toString().trim())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableObserver<ResponseVerifyOTP>() {
                        override fun onNext(responseVerifyOTP: ResponseVerifyOTP) {
//                            activity!!.showToast(responseVerifyOTP.message)

                            if (responseVerifyOTP.success == 1) {
                                //Next Screen
                                callAPIRegister()
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