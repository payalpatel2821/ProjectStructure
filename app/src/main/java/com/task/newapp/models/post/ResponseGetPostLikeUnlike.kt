package com.task.newapp.models.post

import com.google.gson.annotations.SerializedName

data class ResponseGetPostLikeUnlike(
    @SerializedName("success") var success: Int,
    @SerializedName("message") var message: String,
    @SerializedName("data") var total_like: Int
)