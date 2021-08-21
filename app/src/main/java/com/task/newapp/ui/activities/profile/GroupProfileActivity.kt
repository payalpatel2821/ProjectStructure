package com.task.newapp.ui.activities.profile

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.peekandpop.shalskar.peekandpop.PeekAndPop
import com.task.newapp.R
import com.task.newapp.adapter.profile.GroupMemberListAdapter
import com.task.newapp.api.ApiClient
import com.task.newapp.databinding.ActivityGroupProfileBinding
import com.task.newapp.databinding.ContentGroupProfileActivityBinding
import com.task.newapp.models.CommonResponse
import com.task.newapp.models.ResponseAddRemoveAdmin
import com.task.newapp.models.ResponseExitReportGroup
import com.task.newapp.realmDB.getGroupCreatedUserName
import com.task.newapp.realmDB.getGroupDetail
import com.task.newapp.realmDB.getMyGroupSetting
import com.task.newapp.realmDB.getUserByUserId
import com.task.newapp.realmDB.models.Chats
import com.task.newapp.realmDB.models.GroupUser
import com.task.newapp.utils.*
import de.hdodenhof.circleimageview.CircleImageView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt


class GroupProfileActivity : AppCompatActivity(), AdapterView.OnItemClickListener {

    private lateinit var groupSetting: GroupUser
    private lateinit var currentUser: GroupUser
    private lateinit var groupMemberList: ArrayList<GroupUser>
    private lateinit var contentGrpProfile: ContentGroupProfileActivityBinding
    private lateinit var groupMemberAdapter: GroupMemberListAdapter
    lateinit var grpDetail: Chats
    lateinit var binding: ActivityGroupProfileBinding
    private var grpID = 0
    private val mCompositeDisposable = CompositeDisposable()
    private var resultLauncher1 =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val isCustomNotification =
                    data!!.getIntExtra(Constants.bundle_custom_notification, 0)
                val notiToneId = data!!.getIntExtra(Constants.bundle_notification_tone, 0)
                val vibrate = data!!.getStringExtra(Constants.bundle_vibration)
                setOnOffText(isCustomNotification, notiToneId, vibrate!!)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_group_profile)
        initView()
    }

    private fun initView() {
        grpID = intent.getIntExtra(Constants.group_id, 0)
        getGroupDetailFromID()
        setBottomSheet()
        setData()
        setMemberList()

        binding.ivBack.setOnClickListener { finish() }
    }

    private fun getCurrentUserGroupSetting(): GroupUser? {
        return getMyGroupSetting(grpID, getCurrentUserId())
    }

    private fun setBottomSheet() {
        binding.ivProfile.layoutParams.height =
            ((getDisplayMatrix().heightPixels / 1.7).roundToInt())
        val sheetBehavior = BottomSheetBehavior.from(binding.layoutContentGrpProfile.bottomSheet)
        sheetBehavior.state = BottomSheetBehavior.STATE_DRAGGING
        binding.layoutContentGrpProfile.bottomSheet.requestLayout()
        sheetBehavior.isHideable = false
    }

    private fun initPeakPop() {
        val peekAndPop = PeekAndPop.Builder(this)
            .peekLayout(R.layout.item_edit_profile)
            .longClickViews(binding.ivProfile)
            .onHoldAndReleaseListener(object : PeekAndPop.OnHoldAndReleaseListener {
                override fun onHold(view: View?, position: Int) {
                    if (view?.id == R.id.txt_edit_profile) {
                        showToast("onHold click edit proile")
                    } else {
                        showToast("onHold click edit other")
                    }
                }

                override fun onLeave(view: View?, position: Int) {
                    showToast("onLeave click edit profile")
                }

                override fun onRelease(view: View?, position: Int) {
                    showToast("onRelease click edit profile")
                }

            })
            .build()

        val peekView = peekAndPop.peekView
        val userName: TextView = peekView.findViewById(R.id.txt_user_name)
        val accId: TextView = peekView.findViewById(R.id.txt_accid)
        val userProfile: CircleImageView = peekView.findViewById(R.id.iv_user_profile)
        val ivProfile: ImageView = peekView.findViewById(R.id.iv_profile)
        val editProfile: TextView = peekView.findViewById(R.id.txt_edit_profile)
        val resetProfile: TextView = peekView.findViewById(R.id.txt_reset_profile)
        val linearlay: LinearLayout = peekView.findViewById(R.id.linearlay)

        linearlay.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                showToast("click linearlay")
            }
        })

        peekAndPop.addHoldAndReleaseView(R.id.txt_edit_profile)

        userName.text = grpDetail.name
        accId.text = "Created by " + getGroupCreatedUserName(grpDetail.group_data!!.grp_created_by)

        userProfile.load(grpDetail.group_data!!.grp_icon.toString(), false)
        ivProfile.load(grpDetail.group_data!!.grp_icon.toString(), false)


        editProfile.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                showToast("click edit profile")
                peekAndPop.pop(view, -1)
                openPicker(supportFragmentManager)
            }
        })

        resetProfile.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                showToast("click edit profile")
                peekAndPop.pop(view, -1)
            }
        })


    }

    private fun setOnOffText(isCustomNotification: Int, notiToneId: Int, vibrate: String) {
//        groupSetting.custom_notification_enable = isCustomNotification
//        groupSetting.notification_tone_id = notiToneId
//        groupSetting.vibrate_status = vibrate
        if (isCustomNotification == 1) {
            binding.layoutContentGrpProfile.txtCustomNotificationStatus.text = resources.getString(R.string.on)
        } else {
            binding.layoutContentGrpProfile.txtCustomNotificationStatus.text = resources.getString(R.string.off)
        }
    }

    private fun setData() {
        binding.ivProfile.load(grpDetail.group_data!!.grp_icon.toString(), false)
        contentGrpProfile = binding.layoutContentGrpProfile
        contentGrpProfile.txtGrpName.text = grpDetail.name
        contentGrpProfile.txtCreatedByNTime.text = "Created by " + getGroupCreatedUserName(grpDetail.group_data!!.grp_created_by)
        groupSetting = getCurrentUserGroupSetting()!!
        groupSetting?.let { setOnOffText(it.custom_notification_enable, it.notification_tone_id, it.vibrate_status!!) }
        initPeakPop()

        if (groupSetting?.is_allow_to_edit_info == 1) {
            contentGrpProfile.ivEditProfile.visibility = VISIBLE
        } else {
            contentGrpProfile.ivEditProfile.visibility = GONE
        }
        if (groupSetting?.is_admin == 1) {
            contentGrpProfile.txtGrpSetting.visibility = VISIBLE
            contentGrpProfile.viewGrpSetting.visibility = VISIBLE
            contentGrpProfile.txtAddMember.visibility = VISIBLE
        } else {
            contentGrpProfile.txtGrpSetting.visibility = GONE
            contentGrpProfile.viewGrpSetting.visibility = GONE
            contentGrpProfile.txtAddMember.visibility = GONE
        }
        if (groupSetting?.status == "Active") {
            contentGrpProfile.llCustomNotification.visibility = VISIBLE
            contentGrpProfile.viewCustomNotification.visibility = VISIBLE
            visibleExitGroup()
        } else {
            contentGrpProfile.llCustomNotification.visibility = GONE
            contentGrpProfile.viewCustomNotification.visibility = GONE
            visibleDeleteGroup()
        }
        if (grpDetail.group_data?.grp_created_by == getCurrentUserId()) {
            contentGrpProfile.txtReportGrp.visibility = GONE
            contentGrpProfile.viewReport.visibility = GONE
        } else {
            contentGrpProfile.txtReportGrp.visibility = VISIBLE
            contentGrpProfile.viewReport.visibility = VISIBLE
        }
    }

    private fun setMemberList() {

        groupMemberList = grpDetail.group_user_with_settings?.let {
            it.filter { it.status == "Active" }.sortedByDescending { it.is_admin == 1 }.toList() as ArrayList<GroupUser>//.sortedBy { it.user_id== getCurrentUserId() }
        }

        currentUser = groupMemberList.first { it.user_id == getCurrentUserId() }
        val index: Int = groupMemberList.indexOf(currentUser)
        groupMemberList.removeAt(index)
        groupMemberList.add(0, currentUser)

        groupMemberAdapter = GroupMemberListAdapter(ArrayList())
        val layoutManager = LinearLayoutManager(this)
        contentGrpProfile.rvMemberList.layoutManager = layoutManager
        contentGrpProfile.rvMemberList.adapter = groupMemberAdapter
        groupMemberAdapter.setData(groupMemberList, 5/*, currentUser.is_admin*/)

        if (groupMemberList.size > 5) {
            contentGrpProfile.txtLoadMore.visibility = VISIBLE
        } else {
            contentGrpProfile.txtLoadMore.visibility = GONE
        }

        contentGrpProfile.txtTotalMember.text = groupMemberList.size.toString() + " MEMBERS"
        contentGrpProfile.txtLoadMore.text = (groupMemberList.size - 5).toString() + " more"

        groupMemberAdapter.setOnItemClickListener(this)

    }

    private fun getGroupDetailFromID() {
        grpDetail = getGroupDetail(grpID)
    }

    private fun visibleExitGroup() {
        binding.layoutContentGrpProfile.txtExitDeleteGrp.text = resources.getString(R.string.exit_group)
        binding.layoutContentGrpProfile.txtExitDeleteGrp.tag = resources.getString(R.string.exit_group)
    }

    private fun visibleDeleteGroup() {
        binding.layoutContentGrpProfile.txtExitDeleteGrp.text = resources.getString(R.string.delete_group)
        binding.layoutContentGrpProfile.txtExitDeleteGrp.tag = resources.getString(R.string.delete_group)
    }

    fun onClickByTag(view: View) {
        when (view.tag) {
            resources.getString(R.string.delete_group) -> {
                DialogUtils().showConfirmationYesNoDialog(this, resources.getString(R.string.delete_group), resources.getString(R.string.confirm_delete_grp_msg), object : DialogUtils.DialogCallbacks {
                    override fun onPositiveButtonClick() {
                        callAPIDeleteGroup()
                    }

                    override fun onNegativeButtonClick() {
                    }

                    override fun onDefaultButtonClick(actionName: String) {
                    }

                })
            }
            resources.getString(R.string.exit_group) -> {
                DialogUtils().showConfirmationYesNoDialog(this, resources.getString(R.string.exit_group), resources.getString(R.string.confirm_exit_grp_msg), object : DialogUtils.DialogCallbacks {
                    override fun onPositiveButtonClick() {
                        callAPIExitGroup()
                    }

                    override fun onNegativeButtonClick() {
                    }

                    override fun onDefaultButtonClick(actionName: String) {
                    }

                })
            }
        }
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.txt_report_grp -> {
                DialogUtils().showConfirmationYesNoDialog(this, resources.getString(R.string.report_group), resources.getString(R.string.confirm_report_grp_msg), object : DialogUtils.DialogCallbacks {
                    override fun onPositiveButtonClick() {
                        callAPIReportGroup()
                    }

                    override fun onNegativeButtonClick() {
                    }

                    override fun onDefaultButtonClick(actionName: String) {
                    }

                })
            }
            R.id.txt_load_more -> {
                groupMemberAdapter.setData(groupMemberList, groupMemberList.size/*, currentUser.is_admin*/)
                contentGrpProfile.txtLoadMore.visibility = GONE
            }
            R.id.ll_custom_notification -> {
                val intent = Intent(this, CustomNotificationForGroupActivity::class.java)
                intent.putExtra(Constants.bundle_custom_notification, groupSetting.custom_notification_enable)
                intent.putExtra(Constants.bundle_notification_tone, groupSetting.notification_tone_id)
                intent.putExtra(Constants.bundle_vibration, groupSetting.vibrate_status)
                intent.putExtra(Constants.group_id, grpID)
                resultLauncher1.launch(intent)
            }
        }
    }

    private fun callAPIExitGroup() {
        if (!isNetworkConnected()) {
            showToast(getString(R.string.no_internet))
            return
        }
        try {
            openProgressDialog(this)

            mCompositeDisposable.add(
                ApiClient.create()
                    .exitGroup(grpID)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableObserver<ResponseExitReportGroup>() {
                        @RequiresApi(Build.VERSION_CODES.O)
                        override fun onNext(responseExitGroup: ResponseExitReportGroup) {
                            showToast(responseExitGroup.message)
                            if (responseExitGroup.success == 1) {
                                visibleDeleteGroup()
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

    private fun callAPIDeleteGroup() {
        if (!isNetworkConnected()) {
            showToast(getString(R.string.no_internet))
            return
        }
        try {
            openProgressDialog(this)

            mCompositeDisposable.add(
                ApiClient.create()
                    .deleteGroup(grpID)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableObserver<CommonResponse>() {
                        @RequiresApi(Build.VERSION_CODES.O)
                        override fun onNext(commonResponse: CommonResponse) {
                            showToast(commonResponse.message)
                            if (commonResponse.success == 1) {
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
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun callAPIReportGroup() {
        if (!isNetworkConnected()) {
            showToast(getString(R.string.no_internet))
            return
        }
        try {
            openProgressDialog(this)

            mCompositeDisposable.add(
                ApiClient.create()
                    .reportGroup(grpID)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableObserver<ResponseExitReportGroup>() {
                        @RequiresApi(Build.VERSION_CODES.O)
                        override fun onNext(responseExitReportGroup: ResponseExitReportGroup) {
                            showToast(responseExitReportGroup.message)
                            if (responseExitReportGroup.success == 1) {
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
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (groupMemberList[position].user_id != getCurrentUserId()) {
            val userID = groupMemberList[position].user_id
            val user = getUserByUserId(groupMemberList[position].user_id)
            val userName = user?.first_name + " " + user?.last_name
            if (currentUser.is_admin == 1) {
                openDialogAsAdmin(groupMemberList[position].is_admin, userID, userName)
            } else {
                openDialogMemberClick(userID, userName)
            }
        }
    }

    private fun openDialogAsAdmin(isAdmin: Int, userId: Int, userName: String) {
        DialogUtils().showMemberClickIOSDialogIfAdmin(this as AppCompatActivity, "", userName, isAdmin, object : DialogUtils.DialogCallbacks {
            override fun onPositiveButtonClick() {

            }

            override fun onNegativeButtonClick() {

            }

            override fun onDefaultButtonClick(actionName: String) {
                when (actionName) {
                    DialogUtils.MemberClickIfAdminDialogActionName.SEND_MESSAGE.value -> {
                        showToast("Redirect to ChattingActivity")
                    }
                    DialogUtils.MemberClickIfAdminDialogActionName.VIEW_PROFILE.value -> {
                        launchActivity<OtherUserProfileActivity> {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            putExtra(Constants.user_id, userId)
                        }
                    }
                    DialogUtils.MemberClickIfAdminDialogActionName.MAKE_GROUP_CAPTAIN.value -> {
                        val hashMap: HashMap<String, Any> = hashMapOf(
                            Constants.group_id to grpID,
                            Constants.make_admin_id to userId
                        )
                        callAPIMakeRemoveAdmin(hashMap)
                    }
                    DialogUtils.MemberClickIfAdminDialogActionName.DISMISS_AS_CAPTAIN.value -> {
                        val hashMap: HashMap<String, Any> = hashMapOf(
                            Constants.group_id to grpID,
                            Constants.remove_admin_id to userId
                        )
                        callAPIMakeRemoveAdmin(hashMap)
                    }
                    DialogUtils.MemberClickIfAdminDialogActionName.REMOVE_FROM_GROUP.value -> {
                        val hashMap: HashMap<String, Any> = hashMapOf(
                            Constants.group_id to grpID,
                            Constants.remove_user to userId
                        )
                        callAPIAddOrRemoveUser(hashMap)
                    }
                }
            }
        })
    }

    private fun callAPIAddOrRemoveUser(hashMap: HashMap<String, Any>) {
        if (!isNetworkConnected()) {
            showToast(getString(R.string.no_internet))
            return
        }
        try {
            openProgressDialog(this)

            mCompositeDisposable.add(
                ApiClient.create()
                    .addOrRemoveUser(hashMap)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableObserver<ResponseAddRemoveAdmin>() {
                        @RequiresApi(Build.VERSION_CODES.O)
                        override fun onNext(responseAddRemoveAdmin: ResponseAddRemoveAdmin) {
                            showToast(responseAddRemoveAdmin.message)
                            if (responseAddRemoveAdmin.success == 1) {

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

    private fun openDialogMemberClick(userId: Int, userName: String) {
        DialogUtils().showMemberClickIOSDialog(this as AppCompatActivity, "", userName, object : DialogUtils.DialogCallbacks {
            override fun onPositiveButtonClick() {

            }

            override fun onNegativeButtonClick() {

            }

            override fun onDefaultButtonClick(actionName: String) {
                when (actionName) {
                    DialogUtils.MemberClickIfAdminDialogActionName.SEND_MESSAGE.value -> {
                        showToast("Redirect to ChattingActivity")
                    }
                    DialogUtils.MemberClickIfAdminDialogActionName.VIEW_PROFILE.value -> {
                        launchActivity<OtherUserProfileActivity> {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            putExtra(Constants.user_id, userId)
                        }
                    }
                }
            }
        })
    }

    fun callAPIMakeRemoveAdmin(hashMap: HashMap<String, Any>) {
        if (!isNetworkConnected()) {
            showToast(getString(R.string.no_internet))
            return
        }
        try {
            openProgressDialog(this)

            mCompositeDisposable.add(
                ApiClient.create()
                    .makeOrRemoveAdmin(hashMap)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableObserver<ResponseAddRemoveAdmin>() {
                        @RequiresApi(Build.VERSION_CODES.O)
                        override fun onNext(responseAddRemoveAdmin: ResponseAddRemoveAdmin) {
                            showToast(responseAddRemoveAdmin.message)
                            if (responseAddRemoveAdmin.success == 1) {

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