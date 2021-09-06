package com.task.newapp.models

import com.google.gson.annotations.SerializedName

data class OtherUserModel(
    @SerializedName("account_id")
    val accountId: String,
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("flag")
    val flag: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("is_visible")
    val isVisible: Int,
    @SerializedName("last_name")
    val lastName: String,
    @SerializedName("profile_color")
    val profileColor: String,
    @SerializedName("profile_image")
    val profileImage: String,
    @SerializedName("is_follow")
    var is_follow: Int,
    @SerializedName("user_id")
    var user_id: Int,
    @SerializedName("follow_id")
    var follow_id: Int,
    @SerializedName("view_date")
    val view_date: String,

    //Add New
    var isSelected: Boolean = false
)
