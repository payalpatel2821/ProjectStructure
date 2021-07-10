package com.task.newapp.models

import com.google.gson.annotations.SerializedName

data class ChatModel(
    @SerializedName("local_id")
    var localId: Int,
    @SerializedName("added_user_id")
    val addedUserId: Int,
    @SerializedName("audio_ids")
    val audioIds: String,
    @SerializedName("audios")
    val audios: List<ChatAudios>,
    @SerializedName("broadcast_add_id")
    val broadcastAddId: Int,
    @SerializedName("broadcast_chat_id")
    val broadcastChatId: Int,
    @SerializedName("broadcast_id")
    val broadcastId: Int,
    @SerializedName("broadcast_remove_id")
    val broadcastRemoveId: Int,
    @SerializedName("chat_contents")
    val chatContents: List<ChatContentsModel>,
    @SerializedName("chat_end_time")
    val chatEndTime: String,
    @SerializedName("chat_id")
    val chatId: Int,
    @SerializedName("contact_ids")
    val contactIds: String,
    @SerializedName("contacts")
    val contacts: List<ChatContacts>,
    @SerializedName("content_id")
    val contentId: Int,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("deleted_by")
    val deletedBy: Int,
    @SerializedName("deleted_for_all")
    val deletedForAll: Int,
    @SerializedName("document_id")
    val documentId: String,
    @SerializedName("documents")
    val documents: List<ChatDocumentModel>,
    @SerializedName("entry_id")
    val entryId: Int,
    @SerializedName("event")
    val event: String,
    @SerializedName("flag")
    val flag: String,
    @SerializedName("fullname")
    val fullname: String,
    @SerializedName("group_id")
    val groupId: Int,
    @SerializedName("id")
    val id: Int,
    @SerializedName("image_ids")
    val imageIds: String,
    @SerializedName("images")
    val images: List<String>,
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
    val leftUserId: String,
    @SerializedName("location")
    val location: List<ChatLocations>,
    @SerializedName("location_id")
    val locationId: String,
    @SerializedName("make_admin_user_id")
    val makeAdminUserId: String,
    @SerializedName("message_text")
    val messageText: String,
    @SerializedName("other_user_id")
    val otherUserId: String,
    @SerializedName("read_member_list")
    val readMemberList: String,
    @SerializedName("receiver_id")
    val receiverId: Int,
    @SerializedName("remove_admin_user_id")
    val removeAdminUserId: String,
    @SerializedName("removed_user_id")
    val removedUserId: String,
    @SerializedName("request")
    val request: FriendRequestModel,
    @SerializedName("sender")
    val sender: OtherUserModel,
    @SerializedName("receiver")
    val receiver: OtherUserModel,
    @SerializedName("story")
    val story: List<String>,
    @SerializedName("story_id")
    val storyId: Int,
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
    val videoIds: String,
    @SerializedName("videos")
    val videos: List<String>,
    @SerializedName("voice_ids")
    val voiceIds: String,
    @SerializedName("voice_msg")
    val voiceMsg: List<ChatVoiceMessage>
)