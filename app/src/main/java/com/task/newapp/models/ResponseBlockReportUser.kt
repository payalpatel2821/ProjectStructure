package com.task.newapp.models


import com.google.gson.annotations.SerializedName

data class ResponseBlockReportUser(
    @SerializedName("data")
    val `data`: Any,
    @SerializedName("message")
    val message: String,
    @SerializedName("success")
    val success: Int
)