package com.task.newapp.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.task.newapp.ui.fragments.registration.RegistrationStep2Fragment

class ForgotPasswordActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(android.R.id.content, RegistrationStep2Fragment()).commit();
        }

    }
}