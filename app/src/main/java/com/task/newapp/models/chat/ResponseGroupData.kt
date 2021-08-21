package com.task.newapp.models.chat

import com.google.gson.annotations.SerializedName

data class ResponseGroupData(
    @SerializedName("data")
    val `data`: List<GetAllGroup>,
    @SerializedName("message")
    val message: String,
    @SerializedName("success")
    val success: Int
)
