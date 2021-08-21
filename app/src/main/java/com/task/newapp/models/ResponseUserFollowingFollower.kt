package com.task.newapp.models


import com.google.gson.annotations.SerializedName

data class ResponseUserFollowingFollower(
    @SerializedName("data")
    val `data`: List<OtherUserModel>,
    @SerializedName("message")
    val message: String,
    @SerializedName("success")
    val success: Int
)
