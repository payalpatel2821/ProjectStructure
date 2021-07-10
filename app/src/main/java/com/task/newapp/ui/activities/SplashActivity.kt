package com.task.newapp.ui.activities

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.appizona.yehiahd.fastsave.FastSave
import com.task.newapp.R
import com.task.newapp.databinding.ActivitySplashBinding
import com.task.newapp.utils.Constants
import com.task.newapp.utils.launchActivity

class SplashActivity : AppCompatActivity() {

    lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)

        Handler().postDelayed({
            if (FastSave.getInstance().getBoolean(Constants.isFirstTime, true)) {
                launchActivity<WelcomeActivity> {}
            } else {
                if (FastSave.getInstance().getBoolean(Constants.isLogin, true)) {

                    launchActivity<MainActivity> {}
                } else {
                    launchActivity<LoginActivity> {}
                }
            }
            finish()
        }, 2000)

    }
}