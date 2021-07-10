package com.task.newapp.models


import com.google.gson.annotations.SerializedName

data class ResponseGetUsername(
    @SerializedName("data")
    val `data`: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("success")
    val success: Int,

)