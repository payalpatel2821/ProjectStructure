package com.task.newapp.models


import com.google.gson.annotations.SerializedName

data class ReponseGetUsername(
    @SerializedName("data")
    val `data`: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("success")
    val success: Int
)