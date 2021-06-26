package com.task.newapp.utils

class Constants {
    companion object {
        const val deviceToken = "c926RJ-JQS62C7bolZsMrq:APA91bF-_8V1mRc-cpuKlTmw2kL7iYIua9HI4uZye76jR1lII7gDZT8HOABpBIubisYO7bNnyDbYNNVYoiX47bwkRODU6vAWJjz9z3wNLBCSni5dyzTjc91xQ3FAWDalu4BwZvA4p0h0"
        const val user_name = "user_name"
        const val device_token = "device_token"
        const val device_type = "device_type"
        const val email = "email"
        const val mobile = "mobile"
        const val typeCode = "type"
        const val latitude = "latitude"
        const val longitude = "longitude"
        const val password = "password"
        const val password_confirmation = "password_confirmation"
        const val code = "code"
        const val first_name = "first_name"
        const val last_name = "last_name"
        const val account_id = "account_id"
        const val profile_image = "profile_image"
        const val userClass = "userClass"
        const val isLogin = "isLogin"

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
