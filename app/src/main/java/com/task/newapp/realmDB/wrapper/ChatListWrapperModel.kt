package com.task.newapp.realmDB.wrapper

import com.task.newapp.realmDB.models.ChatList
import java.io.Serializable

data class ChatListWrapperModel(
    var chatList: ChatList,
    var isSelect: Boolean = false
) : Serializable
