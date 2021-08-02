package com.task.newapp.ui.activities.post

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.task.newapp.R
import com.task.newapp.databinding.ActivityShowPostBinding
import com.task.newapp.databinding.FragmentPostBinding

class ShowPostActivity : AppCompatActivity() {

    companion object {
        lateinit var activity: Activity
    }

    private lateinit var binding: ActivityShowPostBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_show_post)
        activity = this
    }


}