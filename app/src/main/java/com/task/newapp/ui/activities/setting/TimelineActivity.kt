package com.task.newapp.ui.activities.setting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.task.newapp.R
import com.task.newapp.databinding.ActivityProfileBinding
import com.task.newapp.databinding.ActivityTimelineBinding

class TimelineActivity : AppCompatActivity() {

    lateinit var binding: ActivityTimelineBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_timeline)
        initView()
    }

    private fun initView() {
        binding.toolbarLayout.txtTitle.text = resources.getString(R.string.timeline)
        setSupportActionBar(binding.toolbarLayout.activityMainToolbar)
    }
}