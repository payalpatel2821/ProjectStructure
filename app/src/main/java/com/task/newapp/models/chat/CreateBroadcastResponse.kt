package com.task.newapp.models.chat


import com.google.gson.annotations.SerializedName
import com.task.newapp.models.ChatLabel
import com.task.newapp.models.LoginResponse.Broadcast

data class CreateBroadcastResponse(
    @SerializedName("data")
    val `data`: Broadcast,
    @SerializedName("lable_data")
    val lableData: ChatLabel,
    @SerializedName("message")
    val message: String,
    @SerializedName("success")
    val success: Int
) {
    data class Data(
        @SerializedName("chat")
        val chat: Chat,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("icon")
        val icon: String,
        @SerializedName("id")
        val id: Int,
        @SerializedName("name")
        val name: String,
        @SerializedName("other_user_id")
        val otherUserId: String,
        @SerializedName("total_users")
        val totalUsers: Int,
        @SerializedName("updated_at")
        val updatedAt: String,
        @SerializedName("user_id")
        val userId: Int
    ) {
        data class Chat(
            @SerializedName("added_user_id")
            val addedUserId: Any,
            @SerializedName("broadcast_add_id")
            val broadcastAddId: Any,
            @SerializedName("broadcast_chat_id")
            val broadcastChatId: Any,
            @SerializedName("broadcast_id")
            val broadcastId: Int,
            @SerializedName("broadcast_remove_id")
            val broadcastRemoveId: Any,
            @SerializedName("chat_end_time")
            val chatEndTime: Any,
            @SerializedName("chat_id")
            val chatId: Any,
            @SerializedName("created_at")
            val createdAt: String,
            @SerializedName("deleted_by")
            val deletedBy: Any,
            @SerializedName("deleted_for_all")
            val deletedForAll: Int,
            @SerializedName("deliver_time")
            val deliverTime: Any,
            @SerializedName("event")
            val event: String,
            @SerializedName("flag")
            val flag: String,
            @SerializedName("group_id")
            val groupId: Any,
            @SerializedName("id")
            val id: Int,
            @SerializedName("is_activity_label")
            val isActivityLabel: Int,
            @SerializedName("is_broadcast_chat")
            val isBroadcastChat: Int,
            @SerializedName("is_deleted")
            val isDeleted: Int,
            @SerializedName("is_forward")
            val isForward: Int,
            @SerializedName("is_group_chat")
            val isGroupChat: Int,
            @SerializedName("is_read")
            val isRead: Int,
            @SerializedName("is_reply")
            val isReply: Int,
            @SerializedName("is_reply_to_story")
            val isReplyToStory: Int,
            @SerializedName("is_secret")
            val isSecret: Int,
            @SerializedName("is_shared")
            val isShared: Int,
            @SerializedName("left_user_id")
            val leftUserId: Any,
            @SerializedName("make_admin_user_id")
            val makeAdminUserId: Any,
            @SerializedName("message_text")
            val messageText: String,
            @SerializedName("other_user_id")
            val otherUserId: String,
            @SerializedName("read_time")
            val readTime: Any,
            @SerializedName("receiver_id")
            val receiverId: Any,
            @SerializedName("remove_admin_user_id")
            val removeAdminUserId: Any,
            @SerializedName("removed_user_id")
            val removedUserId: Any,
            @SerializedName("story_id")
            val storyId: Any,
            @SerializedName("tick")
            val tick: Int,
            @SerializedName("tick_status_send")
            val tickStatusSend: Int,
            @SerializedName("type")
            val type: String,
            @SerializedName("updated_at")
            val updatedAt: String,
            @SerializedName("user_id")
            val userId: Int
        )
    }

    data class LableData(
        @SerializedName("added_user_id")
        val addedUserId: Any,
        @SerializedName("broadcast_add_id")
        val broadcastAddId: Any,
        @SerializedName("broadcast_chat_id")
        val broadcastChatId: Any,
        @SerializedName("broadcast_id")
        val broadcastId: Int,
        @SerializedName("broadcast_remove_id")
        val broadcastRemoveId: Any,
        @SerializedName("chat_end_time")
        val chatEndTime: Any,
        @SerializedName("chat_id")
        val chatId: Any,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("deleted_by")
        val deletedBy: Any,
        @SerializedName("deleted_for_all")
        val deletedForAll: Int,
        @SerializedName("deliver_time")
        val deliverTime: Any,
        @SerializedName("event")
        val event: String,
        @SerializedName("flag")
        val flag: String,
        @SerializedName("group_id")
        val groupId: Any,
        @SerializedName("id")
        val id: Int,
        @SerializedName("is_activity_label")
        val isActivityLabel: Int,
        @SerializedName("is_broadcast_chat")
        val isBroadcastChat: Int,
        @SerializedName("is_deleted")
        val isDeleted: Int,
        @SerializedName("is_forward")
        val isForward: Int,
        @SerializedName("is_group_chat")
        val isGroupChat: Int,
        @SerializedName("is_read")
        val isRead: Int,
        @SerializedName("is_reply")
        val isReply: Int,
        @SerializedName("is_reply_to_story")
        val isReplyToStory: Int,
        @SerializedName("is_secret")
        val isSecret: Int,
        @SerializedName("is_shared")
        val isShared: Int,
        @SerializedName("left_user_id")
        val leftUserId: Any,
        @SerializedName("make_admin_user_id")
        val makeAdminUserId: Any,
        @SerializedName("message_text")
        val messageText: String,
        @SerializedName("other_user_id")
        val otherUserId: String,
        @SerializedName("read_time")
        val readTime: Any,
        @SerializedName("receiver_id")
        val receiverId: Any,
        @SerializedName("remove_admin_user_id")
        val removeAdminUserId: Any,
        @SerializedName("removed_user_id")
        val removedUserId: Any,
        @SerializedName("story_id")
        val storyId: Any,
        @SerializedName("tick")
        val tick: Int,
        @SerializedName("tick_status_send")
        val tickStatusSend: Int,
        @SerializedName("type")
        val type: String,
        @SerializedName("updated_at")
        val updatedAt: String,
        @SerializedName("user_id")
        val userId: Int
    )
}