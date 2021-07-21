package com.task.newapp.models


import com.google.gson.annotations.SerializedName

data class ResponseNotification(
    @SerializedName("data")
    val `data`: ArrayList<Data>,
    @SerializedName("message")
    val message: String,
    @SerializedName("success")
    val success: Int
) {
    data class Data(
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("id")
        val id: Int,
        @SerializedName("is_follow")
        val isFollow: String,
        @SerializedName("name")
        val name: String,
        @SerializedName("sound")
        val sound: String,
        @SerializedName("status")
        val status: String,
        @SerializedName("tone_name")
        val toneName: String,
        @SerializedName("updated_at")
        val updatedAt: String
    )
}