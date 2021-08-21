package com.task.newapp.models.chat

import com.google.gson.annotations.SerializedName

data class ChatAudios(
    @SerializedName("id")
    var id: Int,
    @SerializedName("chat_id")
    var chat_id: Int,
    @SerializedName("audio")
    var audio: String,
    @SerializedName("audio_caption")
    var audio_caption: String,
    @SerializedName("size")
    var size: Double,
    @SerializedName("delete_for")
    var delete_for: String,
    @SerializedName("duration")
    var duration: String,
    @SerializedName("created_at")
    var created_at: String,
    @SerializedName("updated_at")
    var updated_at: String,
    @SerializedName("local_path")
    var local_path: String,
)
