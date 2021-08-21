package com.task.newapp.models.chat


import com.google.gson.annotations.SerializedName
import com.task.newapp.models.chat.ChatModel

data class ResponseGetUnreadMessage(
    @SerializedName("data")
    val `data`: List<ChatModel>,
    @SerializedName("message")
    val message: String,
    @SerializedName("success")
    val success: Int
)

