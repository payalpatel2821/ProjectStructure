package com.task.newapp.models.chat

import com.google.gson.annotations.SerializedName

data class ChatLabel(
    @SerializedName("id")
    val id: Int,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("receiver_id")
    val receiverId: Int,
    @SerializedName("is_group_chat")
    val isGroupChat: Int,
    @SerializedName("group_id")
    val groupId: Int,
    @SerializedName("other_user_id")
    val otherUserId: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("message_text")
    val messageText: String,
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
    val tick: String,
    @SerializedName("tick_status_send")
    val tickStatusSend: String,
    @SerializedName("is_reply")
    val isReply: Int,
    @SerializedName("chat_id")
    val chatId: Int,
    @SerializedName("is_reply_to_story")
    val isReplyToStory: Int,
    @SerializedName("story_id")
    val storyId: Int,
    @SerializedName("is_story_reply_back_to_reply")
    val isStoryReplyBackToReply: Int,
    @SerializedName("is_secret")
    val isSecret: Int,
    @SerializedName("is_read")
    val isRead: Int,
    @SerializedName("deliver_time")
    val deliverTime: String,
    @SerializedName("read_time")
    val readTime: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("is_activity_label")
    val isActivityLabel: Int,
    @SerializedName("event")
    val event: String,
    @SerializedName("added_user_id")
    val addedUserId: Any,
    @SerializedName("removed_user_id")
    val removedUserId: Any,
    @SerializedName("make_admin_user_id")
    val makeAdminUserId: Any,
    @SerializedName("remove_admin_user_id")
    val removeAdminUserId: Any,
    @SerializedName("left_user_id")
    val leftUserId: Any,
    @SerializedName("chat_end_time")
    val chatEndTime: Any,
    @SerializedName("is_broadcast_chat")
    val isBroadcastChat: Int,
    @SerializedName("broadcast_id")
    val broadcastId: Int,
    @SerializedName("broadcast_chat_id")
    val broadcastChatId: Int,
    @SerializedName("broadcast_add_id")
    val broadcastAddId: Any,
    @SerializedName("broadcast_remove_id")
    val broadcastRemoveId: Any,
    @SerializedName("flag")
    val flag: String,













   /* @SerializedName("audio_ids")
    val audioIds: Any,
    @SerializedName("contact_ids")
    val contactIds: Any,
    @SerializedName("content_id")
    val contentId: Int,
    @SerializedName("document_id")
    val documentId: Any,
    @SerializedName("entry_id")
    val entryId: Int,
    @SerializedName("fullname")
    val fullname: Any,
    @SerializedName("image_ids")
    val imageIds: Any,
    @SerializedName("is_content_reply")
    val isContentReply: Int,
    @SerializedName("is_reply_to_message")
    val isReplyToMessage: Int,
    @SerializedName("is_replyback_to_reply")
    val isReplybackToReply: Int,
    @SerializedName("location_id")
    val locationId: Any,
    @SerializedName("read_member_list")
    val readMemberList: Any,
    @SerializedName("sender_id")
    val senderId: Int,
    @SerializedName("video_ids")
    val videoIds: Any,
    @SerializedName("voice_ids")
    val voiceIds: Any*/
)