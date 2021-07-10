package com.task.newapp.ui.activities.profile

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.task.newapp.R
import com.task.newapp.api.ApiClient
import com.task.newapp.databinding.ActivityOtherUserBinding
import com.task.newapp.models.ResponseFollowUnfollow
import com.task.newapp.models.ResponseMyProfile
import com.task.newapp.utils.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class OtherUserProfileActivity : AppCompatActivity() {

    private var User_ID: Int = 0
    lateinit var binding: ActivityOtherUserBinding
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_other_user)

        getFromIntent()
        setTag(0)
        setBottomsheet()
        callAPIGetOtherProfile()
    }

    private fun getFromIntent() {
        User_ID = intent.getIntExtra(Constants.user_id, 0)
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

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    private fun setData(data: ResponseMyProfile.MyProfileData) {
        var content_other_profile = binding.layoutContentOtherprofile
        Glide.with(this).asBitmap().load(data.profile_image).into(binding.ivProfile)

        isfollow = data.is_follow
        if (data.is_follow) {
            setTag(1)
        } else {
            setTag(0)
        }

        if (data.is_friend) {
            content_other_profile.cv1.visibility = VISIBLE
            content_other_profile.llImagebutton.visibility = VISIBLE
        } else {
            content_other_profile.cv1.visibility = GONE
            content_other_profile.llImagebutton.visibility = GONE
        }

        content_other_profile.txtPostCount.text = data.count_post.toString()
        content_other_profile.txtFollowerCount.text = data.followers.toString()
        content_other_profile.txtFollowingCount.text = data.following.toString()

        content_other_profile.txtUsername.text = data.first_name + " " + data.last_name
        content_other_profile.txtAccId.text = data.account_id
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        content_other_profile.txtLastseen.text = DateTimeUtils.instance!!.getConversationTimestamp(DateTimeUtils.instance!!.getLongFromDateString(data.last_seen_time, formatter))
        showLog("last seen", content_other_profile.txtLastseen.text.toString());
        content_other_profile.txtStatus.text = data.status

//        content_other_profile.switchCustomNotification.isSelected=data.usersetting.
//        content_other_profile.txtPwldCount.text=
//        content_other_profile.txtStarCount.text=
        content_other_profile.txtPostsCount.text = data.count_post.toString()

        content_other_profile.txtGrpcommonCount.text = data.count_common_groups.toString()
        content_other_profile.txtPagescommonCount.text = data.count_common_pages.toString()
        content_other_profile.txtFrdscommonCount.text = data.count_common_friends.toString()

        if (data.is_block == 1) {
            content_other_profile.txtBlock.text = resources.getString(R.string.unblock)
        } else {
            content_other_profile.txtBlock.text = resources.getString(R.string.block)
        }
    }

    private fun setBottomsheet() {
//        binding.layoutContentOtherprofile.txtLastseen.isSelected = true

        binding.ivProfile.layoutParams.height =
            ((getDisplayMatrix().heightPixels / 1.7).roundToInt())
        var sheetBehavior = BottomSheetBehavior.from(binding.layoutContentOtherprofile.bottomSheet)

        sheetBehavior.state = BottomSheetBehavior.STATE_DRAGGING
        binding.layoutContentOtherprofile.bottomSheet.requestLayout()

        sheetBehavior.isHideable = false
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
            R.id.rl_frds->{
                val intent = Intent(this, FollowesFollowingListActivity::class.java)
                intent.putExtra("By", "Other")
                intent.putExtra("From", Constants.Companion.ProfileNavigation.FROM_FRIENDS.fromname)
                intent.putExtra("Title", Constants.Companion.ProfileNavigation.FROM_FRIENDS.title)
                intent.putExtra(Constants.user_id, User_ID)
                resultLauncher.launch(intent)
            }
        }
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

    override fun onBackPressed() {
        var intent = Intent().putExtra("change", change)
        setResult(RESULT_OK, intent)
        finish()
        super.onBackPressed()
    }
}




