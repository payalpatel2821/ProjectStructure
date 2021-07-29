package com.task.newapp.ui.activities.chat.broadcast

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.View.OnClickListener
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.appizona.yehiahd.fastsave.FastSave
import com.bumptech.glide.Glide
import com.task.newapp.R
import com.task.newapp.adapter.post.SelectedFriendsListAdapter
import com.task.newapp.api.ApiClient
import com.task.newapp.databinding.ActivityCreateBroadcastBinding
import com.task.newapp.models.chat.CreateBroadcastResponse
import com.task.newapp.models.chat.SelectFriendWrapperModel
import com.task.newapp.realmDB.insertSingleBroadcastData
import com.task.newapp.realmDB.prepareSingleBroadcastData
import com.task.newapp.utils.*
import com.task.newapp.utils.compressor.SiliCompressor
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView.CropShape.RECTANGLE
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

class CreateBroadcastActivity : AppCompatActivity(), OnClickListener, MediaPickerFragment.Callback {
    private val TAG = javaClass.simpleName
    lateinit var binding: ActivityCreateBroadcastBinding
    private val mCompositeDisposable = CompositeDisposable()
    private var selectedFriendsList: ArrayList<SelectFriendWrapperModel> = ArrayList()
    private var selectedFriendsListAdapter: SelectedFriendsListAdapter? = null
    private var imageUri: String? = null

    private val selectedFriendsItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
        val obj = selectedFriendsList[position]
        obj.isChecked = false
        this.selectedFriendsList.remove(obj)
        selectedFriendsListAdapter?.removeSelected(obj)
        checkAndEnable(selectedFriendsList.isNotEmpty())

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_broadcast)
        initView()
    }

    private fun initView() {
        binding.toolbarLayout.txtTitle.text = getString(R.string.title_new_broadcast)
        setSupportActionBar(binding.toolbarLayout.activityMainToolbar)
        selectedFriendsList = intent.getSerializableExtra(Constants.bundle_selected_friends) as ArrayList<SelectFriendWrapperModel>
        setSelectedFriendsAdapter()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setSelectedFriendsAdapter() {
        if (selectedFriendsList.isNotEmpty()) {
            if (selectedFriendsListAdapter == null) {
                selectedFriendsListAdapter = SelectedFriendsListAdapter(this)
                selectedFriendsListAdapter?.setOnItemClickListener(selectedFriendsItemClickListener)
                binding.rvSelectedFriends.layoutManager = GridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false)
                binding.rvSelectedFriends.adapter = selectedFriendsListAdapter
            }
            selectedFriendsListAdapter?.doRefresh(selectedFriendsList)
        }
    }

    private fun checkAndEnable(enable: Boolean) {
        enableOrDisableButtonBgColor(this, enable, binding.btnCreateBroadcast)
    }

    private fun openPicker() {
        MediaPickerFragment.newInstance(
            multiple = false,
            allowCamera = true,
            pickerType = MediaPickerFragment.PickerType.PHOTO,
            maxSelection = 1,
            theme = R.style.ChiliPhotoPicker_Light,
        ).show(supportFragmentManager, "picker")
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_edit_img ->
                if (imageUri != null) {
                    FastSave.getInstance().saveString(Constants.profile_image, "")
                    binding.ivEditImg.setImageResource(R.drawable.ic_edit)
                    imageUri = null
                    binding.imgProfile.setImageResource(0)

                } else {
                    openPicker()
                }
            R.id.btn_create_broadcast -> {
                if (binding.edtBroadcastName.text.toString().isEmpty()) {
                    showToast("Please enter broadcast name.")
                    requestFocus(this, binding.edtBroadcastName)
                } else
                    callAPICreateBroadcast()
            }

        }
    }

    override fun onMediaPicked(mediaItems: ArrayList<Uri>) {
        imageUri = mediaItems[0].toString()
        CropImage.activity(mediaItems[0]).setAspectRatio(1, 1).setCropShape(RECTANGLE).start(this)

        /* val filePath: String = SiliCompressor.with(this).compress(imageUri,
             File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "temp_profile.jpg"), 1)*/
        //Glide.with(this).load(imageUri).into(binding.imgProfile)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                val resultUri = result.uri

                imageUri = resultUri.path
                binding.imgProfile.setColorFilter(0)
//                Glide.with(requireActivity()).load(resultUri).into(binding.ivProfile)
//                FastSave.getInstance().saveString(Constants.profile_image, resultUri.path.toString())
                binding.ivEditImg.setImageResource(R.drawable.ic_close)
                Log.e("callAPIRegister:result", resultUri.path.toString())


                //------------------------Add New------------------------

//                val filePath: String = SiliCompressor.with(activity).compress(resultUri.toString(), File(resultUri.path.toString()))
                val filePath: String = SiliCompressor.with(this).compress(
                    imageUri,
                    File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "temp_profile.jpg"), 0
                )
                Log.e("callAPI:resultPath", filePath)

                Glide.with(this).load(filePath).into(binding.imgProfile)


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }
    }

    private fun callAPICreateBroadcast() {
        if (!isNetworkConnected()) {
            showToast(getString(R.string.no_internet))
            return
        }
        openProgressDialog(this)

        val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
        builder.addFormDataPart(Constants.users, selectedFriendsList.filter(SelectFriendWrapperModel::isChecked)
            .joinToString(separator = ",") { it.id.toString() })
        builder.addFormDataPart(Constants.name, binding.edtBroadcastName.text.toString().trim())
        if (imageUri != null) {
            Log.e(TAG, imageUri!!.toString())

            val file = File(imageUri.toString())
            if (file.exists()) {
                val inputStream: InputStream = contentResolver.openInputStream(Uri.fromFile(File(imageUri))!!)!!
                val bmp = BitmapFactory.decodeFile(file.absolutePath)
                val bos = ByteArrayOutputStream()
                if (bmp != null) {
                    bmp.compress(Bitmap.CompressFormat.JPEG, 100, bos)
                    builder.addFormDataPart(
                        Constants.icon, file.name, RequestBody.create(
                            MultipartBody.FORM, /*bos.toByteArray()*/getBytes(inputStream)!! /*file*/
                        )
                    )
                }
            }
        }

        //------------------------Call API-------------------------
        val requestBody: RequestBody = builder.build()

        mCompositeDisposable.add(
            ApiClient.create()
                .createBroadcast(requestBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableObserver<CreateBroadcastResponse>() {
                    override fun onNext(createBroadcastResponse: CreateBroadcastResponse) {
                        hideProgressDialog()
                        if (createBroadcastResponse.success == 1) {
                            showToast("Broadcast created successfully...")
                            insertSingleBroadcastData(prepareSingleBroadcastData(createBroadcastResponse.data))
                            setResult(Activity.RESULT_OK)
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

    }
}