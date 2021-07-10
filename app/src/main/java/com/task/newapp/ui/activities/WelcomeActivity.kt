package com.task.newapp.ui.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.appizona.yehiahd.fastsave.FastSave
import com.task.newapp.R
import com.task.newapp.databinding.ActivityWelcomeBinding
import com.task.newapp.utils.Constants
import com.task.newapp.utils.launchActivity


class WelcomeActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var binding: ActivityWelcomeBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_welcome)
        binding.content.startRippleAnimation();
    }

    override fun onClick(v: View?) {

        when (v?.id) {
            R.id.centerImage -> {
                FastSave.getInstance().saveBoolean(Constants.isFirstTime, false)
                launchActivity<LoginActivity> { }
                finish()
            }
        }
    }


}