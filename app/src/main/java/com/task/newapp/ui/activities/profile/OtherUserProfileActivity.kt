package com.task.newapp.ui.activities.profile

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.ContactsContract.PhoneLookup
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.task.newapp.R
import com.task.newapp.api.ApiClient
import com.task.newapp.databinding.ActivityOtherUserProfileBinding
import com.task.newapp.models.ResponseBlockReportUser
import com.task.newapp.models.ResponseFollowUnfollow
import com.task.newapp.models.ResponseFriendSetting
import com.task.newapp.models.ResponseMyProfile
import com.task.newapp.realmDB.getCommonGroup
import com.task.newapp.utils.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class OtherUserProfileActivity : AppCompatActivity() {

    private lateinit var profileData: ResponseMyProfile.MyProfileData
    private var User_ID: Int = 0
    lateinit var binding: ActivityOtherUserProfileBinding
    private val mCompositeDisposable = CompositeDisposable()
    private var isfollow = false
    private var change: Int = 0
    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                change = data!!.getIntExtra("change", 0)
                if (change == 1) {
                    callAPIGetOtherProfile()
                }
            }
        }
    private var resultLauncher1 =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data

                val isCustomNotification = data!!.getIntExtra(Constants.bundle_custom_notification, 0)
                val notiToneId = data!!.getIntExtra(Constants.bundle_notification_tone, 0)
                val vibrate = data!!.getStringExtra(Constants.bundle_vibration)
                setOnOffText(isCustomNotification, notiToneId, vibrate!!)

            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_other_user_profile)
        initView()
    }

    private fun initView() {
        getFromIntent()
        setTag(0)
        setBottomSheet()
        callAPIGetOtherProfile()
    }

    private fun getFromIntent() {
        User_ID = intent.getIntExtra(Constants.user_id, 0)
    }

    @SuppressLint("SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setData(data: ResponseMyProfile.MyProfileData) {
        val contentOtherProfile = binding.layoutContentOtherprofile
        binding.ivProfile.load(data.profile_image, true)
        isfollow = data.is_follow

        data.friendsetting?.let{
            manageIsMuteOrNot(it.muteNotification)
            setOnOffText(it.isCustomNotificationEnable, it.notificationToneId, it.vibrateStatus)
        }
        contentOtherProfile.txtPostCount.text = data.count_post.toString()
        contentOtherProfile.txtFollowerCount.text = data.followers.toString()
        contentOtherProfile.txtFollowingCount.text = data.following.toString()
        contentOtherProfile.txtUsername.text = data.first_name + " " + data.last_name
        contentOtherProfile.txtAccId.text = data.account_id
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        contentOtherProfile.txtLastseen.text = DateTimeUtils.instance!!.getConversationTimestamp(DateTimeUtils.instance!!.getLongFromDateString(data.last_seen_time, formatter))
        contentOtherProfile.txtStatus.text = data.status
        contentOtherProfile.txtPostsCount.text = data.count_post.toString()
        contentOtherProfile.txtGrpcommonCount.text = getCommonGroup(User_ID).size.toString()
        contentOtherProfile.txtPagescommonCount.text = data.count_common_pages.toString()
        contentOtherProfile.txtFrdscommonCount.text = data.count_common_friends.toString()
        if (data.is_follow) {
            setTag(1)
        } else {
            setTag(0)
        }
        if (data.is_friend) {
            contentOtherProfile.cv1.visibility = VISIBLE
            contentOtherProfile.llImagebutton.visibility = VISIBLE
        } else {
            contentOtherProfile.cv1.visibility = GONE
            contentOtherProfile.llImagebutton.visibility = GONE
        }
        if (data.is_block == 1) {
            contentOtherProfile.txtBlock.text = resources.getString(R.string.unblock)
        } else {
            contentOtherProfile.txtBlock.text = resources.getString(R.string.block)
        }
        if (data.is_reported_by_admin == 1) {
            binding.layoutContentOtherprofile.txtReport.visibility = GONE
        } else {
            binding.layoutContentOtherprofile.txtReport.visibility = VISIBLE
        }
    }

    private fun manageIsMuteOrNot(muteNotification: Int) {
        if (muteNotification == 1) {
            binding.layoutContentOtherprofile.btnMute.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(this, R.drawable.ic_profile_unmute_resize), null, null)
            binding.layoutContentOtherprofile.btnMute.text = resources.getString(R.string.unmute)
        } else {
            binding.layoutContentOtherprofile.btnMute.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(this, R.drawable.ic_profile_mute_resize), null, null)
            binding.layoutContentOtherprofile.btnMute.text = resources.getString(R.string.mute)
        }
    }

    private fun setBottomSheet() {
//        binding.layoutContentOtherprofile.txtLastseen.isSelected = true

        binding.ivProfile.layoutParams.height =
            ((getDisplayMatrix().heightPixels / 1.7).roundToInt())
        var sheetBehavior = BottomSheetBehavior.from(binding.layoutContentOtherprofile.bottomSheet)

        sheetBehavior.state = BottomSheetBehavior.STATE_DRAGGING
        binding.layoutContentOtherprofile.bottomSheet.requestLayout()

        sheetBehavior.isHideable = false
    }

    private fun getContactIdFromPhoneNumber(phone: String): String? {
        if (TextUtils.isEmpty(phone)) return null
        val uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phone))
        val contentResolver: ContentResolver = contentResolver
        val phoneQueryCursor: Cursor? = contentResolver.query(uri, arrayOf<String>(PhoneLookup._ID), null, null, null)
        if (phoneQueryCursor != null) {
            if (phoneQueryCursor.moveToFirst()) {
                val result: String = phoneQueryCursor.getString(phoneQueryCursor.getColumnIndex(PhoneLookup._ID))
                phoneQueryCursor.close()
                return result
            }
            phoneQueryCursor.close()
        }
        return null
    }

    fun onClickTag(view: View) {
        when (view.tag) {
            Constants.tag_follow -> {
                setFlagFollowUnfollow()
            }
            Constants.tag_message -> {
                showToast("click message")
            }
        }
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.ll_following -> {
                val intent = Intent(this, FollowesFollowingListActivity::class.java)
                intent.putExtra("By", "Other")
                intent.putExtra("From", Constants.Companion.ProfileNavigation.FROM_FOLLOWINGS.fromname)
                intent.putExtra("Title", Constants.Companion.ProfileNavigation.FROM_FOLLOWINGS.title)
                intent.putExtra(Constants.user_id, User_ID)
                resultLauncher.launch(intent)
            }
            R.id.ll_follower -> {
                val intent = Intent(this, FollowesFollowingListActivity::class.java)
                intent.putExtra("By", "Other")
                intent.putExtra("From", Constants.Companion.ProfileNavigation.FROM_FOLLOWERS.fromname)
                intent.putExtra("Title", Constants.Companion.ProfileNavigation.FROM_FOLLOWERS.title)
                intent.putExtra(Constants.user_id, User_ID)
                resultLauncher.launch(intent)
            }
            R.id.iv_back -> {
                onBackPressed()
            }
            R.id.rl_posts -> {
                launchActivity<PostListActivity> {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    putExtra(Constants.user_id, User_ID)
                    putExtra(Constants.type, Constants.Companion.PostNavigation.FROM_POST.flag)
                    putExtra(Constants.title, Constants.Companion.PostNavigation.FROM_POST.title)
                }
            }
            R.id.ll_post -> {
                launchActivity<PostListActivity> {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    putExtra(Constants.user_id, User_ID)
                    putExtra(Constants.type, Constants.Companion.PostNavigation.FROM_POST.flag)
                    putExtra(Constants.title, Constants.Companion.PostNavigation.FROM_POST.title)
                }
            }
            R.id.rl_frds -> {
                val intent = Intent(this, FollowesFollowingListActivity::class.java)
                intent.putExtra("By", "Other")
                intent.putExtra("From", Constants.Companion.ProfileNavigation.FROM_FRIENDS.fromname)
                intent.putExtra("Title", Constants.Companion.ProfileNavigation.FROM_FRIENDS.title)
                intent.putExtra(Constants.user_id, User_ID)
                resultLauncher.launch(intent)
            }
            R.id.rl_grps -> {
                launchActivity<GroupListActivity> {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    putExtra(Constants.type, "Other")
                    putExtra(Constants.user_id, User_ID)
                }
            }
            R.id.rl_pages -> {
                launchActivity<PageListActivity> {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    putExtra(Constants.user_id, User_ID)
                    putExtra(Constants.type, resources.getString(R.string.pages))
//                    putExtra(Constants.title, PostNavigation.FROM_POST.title)
                }
            }
            R.id.txt_contact_detail -> {
                if (profileData.mobile.isNullOrEmpty()) {
                    showToast("This user not present in your contact list")
                } else {
                    val contactId = getContactIdFromPhoneNumber(profileData.mobile)
                    if (contactId.isNullOrEmpty()) {
                        showToast("This user not present in your contact list")
                    } else {
                        val intent = Intent(Intent.ACTION_VIEW)
                        val uri: Uri = Uri.withAppendedPath(
                            ContactsContract.Contacts.CONTENT_URI,
                            contactId.toString()
                        )
                        intent.data = uri
                        startActivity(intent)
                    }
                }
            }
            R.id.btn_mute -> {
                if (profileData.friendsetting.muteNotification == 1) {
                    callAPIMuteNotification(0)
                } else {
                    callAPIMuteNotification(1)
                }
            }
            R.id.txt_block -> {
                if (profileData.is_block == 1) {
                    callAPIBlockReportUser("unblock")
                } else {
                    callAPIBlockReportUser("block")
                }
            }
            R.id.txt_report -> {
                callAPIBlockReportUser("report")
            }
            R.id.ll_custom_notification -> {
                val intent = Intent(this, CustomNotificationActivity::class.java)
                intent.putExtra(Constants.bundle_custom_notification, profileData.friendsetting.isCustomNotificationEnable)
                intent.putExtra(Constants.bundle_notification_tone, profileData.friendsetting.notificationToneId)
                intent.putExtra(Constants.bundle_vibration, profileData.friendsetting.vibrateStatus)
                intent.putExtra(Constants.user_id, User_ID)
                resultLauncher1.launch(intent)

            }
        }
    }

    private fun setFlagFollowUnfollow() {
        if (!applicationContext.isNetworkConnected()) {
            applicationContext.showToast(applicationContext.getString(R.string.no_internet))
            return
        }
        try {

            val isFollow: String = if (!isfollow) {
                "follow"
            } else {
                "unfollow"
            }

            if (isFollow == "follow") {
                callAPIFollowUnfollow(isFollow)
            } else {
                DialogUtils().showConfirmationDialog(this@OtherUserProfileActivity, "", applicationContext.resources.getString(R.string.unfollow_confirm_msg), object : DialogUtils.DialogCallbacks {
                    override fun onPositiveButtonClick() {
                        callAPIFollowUnfollow(isFollow)
                    }

                    override fun onNegativeButtonClick() {

                    }

                    override fun onDefaultButtonClick(actionName: String) {
                    }

                })
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setOnOffText(isCustomNotification: Int, notiToneId: Int, vibrate: String) {
        profileData.friendsetting.isCustomNotificationEnable = isCustomNotification
        profileData.friendsetting.notificationToneId = notiToneId
        profileData.friendsetting.vibrateStatus = vibrate
        if (isCustomNotification == 1) {
            binding.layoutContentOtherprofile.txtCustomNotificationStatus.text = resources.getString(R.string.on)
        } else {
            binding.layoutContentOtherprofile.txtCustomNotificationStatus.text = resources.getString(R.string.off)
        }
    }

    private fun setTag(tag: Int) {
        val follow = binding.layoutContentOtherprofile.btnFollow
        val message = binding.layoutContentOtherprofile.btnMessage
        if (tag == 0) {
            follow.tag = Constants.tag_follow
            message.tag = Constants.tag_message
            follow.setBackgroundResource(R.drawable.btn_follow_user)
            follow.backgroundTintList = null
            follow.text = ""
            message.setBackgroundResource(R.drawable.btn_rect_rounded_bg)
            message.backgroundTintList = ContextCompat.getColorStateList(this@OtherUserProfileActivity, R.color.white)
            message.text = resources.getString(R.string.message)
        } else {
            follow.tag = Constants.tag_message
            message.tag = Constants.tag_follow
            message.setBackgroundResource(R.drawable.btn_follow_user_accept)
            message.backgroundTintList = null
            message.text = ""
            follow.setBackgroundResource(R.drawable.btn_rect_rounded_bg)
            follow.backgroundTintList = ContextCompat.getColorStateList(this@OtherUserProfileActivity, R.color.white)
            follow.text = resources.getString(R.string.message)
        }
    }

    private fun callAPIFollowUnfollow(isFollow: String) {
        openProgressDialog(this@OtherUserProfileActivity)

        val hashMap: HashMap<String, Any> = hashMapOf(
            Constants.type to isFollow,
            Constants.follow_id to User_ID,
        )

        mCompositeDisposable.add(
            ApiClient.create()
                .setUserFollowUnfollow(hashMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableObserver<ResponseFollowUnfollow>() {
                    override fun onNext(loginResponse: ResponseFollowUnfollow) {
                        if (loginResponse.success != 1) {
                            applicationContext.showToast(loginResponse.message)
                        } else if (loginResponse.success == 1 || loginResponse.success == -1) {
                            if (!isfollow) {
                                isfollow = true
                                setTag(1)
                            } else {
                                isfollow = false
                                setTag(0)
                            }
                        }
                    }

                    override fun onError(e: Throwable) {
                        hideProgressDialog()
                    }

                    override fun onComplete() {
                        hideProgressDialog()
                    }
                })
        )
    }

    private fun callAPIGetOtherProfile() {
        if (!isNetworkConnected()) {
            showToast(getString(R.string.no_internet))
            return
        }
        try {
            openProgressDialog(this)

            mCompositeDisposable.add(
                ApiClient.create()
                    .getMyProfile(User_ID.toString())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableObserver<ResponseMyProfile>() {
                        @RequiresApi(Build.VERSION_CODES.O)
                        override fun onNext(loginResponse: ResponseMyProfile) {
                            Log.v("onNext: ", loginResponse.toString())
                           // showToast(loginResponse.message)

                            if (loginResponse.success == 1) {
                                profileData = loginResponse.data
                                setData(profileData)
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

    private fun callAPIMuteNotification(mute: Int) {
        if (!isNetworkConnected()) {
            showToast(getString(R.string.no_internet))
            return
        }
        try {
            openProgressDialog(this)

            val hashMap: HashMap<String, Any> = hashMapOf(
                Constants.mute_notification to mute,
                Constants.friend_id to User_ID,
            )

            mCompositeDisposable.add(
                ApiClient.create()
                    .setFriendSetting(hashMap)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableObserver<ResponseFriendSetting>() {
                        @RequiresApi(Build.VERSION_CODES.O)
                        override fun onNext(loginResponse: ResponseFriendSetting) {
                            Log.v("onNext: ", loginResponse.toString())
                            showToast(loginResponse.message)

                            if (loginResponse.success == 1) {
                                profileData.friendsetting.muteNotification = mute
                                manageIsMuteOrNot(profileData.friendsetting.muteNotification)
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


    private fun callAPIBlockReportUser(isBlock: String) {
        val message: String = when (isBlock) {
            "block" -> {
                applicationContext.resources.getString(R.string.block_confirm_msg)
            }
            "unblock" -> {
                applicationContext.resources.getString(R.string.unblock_confirm_msg)
            }
            else -> {
                applicationContext.resources.getString(R.string.conform_report)
            }
        }

        if (isBlock == "block" || isBlock == "unblock") {
            DialogUtils().showConfirmationYesNoDialog(this, "", message, object : DialogUtils.DialogCallbacks {
                override fun onPositiveButtonClick() {
                    if (!isNetworkConnected()) {
                        showToast(getString(R.string.no_internet))
                        return
                    }
                    try {
                        openProgressDialog(this@OtherUserProfileActivity)

                        val hashMap: HashMap<String, Any> = hashMapOf(
                            Constants.id to User_ID,
                            Constants.type to isBlock,
                        )

                        mCompositeDisposable.add(
                            ApiClient.create()
                                .setBlockUnblockReportSetting(hashMap)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeWith(object : DisposableObserver<ResponseBlockReportUser>() {
                                    @RequiresApi(Build.VERSION_CODES.O)
                                    override fun onNext(blockreportuserresponse: ResponseBlockReportUser) {
                                        Log.v("onNext: ", blockreportuserresponse.toString())
                                        showToast(blockreportuserresponse.message)

                                        if (blockreportuserresponse.success == 1) {
                                            if (isBlock == "block" || isBlock == "unblock") {
                                                if (profileData.is_block == 1) {
                                                    profileData.is_block = 0
                                                    manageBlockUnblockText(0)
                                                } else {
                                                    profileData.is_block = 1
                                                    manageBlockUnblockText(1)
                                                }
                                            } else {
                                                onBackPressed()
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

                override fun onNegativeButtonClick() {

                }

                override fun onDefaultButtonClick(actionName: String) {
                }

            })
        } else {
            DialogUtils().showReportDialog(this, message, object : DialogUtils.DialogCallbacks {
                override fun onPositiveButtonClick() {

                }

                override fun onNegativeButtonClick() {

                }

                override fun onDefaultButtonClick(actionName: String) {
                    if (!isNetworkConnected()) {
                        showToast(getString(R.string.no_internet))
                        return
                    }
                    try {
                        openProgressDialog(this@OtherUserProfileActivity)

                        val hashMap: HashMap<String, Any> = hashMapOf(
                            Constants.id to User_ID,
                            Constants.type to isBlock,
                            Constants.report_reason to actionName
                        )

                        mCompositeDisposable.add(
                            ApiClient.create()
                                .setBlockUnblockReportSetting(hashMap)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeWith(object : DisposableObserver<ResponseBlockReportUser>() {
                                    @RequiresApi(Build.VERSION_CODES.O)
                                    override fun onNext(blockreportuserresponse: ResponseBlockReportUser) {
                                        Log.v("onNext: ", blockreportuserresponse.toString())
                                        showToast(blockreportuserresponse.message)

                                        if (blockreportuserresponse.success == 1) {
                                            if (isBlock == "block" || isBlock == "unblock") {
                                                if (profileData.is_block == 1) {
                                                    profileData.is_block = 0
                                                    manageBlockUnblockText(0)
                                                } else {
                                                    profileData.is_block = 1
                                                    manageBlockUnblockText(1)
                                                }
                                            } else {
                                                onBackPressed()
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

            })
        }

    }

    private fun manageBlockUnblockText(isBlock: Int) {
        if (isBlock == 0) {
            binding.layoutContentOtherprofile.txtBlock.text = resources.getString(R.string.block)
        } else {
            binding.layoutContentOtherprofile.txtBlock.text = resources.getString(R.string.unblock)
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




