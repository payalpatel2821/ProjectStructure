package com.task.newapp.models

import com.google.gson.annotations.SerializedName

data class OtherUserModel(
    @SerializedName("id")
    val id: Int,
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("last_name")
    val lastName: String,
    @SerializedName("account_id")
    val accountId: String,
    @SerializedName("profile_image")
    val profileImage: String,
    @SerializedName("profile_color")
    val profileColor: String,
    @SerializedName("is_visible")
    val isVisible: Int,
    @SerializedName("flag")
    val flag: String,
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
/* "id": 435,
                "first_name": "Pravin",
                "last_name": "Suvagiya",
                "account_id": "piyush_suvagiya",
                "profile_image": "http://192.168.100.49:8000/profile_image/161517820344195.jpg",
                "profile_color": "#f7411e",
                "is_visible": 0,
                "flag": "user"*/