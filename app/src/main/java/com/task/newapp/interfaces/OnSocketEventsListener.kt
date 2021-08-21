package com.task.newapp.interfaces

import com.task.newapp.realmDB.models.ChatList

interface OnSocketEventsListener {
    fun onOnlineOfflineSocketEvent(userId: Int, status: Boolean)
    fun onPostLikeDislikeSocketEvent()
    fun onNewMessagePrivateSocketEvent(chatList: ChatList)
    fun onUserTypingSocketEvent(receiverId: Int)
    fun onUserStopTypingSocketEvent(receiverId: Int)
}