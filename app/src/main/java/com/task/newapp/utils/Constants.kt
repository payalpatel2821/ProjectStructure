package com.task.newapp.utils

class Constants {
    companion object {
        const val user_name = "user_name"
        const val device_token = "device_token"
        const val device_type = "device_type"
        const val email = "email"
        const val latitude = "latitude"
        const val longitude = "longitude"
        const val password = "password"

        enum class RegistrationStepsEnum(val index: Int) {
            STEP_1(0), //Basic information
            STEP_2(1), //Validate yourself
            STEP_3(2), //set password
            STEP_4(3)  //set username

        }

        //-----------------Pref--------------------
        const val prefUser = "prefUser"
        const val prefToken = "prefToken"
        const val prefIsRemember = "prefIsRemember"
        const val prefUserNameRemember = "prefUserNameRemember"
        const val prefPasswordRemember = "prefPasswordRemember"
    }
}
