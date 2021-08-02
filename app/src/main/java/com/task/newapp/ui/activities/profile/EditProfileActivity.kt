package com.task.newapp.ui.activities.profile

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.View.VISIBLE
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.appizona.yehiahd.fastsave.FastSave
import com.bumptech.glide.Glide
import com.task.newapp.App
import com.task.newapp.R
import com.task.newapp.api.ApiClient
import com.task.newapp.databinding.ActivityEditProfileBinding
import com.task.newapp.models.CommonResponse
import com.task.newapp.models.ResponseFollowUnfollow
import com.task.newapp.models.ResponseMyProfile
import com.task.newapp.models.User
import com.task.newapp.utils.*
import com.task.newapp.utils.compressor.SiliCompressor
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import lv.chi.photopicker.MediaPickerFragment
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.util.*


class EditProfileActivity : AppCompatActivity(), MediaPickerFragment.Callback {
    private var change: Int = 0
    private var imageUri: String? = null
    private lateinit var binding: ActivityEditProfileBinding
    var flagFirst = false
    var flagLast = false
    private val mCompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile)
        initView()
    }

    private fun initView() {
        binding.toolbarLayout.txtSave.visibility = VISIBLE
        binding.toolbarLayout.txtTitle.text = getString(R.string.edit_my_profile)
        setSupportActionBar(binding.toolbarLayout.activityMainToolbar)

        val prefUser = FastSave.getInstance().getObject(Constants.prefUser, User::class.java)
        setData(prefUser)

        binding.edtFirstName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                validateFirstName()
                if (imageUri == "") {
                    if (binding.edtFirstName.text!!.isEmpty() && binding.edtLastName.text!!.isEmpty()) {
                        binding.ivProfile.setImageResource(R.drawable.ic_user_with_padding)
                        binding.ivProfile.setColorFilter(Color.argb(255, 255, 255, 255))
                        binding.txtDpName.visibility = View.GONE
                    } else {
                        binding.ivProfile.setImageResource(0)
                        binding.txtDpName.visibility = View.VISIBLE
                        binding.txtDpName.text = firstCharacter(binding.edtFirstName.text.toString().trim() + " " + binding.edtLastName.text.toString().trim())
                    }
                }
            }
        })

        binding.edtLastName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                validateLastName()
                if (imageUri == "") {
                    if (binding.edtFirstName.text!!.isEmpty() && binding.edtLastName.text!!.isEmpty()) {
                        binding.ivProfile.setImageResource(R.drawable.ic_user_with_padding)
                        binding.ivProfile.setColorFilter(Color.argb(255, 255, 255, 255))
                        binding.txtDpName.visibility = View.GONE
                    } else {
                        binding.ivProfile.setImageResource(0)
                        binding.txtDpName.visibility = View.VISIBLE
                        binding.txtDpName.text = firstCharacter(binding.edtFirstName.text.toString().trim() + " " + binding.edtLastName.text.toString().trim())
                    }
                }
            }
        })

        binding.inputBirthdate.setEndIconOnClickListener {
            binding.edtBirthdate.setText("")
        }

        binding.inputAnniversary.setEndIconOnClickListener {
            binding.edtAnniversary.setText("")
        }

    }

    private fun setData(prefUser: User) {
        imageUri = prefUser.profileImage
        if (imageUri != "") {
            binding.ivEditImg.setImageResource(R.drawable.ic_delete_profile)
            binding.txtDpName.visibility = View.GONE
        }
        binding.ivProfile.load(prefUser.profileImage, true)
        binding.edtFirstName.setText(prefUser.firstName)
        binding.edtLastName.setText(prefUser.lastName)
        binding.edtAbout.setText(prefUser.about)

        if (prefUser.dateOfBirth.isNullOrEmpty()) {
            binding.edtBirthdate.setText("")
        } else {
            binding.edtBirthdate.setText(prefUser.dateOfBirth.toString())
        }

        if (prefUser.anniversary.isNullOrEmpty()) {
            binding.edtAnniversary.setText("")
        } else {
            binding.edtAnniversary.setText(prefUser.anniversary.toString())
        }

        if (prefUser.mobile.isNullOrEmpty()) {
            binding.edtContactNo.setText("")
        } else {
            binding.edtContactNo.setText(prefUser.mobile.toString())
        }
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.iv_back -> {
                onBackPressed()
            }
            R.id.rel_profile -> {
                if (imageUri != "") {
                    FastSave.getInstance().saveString(Constants.profile_image, "")
                    binding.ivEditImg.setImageResource(R.drawable.ic_gallery)
                    imageUri = ""
                    callAPIChangeProfile("", 0)
                    if (binding.edtFirstName.text!!.isEmpty() && binding.edtLastName.text!!.isEmpty()) {
                        binding.ivProfile.setImageResource(R.drawable.ic_user_with_padding)
                        binding.ivProfile.setColorFilter(Color.argb(255, 255, 255, 255))
                        binding.txtDpName.visibility = View.GONE
                    } else {
                        binding.ivProfile.setImageResource(0)
                        binding.txtDpName.visibility = View.VISIBLE
                        binding.txtDpName.text = firstCharacter(binding.edtFirstName.text.toString().trim() + " " + binding.edtLastName.text.toString().trim())
                    }
                } else {
                    openPicker(supportFragmentManager)
                }
            }
            R.id.edt_birthdate -> {
                openCalender(binding.edtBirthdate)
            }
            R.id.edt_anniversary -> {
                openCalender(binding.edtAnniversary)
            }
            R.id.txt_save -> {
                if (!validateFirstName()) {
                    return
                }
                if (!validateLastName()) {
                    return
                }
                callAPIUpdateProfile()
            }
            R.id.txt_change_email -> {
                DialogUtils().showChangeEmailDialog(this, object : DialogUtils.DialogCallbacks {
                    override fun onPositiveButtonClick() {

                    }

                    override fun onNegativeButtonClick() {

                    }

                    override fun onDefaultButtonClick(actionName: String) {
                        callAPIChangeEmail(actionName)
                    }

                })
            }
            R.id.txt_change_password -> {
                DialogUtils().showChangePasswordDialog(this, object : DialogUtils.ConfirmationDialogCallbacks {
                    override fun onConfirmButtonClick(requestBody: HashMap<String, Any>) {
                        callAPIChangePassword(requestBody)
                    }
                })
            }
            R.id.ll_delete_account -> {
                launchActivity<DeleteAccountActivity> { }
            }
        }
    }

    private fun validateFirstName(): Boolean {
        when {
            binding.edtFirstName.text.toString().trim().isEmpty() -> {
                binding.inputLayoutFirst.error = getString(R.string.enter_first)
                requestFocus(binding.edtFirstName)
                flagFirst = false
                return false
            }
            binding.edtFirstName.text.toString().length < 3 -> {
                binding.inputLayoutFirst.error = getString(R.string.enter_firstname_validate)
                requestFocus(binding.edtFirstName)
                flagFirst = false
                return false
            }
            else -> {
                binding.inputLayoutFirst.isErrorEnabled = false
                flagFirst = true
            }
        }
        return true
    }

    private fun validateLastName(): Boolean {
        when {
            binding.edtLastName.text.toString().trim().isEmpty() -> {
                binding.inputLayoutLast.error = getString(R.string.enter_last)
                requestFocus(binding.edtLastName)
                flagLast = false
                return false
            }
            else -> {
                binding.inputLayoutLast.isErrorEnabled = false
                flagLast = true
            }
        }
        return true
    }

    override fun onMediaPicked(mediaItems: ArrayList<Uri>) {
        imageUri = mediaItems[0].toString()
        binding.ivEditImg.setImageResource(R.drawable.ic_delete_profile)
        binding.txtDpName.visibility = View.GONE

        val filePath: String = SiliCompressor.with(this).compress(
            imageUri,
            File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "temp_profile.jpg"),
            1
        )

        Glide.with(this).load(filePath).into(binding.ivProfile)
        FastSave.getInstance().saveString(Constants.profile_image, filePath)

        callAPIChangeProfile(filePath, 1)

    }

    private fun callAPIChangeEmail(email: String) {
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
                                launchActivity<EmailVerificationCodeActivity> {
                                    putExtra(Constants.bundle_email, email)
                                }
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

    private fun callAPIChangePassword(requestBody: HashMap<String, Any>) {
        if (!isNetworkConnected()) {
            showToast(getString(R.string.no_internet))
            return
        }
        try {
            openProgressDialog(this)

            mCompositeDisposable.add(
                ApiClient.create()
                    .changePassword(requestBody)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableObserver<CommonResponse>() {
                        @RequiresApi(Build.VERSION_CODES.O)
                        override fun onNext(responseMyProfile: CommonResponse) {
                            showToast(responseMyProfile.message)

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

    private fun callAPIChangeProfile(filePath: String, is_set: Int) {
        val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
        if (filePath != "") {
            Log.e("callAPIRegister: ", filePath)
            val file = File(filePath)
            if (file.exists()) {
                val inputStream: InputStream = contentResolver.openInputStream(Uri.fromFile(File(filePath))!!)!!
                val bmp = BitmapFactory.decodeFile(file.absolutePath)
                val bos = ByteArrayOutputStream()
                if (bmp != null) {
                    bmp.compress(Bitmap.CompressFormat.JPEG, 100, bos)
                    builder.addFormDataPart(
                        Constants.profile_image, file.name, RequestBody.create(
                            MultipartBody.FORM,
                            getBytes(inputStream)!!
                        )
                    )
                }
            }
        }
        builder.addFormDataPart(Constants.is_set, is_set.toString())
        val requestBody: RequestBody = builder.build()
        if (!isNetworkConnected()) {
            showToast(getString(R.string.no_internet))
            return
        }
        try {
            openProgressDialog(this)

            mCompositeDisposable.add(
                ApiClient.create()
                    .changeProfilePic(requestBody)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableObserver<ResponseFollowUnfollow>() {
                        @RequiresApi(Build.VERSION_CODES.O)
                        override fun onNext(responseMyProfile: ResponseFollowUnfollow) {
                            showToast(responseMyProfile.message)
                            if (responseMyProfile.success == 1) {
                                val prefUser = FastSave.getInstance().getObject(Constants.prefUser, User::class.java)
                                if ((responseMyProfile.data as String).isNullOrBlank()) {
                                    prefUser.profileImage = ""
                                } else {
                                    prefUser.profileImage = responseMyProfile.data as String
                                }
                                App.fastSave.saveObject(Constants.prefUser, prefUser)
                                change = 1
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

    private fun callAPIUpdateProfile() {

        val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
        builder.addFormDataPart(Constants.first_name, binding.edtFirstName.text.toString().trim())
            .addFormDataPart(Constants.last_name, binding.edtLastName.text.toString().trim())
            .addFormDataPart(Constants.about, binding.edtAbout.text.toString().trim())
            .addFormDataPart(Constants.date_of_birth, binding.edtBirthdate.text.toString().trim())
            .addFormDataPart(Constants.anniversary, binding.edtAnniversary.text.toString().trim())
            .addFormDataPart(Constants.mobile, binding.edtContactNo.text.toString().trim())

        val requestBody: RequestBody = builder.build()
        if (!isNetworkConnected()) {
            showToast(getString(R.string.no_internet))
            return
        }
        try {
            openProgressDialog(this)

            mCompositeDisposable.add(
                ApiClient.create()
                    .updateProfile(requestBody)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableObserver<ResponseMyProfile>() {
                        @RequiresApi(Build.VERSION_CODES.O)
                        override fun onNext(responseMyProfile: ResponseMyProfile) {
                            showToast(responseMyProfile.message)
                            if (responseMyProfile.success == 1) {
                                App.fastSave.saveObject(Constants.prefUser, responseMyProfile.data)
                                change = 1
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

    private fun openCalender(edt: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        var datePickerDialog = DatePickerDialog(
            this@EditProfileActivity,
            { datePicker, year, month, day -> edt.setText(year.toString() + "/" + (month + 1) + "/" + day.toString()) }, year, month, dayOfMonth
        )
        datePickerDialog.datePicker
        datePickerDialog.show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        val intent = Intent().putExtra("change", change)
        setResult(RESULT_OK, intent)
        finish()
        super.onBackPressed()
    }
}