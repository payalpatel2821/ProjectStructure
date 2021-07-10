package com.task.newapp.models

import com.google.gson.annotations.SerializedName
import com.task.newapp.realmDB.models.Chats

data class ChatListAdapterModel(
    @SerializedName("chats")
    val chats: Chats,
    @SerializedName("unread_message_count")
    val unreadMessageCount: Int = 0,
    @SerializedName("is_online")
    val isOnline: Boolean = false
)
