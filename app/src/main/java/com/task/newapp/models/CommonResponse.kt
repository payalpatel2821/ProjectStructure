package com.task.newapp.models


import com.google.gson.annotations.SerializedName

data class CommonResponse(

    @SerializedName("message")
    val message: String,

    @SerializedName("success")
    val success: Int,

    )



