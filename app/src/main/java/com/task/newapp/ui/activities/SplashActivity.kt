package com.task.newapp.ui.activities

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.appizona.yehiahd.fastsave.FastSave
import com.instacart.library.truetime.TrueTime
import com.task.newapp.R
import com.task.newapp.databinding.ActivitySplashBinding
import com.task.newapp.utils.Constants
import com.task.newapp.utils.DateTimeUtils
import com.task.newapp.utils.launchActivity
import com.task.newapp.utils.showLog


class SplashActivity : AppCompatActivity() {

    lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
        //get true time
       // val noReallyThisIsTheTrueDateAndTime = TrueTime.now()
       // showLog("UTC :", DateTimeUtils.instance?.formatDateTimeToUTC(noReallyThisIsTheTrueDateAndTime, DateTimeUtils.DateFormats.EEEEMMMdyyyyhhmma.label) ?: "")
        Handler().postDelayed({
            if (FastSave.getInstance().getBoolean(Constants.isFirstTime, true)) {
                launchActivity<WelcomeActivity> {}
            } else {
                if (FastSave.getInstance().getBoolean(Constants.isLogin, false)) {
                    launchActivity<MainActivity> {}
                } else {
                    launchActivity<LoginActivity> {}
                }
            }
            finish()
        }, 2000)

    }
}