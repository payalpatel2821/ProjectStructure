package com.task.newapp.realmDB.wrapper

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SelectFriendWrapperModel(
    @SerializedName("id")
    val id: Int,
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("last_name")
    val lastName: String,
    @SerializedName("user_name")
    val userName: String,
    @SerializedName("profile_image")
    val profileImage: String,
    @SerializedName("profile_color")
    val profileColor: String,
    @SerializedName("is_checked")
    var isChecked: Boolean,
    @SerializedName("is_edit")
    var isEdit: Boolean
) : Serializable
