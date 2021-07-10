package com.task.newapp.models

import com.google.gson.annotations.SerializedName

data class ChatContacts(
    @SerializedName("id")
    var id: Int,
    @SerializedName("chat_id")
    var chat_id: Int,
    @SerializedName("number")
    var number: String,
    @SerializedName("name")
    var name: String,
    @SerializedName("email")
    var email: String,
    @SerializedName("size")
    var size: Double,
    @SerializedName("delete_for")
    var delete_for: String,
    @SerializedName("created_at")
    var created_at: String,
    @SerializedName("updated_at")
    var updated_at: String,
    @SerializedName("profile_image")
    var profile_image: String,
    @SerializedName("profile_color")
    var profile_color: String,
    @SerializedName("flag")
    var flag: String
)
