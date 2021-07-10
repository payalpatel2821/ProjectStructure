package com.task.newapp.models


import com.google.gson.annotations.SerializedName

data class ResponseGetUnreadMessage(
    @SerializedName("data")
    val `data`: List<ChatModel>,
    @SerializedName("message")
    val message: String,
    @SerializedName("success")
    val success: Int
)

