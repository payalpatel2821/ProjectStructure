package com.task.newapp.ui.activities.profile

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.task.newapp.R
import com.task.newapp.adapter.profile.GroupListAdapter
import com.task.newapp.databinding.ActivityGroupListBinding
import com.task.newapp.realmDB.getCommonGroup
import com.task.newapp.realmDB.getMyGroup
import com.task.newapp.realmDB.models.Chats
import com.task.newapp.utils.Constants
import com.task.newapp.utils.launchActivity


class GroupListActivity : AppCompatActivity() {
    private lateinit var myGrps: List<Chats>
    private lateinit var groupadapter: GroupListAdapter
    private lateinit var binding: ActivityGroupListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_group_list)
        setData()
    }

    fun setData() {
        binding.toolbarLayout.txtTitle.text = getString(R.string.groups_list)
        setSupportActionBar(binding.toolbarLayout.activityMainToolbar)
        myGrps = if (intent.getStringExtra(Constants.type) == "My") {
            getMyGroup()
        } else {
            val userID = intent.getIntExtra(Constants.user_id, 0)
            getCommonGroup(userID)
        }
        groupadapter = GroupListAdapter(ArrayList())
        val layoutManager = LinearLayoutManager(applicationContext)
        binding.rvGroupList.layoutManager = layoutManager
        binding.rvGroupList.adapter = groupadapter
        groupadapter.setData(myGrps)
        if (myGrps.isEmpty()) {
            binding.llEmptyGroup.visibility = VISIBLE
        } else {
            binding.llEmptyGroup.visibility = GONE
        }
        groupadapter.onItemClick = { groupId, position ->
            launchActivity<GroupProfileActivity> {
                putExtra(Constants.group_id,groupId)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.iv_back -> {
                onBackPressed()
            }
        }
    }
}