package com.task.newapp.models

import com.google.gson.annotations.SerializedName

data class ChatVoiceMessage(
    @SerializedName("id")
    var id: Int,
    @SerializedName("chat_id")
    var chat_id: Int,
    @SerializedName("voice_msg")
    var voice_msg: String,
    @SerializedName("size")
    var size: Double,
    @SerializedName("created_at")
    var created_at: String,
    @SerializedName("updated_at")
    var updated_at: String,
    @SerializedName("duration")
    var duration: String,
    @SerializedName("delete_for")
    var delete_for: String,
    @SerializedName("local_path")
    var local_path: String
)
