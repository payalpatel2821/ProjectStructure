package com.task.newapp.models


import com.google.gson.annotations.SerializedName

data class ResponseAddRemoveAdmin(
    @SerializedName("admin_data")
    val adminData: List<AdminData>,
    @SerializedName("lable_data_add_admin")
    val lableDataAddAdmin: List<LableDataAddAdmin>,
    @SerializedName("message")
    val message: String,
    @SerializedName("other_user")
    val otherUser: String,
    @SerializedName("remove_admin")
    val removeAdmin: List<Any>,
    @SerializedName("success")
    val success: Int
) {
    data class AdminData(
        @SerializedName("about")
        val about: Any,
        @SerializedName("first_name")
        val firstName: String,
        @SerializedName("flag")
        val flag: String,
        @SerializedName("id")
        val id: Int,
        @SerializedName("is_admin")
        val isAdmin: Int,
        @SerializedName("is_request")
        val isRequest: Int,
        @SerializedName("is_select")
        val isSelect: Int,
        @SerializedName("last_name")
        val lastName: String,
        @SerializedName("profile_image")
        val profileImage: String,
        @SerializedName("request_receiver_id")
        val requestReceiverId: Any,
        @SerializedName("request_send_id")
        val requestSendId: String
    )

    data class LableDataAddAdmin(
        @SerializedName("added_user_id")
        val addedUserId: Any,
        @SerializedName("audio_ids")
        val audioIds: Any,
        @SerializedName("broadcast_add_id")
        val broadcastAddId: Any,
        @SerializedName("broadcast_chat_id")
        val broadcastChatId: Any,
        @SerializedName("broadcast_id")
        val broadcastId: Any,
        @SerializedName("broadcast_remove_id")
        val broadcastRemoveId: Any,
        @SerializedName("chat_end_time")
        val chatEndTime: Any,
        @SerializedName("chat_id")
        val chatId: Any,
        @SerializedName("contact_ids")
        val contactIds: Any,
        @SerializedName("content_id")
        val contentId: Any,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("deleted_by")
        val deletedBy: Any,
        @SerializedName("deleted_for_all")
        val deletedForAll: Int,
        @SerializedName("document_id")
        val documentId: Any,
        @SerializedName("entry_id")
        val entryId: Int,
        @SerializedName("event")
        val event: String,
        @SerializedName("flag")
        val flag: String,
        @SerializedName("fullname")
        val fullname: Any,
        @SerializedName("group_id")
        val groupId: Int,
        @SerializedName("id")
        val id: Int,
        @SerializedName("image_ids")
        val imageIds: Any,
        @SerializedName("is_activity_label")
        val isActivityLabel: Int,
        @SerializedName("is_broadcast_chat")
        val isBroadcastChat: Int,
        @SerializedName("is_content_reply")
        val isContentReply: Int,
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
        @SerializedName("is_reply_to_message")
        val isReplyToMessage: Int,
        @SerializedName("is_reply_to_story")
        val isReplyToStory: Int,
        @SerializedName("is_replyback_to_reply")
        val isReplybackToReply: Int,
        @SerializedName("is_secret")
        val isSecret: Int,
        @SerializedName("is_shared")
        val isShared: Int,
        @SerializedName("left_user_id")
        val leftUserId: Any,
        @SerializedName("location_id")
        val locationId: Any,
        @SerializedName("make_admin_user_id")
        val makeAdminUserId: Int,
        @SerializedName("message_text")
        val messageText: String,
        @SerializedName("other_user_id")
        val otherUserId: String,
        @SerializedName("read_member_list")
        val readMemberList: Any,
        @SerializedName("receiver_id")
        val receiverId: Any,
        @SerializedName("remove_admin_user_id")
        val removeAdminUserId: Any,
        @SerializedName("removed_user_id")
        val removedUserId: Any,
        @SerializedName("story_id")
        val storyId: Any,
        @SerializedName("tick")
        val tick: String,
        @SerializedName("tick_status_send")
        val tickStatusSend: String,
        @SerializedName("type")
        val type: String,
        @SerializedName("updated_at")
        val updatedAt: String,
        @SerializedName("user_id")
        val userId: Int,
        @SerializedName("video_ids")
        val videoIds: Any,
        @SerializedName("voice_ids")
        val voiceIds: Any
    )
}