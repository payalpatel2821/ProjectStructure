package com.task.newapp.realmDB.wrapper

import com.task.newapp.realmDB.models.Chats
import java.io.Serializable

data class ChatsWrapperModel(
    var chats: Chats,
    var isTyping: Boolean = false
) : Serializable
