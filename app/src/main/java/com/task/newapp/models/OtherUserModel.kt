package com.task.newapp.models

import com.google.gson.annotations.SerializedName

data class OtherUserModel(
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("flag")
    val flag: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("last_name")
    val lastName: String,
    @SerializedName("profile_image")
    val profileImage: String,
    @SerializedName("profile_color")
    val profileColor: String,
)
