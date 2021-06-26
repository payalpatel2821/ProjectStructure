package com.task.newapp.models


import com.google.gson.annotations.SerializedName

data class ResponseVerifyOTP(
    @SerializedName("message")
    val message: String,
    @SerializedName("success")
    val success: Int
)