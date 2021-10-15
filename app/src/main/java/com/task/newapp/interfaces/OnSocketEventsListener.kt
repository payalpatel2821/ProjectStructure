package com.task.newapp.interfaces

import com.task.newapp.models.socket.DeleteChatSocket
import com.task.newapp.models.socket.PostSocket
import com.task.newapp.realmDB.models.ChatList

interface OnSocketEventsListener {
    fun onOnlineOfflineSocketEvent(userId: Int, status: Boolean)
    fun onPostLikeDislikeSocketEvent(postSocket: PostSocket)
    fun onNewMessagePrivateSocketEvent(chatList: ChatList)
    fun onUserTypingSocketEvent(receiverId: Int)
    fun onUserStopTypingSocketEvent(receiverId: Int)
    fun onDeletePrivateChatMessageResponse(deleteChatSocket: DeleteChatSocket)
    fun onAddPostCommentSocketEvent(postSocket: PostSocket)
    fun onAddPostCommentReplySocketEvent(postSocket: PostSocket)
    fun onDeletePostCommentSocketEvent(postSocket: PostSocket)
    fun onDeletePostSocketEvent(postSocket: PostSocket)
}