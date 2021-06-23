package com.task.newapp.ui.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.task.newapp.R
import com.task.newapp.databinding.ActivityWelcomeBinding
import com.task.newapp.utils.launchActivity


class WelcomeActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var binding: ActivityWelcomeBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_welcome)


    }

    override fun onClick(v: View?) {

        when (v?.id) {
            R.id.centerImage -> {
                launchActivity<LoginActivity> { }
            }
        }
    }


}