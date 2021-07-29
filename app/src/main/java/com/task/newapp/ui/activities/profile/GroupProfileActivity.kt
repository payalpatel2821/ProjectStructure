package com.task.newapp.ui.activities.profile

import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.task.newapp.R
import com.task.newapp.adapter.profile.GroupMemberListAdapter
import com.task.newapp.databinding.ActivityGroupProfileBinding
import com.task.newapp.realmDB.getGroupCreatedUserName
import com.task.newapp.realmDB.getGroupDetail
import com.task.newapp.realmDB.getMyGroupSetting
import com.task.newapp.realmDB.models.Chats
import com.task.newapp.realmDB.models.GroupUser
import com.task.newapp.utils.getCurrentUserId
import com.task.newapp.utils.load

class GroupProfileActivity : AppCompatActivity() {

    private lateinit var groupMemberAdapter: GroupMemberListAdapter
    lateinit var grpDetail: Chats
    lateinit var binding: ActivityGroupProfileBinding
    private val grpID = 31

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_group_profile)
        initView()
    }

    private fun initView() {
        grpDetail = getGroupDetail(grpID)
        binding.ivProfile.load(grpDetail.group_data!!.grp_icon.toString(), false)
        val contentGrpProfile = binding.layoutContentGrpProfile
        contentGrpProfile.txtGrpName.text = grpDetail.name
        contentGrpProfile.txtCreatedByNTime.text = "Created by " + getGroupCreatedUserName(grpDetail.group_data!!.grp_created_by)
        val groupSetting = getMyGroupSetting(grpID,getCurrentUserId())
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
            contentGrpProfile.txtExitDeleteGrp.text = resources.getString(R.string.exit_group)
            contentGrpProfile.txtExitDeleteGrp.tag=resources.getString(R.string.exit_group)
        } else {
            contentGrpProfile.llCustomNotification.visibility = GONE
            contentGrpProfile.viewCustomNotification.visibility = GONE
            contentGrpProfile.txtExitDeleteGrp.text = resources.getString(R.string.delete_group)
        }
        if (grpDetail.group_data?.grp_created_by == getCurrentUserId()) {
            contentGrpProfile.txtReportGrp.visibility = GONE
        } else {
            contentGrpProfile.txtReportGrp.visibility = VISIBLE
        }

        val groupMemberList: ArrayList<GroupUser> = grpDetail.group_user_with_settings.filter { it.status == "Active" }.sortedByDescending { it.is_admin == 1 }.toList() as ArrayList<GroupUser>//.sortedBy { it.user_id== getCurrentUserId() }
        val currentUser = groupMemberList.first { it.user_id == getCurrentUserId() }
        val index: Int = groupMemberList.indexOf(currentUser)
        groupMemberList.removeAt(index)
        groupMemberList.add(0, currentUser)

        groupMemberAdapter = GroupMemberListAdapter(ArrayList())
        val layoutManager = LinearLayoutManager(this)
        contentGrpProfile.rvMemberList.layoutManager = layoutManager
        contentGrpProfile.rvMemberList.adapter = groupMemberAdapter
        groupMemberAdapter.setData(groupMemberList, 5)

        if (groupMemberList.size > 5) {
            contentGrpProfile.txtLoadMore.visibility = VISIBLE
        } else {
            contentGrpProfile.txtLoadMore.visibility = GONE
        }

        contentGrpProfile.txtTotalMember.text = groupMemberList.size.toString() + " MEMBERS"
        contentGrpProfile.txtLoadMore.text = (groupMemberList.size - 5).toString() + " more"

        contentGrpProfile.txtLoadMore.setOnClickListener {
            groupMemberAdapter.setData(groupMemberList, groupMemberList.size)
            contentGrpProfile.txtLoadMore.visibility = GONE
        }
    }


}