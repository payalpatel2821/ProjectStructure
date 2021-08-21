package com.task.newapp.models.chat

import com.google.gson.annotations.SerializedName

data class ChatLocations(
    @SerializedName("locationid")
    var locationid: Int,
    @SerializedName("id")
    var id: Int,
    @SerializedName("chat_id")
    var chat_id: Int,
    @SerializedName("created_at")
    var created_at: String,
    @SerializedName("updated_at")
    var updated_at: String,
    @SerializedName("delete_for")
    var delete_for: String,
    @SerializedName("end_time")
    var end_time: String,
    @SerializedName("latitude")
    var latitude: String,
    @SerializedName("longitude")
    var longitude: String,
    @SerializedName("location")
    var location: String,
    @SerializedName("sharing_time")
    var sharing_time: String,
    @SerializedName("size")
    var size: Double,
    @SerializedName("type")
    var type: String,
    @SerializedName("user_id")
    var user_id: Int
)
