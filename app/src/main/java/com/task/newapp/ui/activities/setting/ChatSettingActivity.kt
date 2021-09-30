package com.task.newapp.ui.activities.setting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import com.task.newapp.R
import com.task.newapp.databinding.ActivityChatSettingBinding
import com.task.newapp.databinding.ActivityNotificationsBinding

class ChatSettingActivity : AppCompatActivity() {

    lateinit var binding: ActivityChatSettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat_setting)
        initView()
    }

    private fun initView() {
        binding.toolbarLayout.txtTitle.text = resources.getString(R.string.chat)
        setSupportActionBar(binding.toolbarLayout.activityMainToolbar)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

}