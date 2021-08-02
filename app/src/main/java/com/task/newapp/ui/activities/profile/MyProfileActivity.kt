package com.task.newapp.ui.activities.profile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.task.newapp.R
import com.task.newapp.api.ApiClient
import com.task.newapp.databinding.ActivityMyProfileBinding
import com.task.newapp.databinding.ContentMyProfileActivityBinding
import com.task.newapp.models.ResponseMyProfile
import com.task.newapp.models.ResponseUserSetting
import com.task.newapp.realmDB.getMyGroup
import com.task.newapp.utils.*
import com.task.newapp.utils.Constants.Companion.PostNavigation
import com.task.newapp.utils.Constants.Companion.ProfileNavigation
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.util.*
import kotlin.math.roundToInt


class MyProfileActivity : AppCompatActivity() {

    private var change: Int = 0
    lateinit var binding: ActivityMyProfileBinding
    private val mCompositeDisposable = CompositeDisposable()
    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data

                change = data!!.getIntExtra("change", 0)
                if (change == 1) {
                    callAPIGetMyProfile()
                }
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_profile)

        setBottomSheet()
        callAPIGetMyProfile()

    }

    private fun setData(data: ResponseMyProfile.MyProfileData) {
        val contentMyProfile: ContentMyProfileActivityBinding = binding.layoutContentMyprofile
        Glide.with(this).asBitmap().load(data.profile_image).into(binding.ivProfile)
        contentMyProfile.txtFollowingCount.text = data.following
        contentMyProfile.txtFollowerCount.text = data.followers
        contentMyProfile.txtProfileviewCount.text = data.profile_views.toString()

        contentMyProfile.txtUsername.text = data.first_name + " " + data.last_name
        contentMyProfile.txtAccId.text = data.account_id
        contentMyProfile.txtStatus.text = data.status

        contentMyProfile.switchVisibility.isChecked = data.usersetting.is_visible == 1
        contentMyProfile.switchShowmenearby.isChecked = data.usersetting.near_location == 1

        contentMyProfile.txtGrpCount.text = getMyGroup().size.toString()
        contentMyProfile.txtPagesCount.text = data.count_common_pages.toString()
        contentMyProfile.txtFrdsCount.text = data.count_common_friends.toString()

        callAPISetVisibility()
        callAPISetShowNearBy()

    }

    private fun setBottomSheet() {

        binding.ivProfile.layoutParams.height =
            ((getDisplayMatrix().heightPixels / 1.4).roundToInt())
        var sheetBehavior = BottomSheetBehavior.from(binding.layoutContentMyprofile.bottomSheet)

        sheetBehavior.state = BottomSheetBehavior.STATE_DRAGGING
        binding.layoutContentMyprofile.bottomSheet.requestLayout()

        sheetBehavior.isHideable = false

    }

    private fun callAPIGetMyProfile() {
        if (!isNetworkConnected()) {
            showToast(getString(R.string.no_internet))
            return
        }
        try {
            openProgressDialog(this)

            mCompositeDisposable.add(
                ApiClient.create()
                    .getMyProfile("0")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableObserver<ResponseMyProfile>() {
                        override fun onNext(loginResponse: ResponseMyProfile) {
                            Log.v("onNext: ", loginResponse.toString())
                            showToast(loginResponse.message)

                            if (loginResponse.success == 1) {
                                setData(loginResponse.data)
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

    private fun callAPISetShowNearBy() {
        binding.layoutContentMyprofile.switchShowmenearby.setOnCheckedChangeListener(object :
            CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                if (!isNetworkConnected()) {
                    showToast(getString(R.string.no_internet))
                    return
                }
                try {
                    val hashMap: HashMap<String, Any> = hashMapOf(
                        if (isChecked) {
                            Constants.nearLocation to 1
                        } else {
                            Constants.nearLocation to 0
                        }
                    )

//                    openProgressDialog(this@MyProfileActivity)

                    mCompositeDisposable.add(
                        ApiClient.create()
                            .setUserSetting(hashMap)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(object : DisposableObserver<ResponseUserSetting>() {
                                override fun onNext(loginResponse: ResponseUserSetting) {
                                    Log.v("onNext: ", loginResponse.toString())
                                    showToast(loginResponse.message)

                                    if (loginResponse.success == 0) {
                                        binding.layoutContentMyprofile.switchShowmenearby.isSelected =
                                            isChecked
                                    }
                                }

                                override fun onError(e: Throwable) {
                                    Log.v("onError: ", e.toString())
//                                    hideProgressDialog()
                                }

                                override fun onComplete() {
//                                    hideProgressDialog()
                                }
                            })
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        })
    }

    private fun callAPISetVisibility() {
        binding.layoutContentMyprofile.switchVisibility.setOnCheckedChangeListener(object :
            CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                if (!isNetworkConnected()) {
                    showToast(getString(R.string.no_internet))
                    return
                }
                try {
                    val hashMap: HashMap<String, Any> = hashMapOf(
                        if (isChecked) {
                            Constants.isVisible to 1
                        } else {
                            Constants.isVisible to 0
                        }
                    )

//                    openProgressDialog(this@MyProfileActivity)

                    mCompositeDisposable.add(
                        ApiClient.create()
                            .setUserSetting(hashMap)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(object : DisposableObserver<ResponseUserSetting>() {
                                override fun onNext(loginResponse: ResponseUserSetting) {
                                    Log.v("onNext: ", loginResponse.toString())
                                    showToast(loginResponse.message)

                                    if (loginResponse.success == 0) {
                                        binding.layoutContentMyprofile.switchVisibility.isSelected =
                                            isChecked
                                    }
                                }

                                override fun onError(e: Throwable) {
                                    Log.v("onError: ", e.toString())
//                                    hideProgressDialog()
                                }

                                override fun onComplete() {
//                                    hideProgressDialog()
                                }
                            })
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        })
    }

    fun onClick(v: View?) {
        when (v!!.id) {
            R.id.ll_following -> {
                val intent = Intent(this, FollowesFollowingListActivity::class.java)
                intent.putExtra("By", "My")
                intent.putExtra("From", ProfileNavigation.FROM_FOLLOWINGS.fromname)
                intent.putExtra("Title", ProfileNavigation.FROM_FOLLOWINGS.title)
                resultLauncher.launch(intent)
            }
            R.id.ll_follower -> {
                val intent = Intent(this, FollowesFollowingListActivity::class.java)
                intent.putExtra("By", "My")
                intent.putExtra("From", ProfileNavigation.FROM_FOLLOWERS.fromname)
                intent.putExtra("Title", ProfileNavigation.FROM_FOLLOWERS.title)
                resultLauncher.launch(intent)
            }
            R.id.ll_profile_viewer -> {
                launchActivity<FollowesFollowingListActivity> {
                    putExtra("By", "My")
                    putExtra("From", ProfileNavigation.FROM_PROFILE_VIEWS.fromname)
                    putExtra("Title", ProfileNavigation.FROM_PROFILE_VIEWS.title)
                }
            }
            R.id.iv_edit_profile -> {
//                launchActivity<EditProfileActivity> { }
                val intent = Intent(this, EditProfileActivity::class.java)
                resultLauncher.launch(intent)
            }
            R.id.iv_back -> {
                onBackPressed()
            }
            R.id.txt_my_post -> {
                launchActivity<PostListActivity> {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    putExtra(Constants.user_id, 0)
                    putExtra(Constants.type, PostNavigation.FROM_POST.flag)
                    putExtra(Constants.title, PostNavigation.FROM_POST.title)
                }
            }
            R.id.txt_tagged_post -> {
                launchActivity<PostListActivity> {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    putExtra(Constants.user_id, 0)
                    putExtra(Constants.type, PostNavigation.FROM_TAGGED_POST.flag)
                    putExtra(Constants.title, PostNavigation.FROM_TAGGED_POST.title)
                }
            }
            R.id.txt_saved_post -> {
                launchActivity<PostListActivity> {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    putExtra(Constants.user_id, 0)
                    putExtra(Constants.type, PostNavigation.FROM_SAVED_POST.flag)
                    putExtra(Constants.title, PostNavigation.FROM_SAVED_POST.title)
                }
            }
            R.id.rl_pages -> {
                launchActivity<PageListActivity> {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    putExtra(Constants.user_id, 0)
                    putExtra(Constants.type, resources.getString(R.string.my_pages))
//                    putExtra(Constants.title, PostNavigation.FROM_POST.title)
                }
            }
            R.id.rl_frds -> {
                val intent = Intent(this, FollowesFollowingListActivity::class.java)
                intent.putExtra("By", "My")
                intent.putExtra("From", ProfileNavigation.FROM_FRIENDS.fromname)
                intent.putExtra("Title", ProfileNavigation.FROM_FRIENDS.title)
                resultLauncher.launch(intent)
            }
            R.id.rl_grps -> {
                launchActivity<GroupListActivity> {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    putExtra(Constants.type, "My")
                }
            }
        }
    }

    override fun onBackPressed() {
        var intent = Intent().putExtra("change", change)
        setResult(RESULT_OK, intent)
        finish()
        super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        mCompositeDisposable.clear()
    }
}