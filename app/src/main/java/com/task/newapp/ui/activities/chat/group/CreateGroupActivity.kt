package com.task.newapp.ui.activities.chat.group

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.View.OnClickListener
import android.widget.AdapterView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.appizona.yehiahd.fastsave.FastSave
import com.bumptech.glide.Glide
import com.task.newapp.R
import com.task.newapp.adapter.post.SelectedFriendsListAdapter
import com.task.newapp.databinding.ActivityCreateGroupBinding
import com.task.newapp.models.chat.SelectFriendWrapperModel
import com.task.newapp.realmDB.getSelectedFriends
import com.task.newapp.realmDB.models.FriendRequest
import com.task.newapp.realmDB.prepareSelectFriendWrapperModelList
import com.task.newapp.ui.activities.BaseAppCompatActivity
import com.task.newapp.utils.Constants
import com.task.newapp.utils.compressor.SiliCompressor
import com.task.newapp.utils.enableOrDisableButtonBgColor
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView.CropShape.RECTANGLE
import io.reactivex.disposables.CompositeDisposable
import lv.chi.photopicker.MediaPickerFragment
import java.io.File

class CreateGroupActivity : BaseAppCompatActivity(), OnClickListener, MediaPickerFragment.Callback {

    lateinit var binding: ActivityCreateGroupBinding
    private val mCompositeDisposable = CompositeDisposable()
    private var selectedFriendsIds: String = ""
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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_group)
        initView()
    }

    private fun initView() {
        binding.toolbarLayout.txtTitle.text = getString(R.string.title_new_group)
        setSupportActionBar(binding.toolbarLayout.activityMainToolbar)
        selectedFriendsIds = intent.getStringExtra(Constants.bundle_selected_friends) ?: ""
        setSelectedFriendsAdapter()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setSelectedFriendsAdapter() {
        prepareAllFriendListAdapterModel(getSelectedFriends(selectedFriendsIds.split(",").map { it.toInt() }.toList()))
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

    private fun prepareAllFriendListAdapterModel(friendRequest: List<FriendRequest>) {
        if (selectedFriendsList.isNotEmpty())
            selectedFriendsList.clear()
        selectedFriendsList = prepareSelectFriendWrapperModelList(friendRequest)
        if (selectedFriendsList.isNotEmpty()) {
            selectedFriendsList.sortBy { obj: SelectFriendWrapperModel ->
                obj.firstName.lowercase()
            }
        }
    }

    private fun checkAndEnable(enable: Boolean) {
        enableOrDisableButtonBgColor(this, enable, binding.btnCreateGroup)
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
            R.id.btn_create_group -> {
                setResult(Activity.RESULT_OK)
                finish()
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
}