package com.task.newapp.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.task.newapp.ui.fragments.registration.RegistrationStep2Fragment

class ForgotPasswordActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {

            val bundle = Bundle()
            bundle.putBoolean("fromForgotPass", true)

            val registrationStep2Fragment = RegistrationStep2Fragment()
            registrationStep2Fragment.arguments = bundle

            supportFragmentManager.beginTransaction()
                .add(android.R.id.content, registrationStep2Fragment).commit();
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}