package com.task.newapp.models.chat

import com.google.gson.annotations.SerializedName
import com.task.newapp.models.FriendRequestModel
import com.task.newapp.models.OtherUserModel

data class ChatModel(

    @SerializedName("id")
    val id: Long,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("receiver_id")
    val receiverId: Int,
    @SerializedName("is_group_chat")
    val isGroupChat: Int,
    @SerializedName("group_id")
    val groupId: Int,
    @SerializedName("other_user_id")
    val otherUserId: String?="",
    @SerializedName("type")
    val type: String,
    @SerializedName("message_text")
    val messageText: String? = "",
    @SerializedName("is_shared")
    val isShared: Int,
    @SerializedName("is_forward")
    val isForward: Int,
    @SerializedName("is_deleted")
    val isDeleted: Int,
    @SerializedName("deleted_for_all")
    val deletedForAll: Int,
    @SerializedName("deleted_by")
    val deletedBy: Int,
    @SerializedName("tick")
    val tick: Int,
    @SerializedName("tick_status_send")
    val tickStatusSend: Int,
    @SerializedName("is_reply")
    val isReply: Int,
    @SerializedName("chat_id")
    val chatId: Int,
    @SerializedName("is_reply_to_story")
    val isReplyToStory: Int,
    @SerializedName("is_story_reply_back_to_reply")
    val isStoryReplyBackToReply: Int,
    @SerializedName("story_id")
    val storyId: Int,
    @SerializedName("is_secret")
    val isSecret: Int,
    @SerializedName("is_read")
    val isRead: Int,
    @SerializedName("deliver_time")
    val deliverTime: String?,
    @SerializedName("read_time")
    val readTime: String?,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("is_activity_label")
    val isActivityLabel: Int,
    @SerializedName("event")
    val event: String,
    @SerializedName("added_user_id")
    val addedUserId: Int,
    @SerializedName("removed_user_id")
    val removedUserId: Int,
    @SerializedName("make_admin_user_id")
    val makeAdminUserId: Int,
    @SerializedName("remove_admin_user_id")
    val removeAdminUserId: Int,
    @SerializedName("left_user_id")
    val leftUserId: Int,
    @SerializedName("chat_end_time")
    val chatEndTime: String,
    @SerializedName("is_broadcast_chat")
    val isBroadcastChat: Int,
    @SerializedName("broadcast_id")
    val broadcastId: Int,
    @SerializedName("broadcast_chat_id")
    val broadcastChatId: Int,
    @SerializedName("broadcast_add_id")
    val broadcastAddId: Int,
    @SerializedName("broadcast_remove_id")
    val broadcastRemoveId: Int,
    @SerializedName("flag")
    val flag: String,
    @SerializedName("local_id")
    val localId: Long,
    @SerializedName("chat_contents")
    val chatContents: ChatContentsModel?,
    @SerializedName("contacts")
    val contacts: List<ChatContentsModel>?,
    @SerializedName("story")
    val story: List<String>,
    @SerializedName("request")
    val request: FriendRequestModel?,
    @SerializedName("sender")
    val sender: OtherUserModel?,
    @SerializedName("receiver")
    val receiver: OtherUserModel?
)