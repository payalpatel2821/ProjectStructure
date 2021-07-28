package com.task.newapp.ui.activities.profile

import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.task.newapp.R
import com.task.newapp.databinding.ActivityGroupProfileBinding
import com.task.newapp.realmDB.getGroupCreatedUserName
import com.task.newapp.realmDB.getGroupDetail
import com.task.newapp.realmDB.getMyGroupSetting
import com.task.newapp.realmDB.models.Chats
import com.task.newapp.utils.getCurrentUserId
import com.task.newapp.utils.load

class GroupProfileActivity : AppCompatActivity() {

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
        var contentGrpProfile = binding.layoutContentGrpProfile
        contentGrpProfile.txtGrpName.text = grpDetail.name
        contentGrpProfile.txtCreatedByNTime.text = "Created by " + getGroupCreatedUserName(grpDetail.group_data!!.grp_created_by)
        val groupSetting = getMyGroupSetting(getCurrentUserId())
        if (groupSetting?.is_allow_to_edit_info == 1) {
            contentGrpProfile.ivEditProfile.visibility = VISIBLE
        } else {
            contentGrpProfile.ivEditProfile.visibility = GONE
        }
        if (groupSetting?.is_admin == 1) {
            contentGrpProfile.txtGrpSetting.visibility = VISIBLE
        } else {
            contentGrpProfile.txtGrpSetting.visibility = GONE
        }

    }
}