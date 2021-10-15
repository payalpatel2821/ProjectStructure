package com.task.newapp.models.socket

import com.google.gson.annotations.SerializedName

data class DeleteChatSocket(
    @SerializedName("chat_ids")
    var chat_ids: String,

    @SerializedName("receiver_id")
    var receiver_id: Int,

    @SerializedName("sender_id")
    var sender_id: Int,

    @SerializedName("group_id")
    var group_id: Int = 0,

    @SerializedName("broadcast_id")
    var broadcast_id: Int = 0,

    @SerializedName("other_user_id")
    var other_user_id: String? = null
)