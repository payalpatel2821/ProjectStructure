package com.task.newapp.models.post

import com.google.gson.annotations.SerializedName

data class PostSocket(
    @SerializedName("user_id") var user_id: Int,
    @SerializedName("post_id") var post_id: Int
)