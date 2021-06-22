package com.task.newapp.models


import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("archive_data")
    val archiveData: List<ArchiveData>,
    @SerializedName("backup")
    val backup: Int,
    @SerializedName("birthday_reminder")
    val birthdayReminder: List<Any>,
    @SerializedName("block_user")
    val blockUser: List<Any>,
    @SerializedName("broadcast")
    val broadcast: List<Broadcast>,
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("friend_reminder")
    val friendReminder: List<Any>,
    @SerializedName("friend_settings")
    val friendSettings: List<FriendSetting>,
    @SerializedName("get_all_group")
    val getAllGroup: List<GetAllGroup>,
    @SerializedName("get_all_request")
    val getAllRequest: List<GetAllRequest>,
    @SerializedName("hook_data")
    val hookData: List<HookData>,
    @SerializedName("message")
    val message: String,
    @SerializedName("post_setting")
    val postSetting: PostSetting,
    @SerializedName("success")
    val success: Int,
    @SerializedName("user_setting")
    val userSetting: UserSetting,
    @SerializedName("wedding_reminder")
    val weddingReminder: List<Any>
) {
    data class ArchiveData(
        @SerializedName("archiver")
        val archiver: Any,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("friend_id")
        val friendId: Any,
        @SerializedName("group_id")
        val groupId: Int,
        @SerializedName("id")
        val id: Int,
        @SerializedName("is_friend")
        val isFriend: Int,
        @SerializedName("is_group")
        val isGroup: Int,
        @SerializedName("lock_lbl_data")
        val lockLblData: Any,
        @SerializedName("updated_at")
        val updatedAt: String,
        @SerializedName("user_id")
        val userId: Int
    )

    data class Broadcast(
        @SerializedName("chat")
        val chat: Chat,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("icon")
        val icon: String,
        @SerializedName("id")
        val id: Int,
        @SerializedName("is_deleted")
        val isDeleted: Int,
        @SerializedName("name")
        val name: String,
        @SerializedName("other_user_id")
        val otherUserId: String,
        @SerializedName("total_users")
        val totalUsers: String,
        @SerializedName("updated_at")
        val updatedAt: String,
        @SerializedName("user_id")
        val userId: Int
    ) {
        data class Chat(
            @SerializedName("added_user_id")
            val addedUserId: Any,
            @SerializedName("audio_ids")
            val audioIds: Any,
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
            @SerializedName("contact_ids")
            val contactIds: Any,
            @SerializedName("content_id")
            val contentId: Any,
            @SerializedName("created_at")
            val createdAt: String,
            @SerializedName("deleted_by")
            val deletedBy: Int,
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
            val groupId: Any,
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
            val makeAdminUserId: Any,
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

    data class Data(
        @SerializedName("token")
        val token: String,
        @SerializedName("user")
        val user: User
    ) {
        data class User(
            @SerializedName("about")
            val about: String,
            @SerializedName("account_id")
            val accountId: String,
            @SerializedName("age")
            val age: String,
            @SerializedName("anniversary")
            val anniversary: String,
            @SerializedName("app_version")
            val appVersion: String,
            @SerializedName("blood_gp")
            val bloodGp: String,
            @SerializedName("created_at")
            val createdAt: String,
            @SerializedName("date_of_birth")
            val dateOfBirth: String,
            @SerializedName("del_request")
            val delRequest: Int,
            @SerializedName("delete_reason")
            val deleteReason: Any,
            @SerializedName("device_arn")
            val deviceArn: String,
            @SerializedName("device_token")
            val deviceToken: String,
            @SerializedName("device_type")
            val deviceType: String,
            @SerializedName("email")
            val email: String,
            @SerializedName("expired_at")
            val expiredAt: Any,
            @SerializedName("first_name")
            val firstName: String,
            @SerializedName("flag")
            val flag: String,
            @SerializedName("forgot_token")
            val forgotToken: Any,
            @SerializedName("gender")
            val gender: String,
            @SerializedName("id")
            val id: Int,
            @SerializedName("is_blocked_by_admin")
            val isBlockedByAdmin: Int,
            @SerializedName("is_deleted")
            val isDeleted: Int,
            @SerializedName("is_last_seen")
            val isLastSeen: Int,
            @SerializedName("is_login")
            val isLogin: Int,
            @SerializedName("is_online")
            val isOnline: Int,
            @SerializedName("is_reported_by_admin")
            val isReportedByAdmin: Int,
            @SerializedName("is_verified_email")
            val isVerifiedEmail: Int,
            @SerializedName("last_logout_time")
            val lastLogoutTime: Any,
            @SerializedName("last_name")
            val lastName: String,
            @SerializedName("last_seen_time")
            val lastSeenTime: String,
            @SerializedName("latitude")
            val latitude: String,
            @SerializedName("location")
            val location: Any,
            @SerializedName("longitude")
            val longitude: String,
            @SerializedName("mobile")
            val mobile: Any,
            @SerializedName("profession")
            val profession: Any,
            @SerializedName("profile_image")
            val profileImage: String,
            @SerializedName("profile_views")
            val profileViews: Int,
            @SerializedName("request_date")
            val requestDate: String,
            @SerializedName("secret_password")
            val secretPassword: String,
            @SerializedName("status")
            val status: String,
            @SerializedName("u_name")
            val uName: String,
            @SerializedName("updated_at")
            val updatedAt: String
        )
    }

    data class FriendSetting(
        @SerializedName("call_rigtone")
        val callRigtone: String,
        @SerializedName("call_vibrate")
        val callVibrate: String,
        @SerializedName("friend_id")
        val friendId: Int,
        @SerializedName("id")
        val id: Int,
        @SerializedName("is_custom_notification_enable")
        val isCustomNotificationEnable: Int,
        @SerializedName("is_popup_notification")
        val isPopupNotification: Int,
        @SerializedName("mute_notification")
        val muteNotification: Int,
        @SerializedName("notification_tone_id")
        val notificationToneId: Int,
        @SerializedName("sound")
        val sound: String,
        @SerializedName("use_high_priority_notification")
        val useHighPriorityNotification: Int,
        @SerializedName("user_id")
        val userId: Int,
        @SerializedName("vibrate_status")
        val vibrateStatus: String
    )

    data class GetAllGroup(
        @SerializedName("add_user_in_gp")
        val addUserInGp: AddUserInGp,
        @SerializedName("create_group_lbl")
        val createGroupLbl: CreateGroupLbl,
        @SerializedName("group_data")
        val groupData: GroupData,
        @SerializedName("group_user_with_settings")
        val groupUserWithSettings: List<GroupUserWithSetting>
    ) {
        data class AddUserInGp(
            @SerializedName("added_user_id")
            val addedUserId: Int,
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
            val deletedBy: Int,
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
            val makeAdminUserId: Any,
            @SerializedName("message_text")
            val messageText: Any,
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

        data class CreateGroupLbl(
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
            val deletedBy: Int,
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
            val makeAdminUserId: Any,
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

        data class GroupData(
            @SerializedName("created_at")
            val createdAt: String,
            @SerializedName("created_by")
            val createdBy: Int,
            @SerializedName("description")
            val description: Any,
            @SerializedName("edit_info_permission")
            val editInfoPermission: String,
            @SerializedName("flag")
            val flag: String,
            @SerializedName("icon")
            val icon: String,
            @SerializedName("id")
            val id: Int,
            @SerializedName("name")
            val name: String,
            @SerializedName("other_user_id")
            val otherUserId: String,
            @SerializedName("pending_requests")
            val pendingRequests: Any,
            @SerializedName("send_msg")
            val sendMsg: String,
            @SerializedName("status")
            val status: String,
            @SerializedName("total_users")
            val totalUsers: String,
            @SerializedName("updated_at")
            val updatedAt: String,
            @SerializedName("user_id")
            val userId: Int
        )

        data class GroupUserWithSetting(
            @SerializedName("created_at")
            val createdAt: String,
            @SerializedName("end_mute_time")
            val endMuteTime: Any,
            @SerializedName("group_id")
            val groupId: Int,
            @SerializedName("id")
            val id: Int,
            @SerializedName("is_admin")
            val isAdmin: Int,
            @SerializedName("is_allow_to_add_post")
            val isAllowToAddPost: Int,
            @SerializedName("is_allow_to_edit_info")
            val isAllowToEditInfo: Int,
            @SerializedName("is_custom_notification_enable")
            val isCustomNotificationEnable: Int,
            @SerializedName("is_deleted")
            val isDeleted: Int,
            @SerializedName("is_media_viewable")
            val isMediaViewable: Int,
            @SerializedName("is_mute_notification")
            val isMuteNotification: Int,
            @SerializedName("is_popup_notification")
            val isPopupNotification: Int,
            @SerializedName("is_report")
            val isReport: Int,
            @SerializedName("label_color")
            val labelColor: String,
            @SerializedName("light")
            val light: String,
            @SerializedName("location")
            val location: Any,
            @SerializedName("mute_time")
            val muteTime: Any,
            @SerializedName("notification_tone_id")
            val notificationToneId: Int,
            @SerializedName("status")
            val status: String,
            @SerializedName("updated_at")
            val updatedAt: String,
            @SerializedName("use_high_priority_notification")
            val useHighPriorityNotification: Int,
            @SerializedName("user")
            val user: User,
            @SerializedName("user_id")
            val userId: Int,
            @SerializedName("vibrate_status")
            val vibrateStatus: String
        ) {
            data class User(
                @SerializedName("first_name")
                val firstName: String,
                @SerializedName("flag")
                val flag: String,
                @SerializedName("id")
                val id: Int,
                @SerializedName("last_name")
                val lastName: String,
                @SerializedName("profile_image")
                val profileImage: String
            )
        }
    }

    data class GetAllRequest(
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("friend")
        val friend: Friend,
        @SerializedName("friend_id")
        val friendId: Int,
        @SerializedName("id")
        val id: Int,
        @SerializedName("is_request")
        val isRequest: Int,
        @SerializedName("receiver_request_count")
        val receiverRequestCount: Int,
        @SerializedName("sender_request_count")
        val senderRequestCount: Int,
        @SerializedName("status")
        val status: String,
        @SerializedName("updated_at")
        val updatedAt: String,
        @SerializedName("user")
        val user: Any,
        @SerializedName("user_id")
        val userId: Int
    ) {
        data class Friend(
            @SerializedName("first_name")
            val firstName: String,
            @SerializedName("flag")
            val flag: String,
            @SerializedName("id")
            val id: Int,
            @SerializedName("last_name")
            val lastName: String,
            @SerializedName("profile_image")
            val profileImage: String
        )
    }

    data class HookData(
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("friend")
        val friend: Friend,
        @SerializedName("friend_id")
        val friendId: Int,
        @SerializedName("group_id")
        val groupId: Int,
        @SerializedName("hook_id")
        val hookId: Any,
        @SerializedName("id")
        val id: Int,
        @SerializedName("is_friend")
        val isFriend: Int,
        @SerializedName("is_group")
        val isGroup: Int,
        @SerializedName("lock_lbl_data")
        val lockLblData: Any,
        @SerializedName("updated_at")
        val updatedAt: String,
        @SerializedName("user_id")
        val userId: Int
    ) {
        data class Friend(
            @SerializedName("first_name")
            val firstName: String,
            @SerializedName("flag")
            val flag: String,
            @SerializedName("id")
            val id: Int,
            @SerializedName("last_name")
            val lastName: String,
            @SerializedName("profile_image")
            val profileImage: String
        )
    }

    data class PostSetting(
        @SerializedName("allow_others_to_share")
        val allowOthersToShare: String,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("id")
        val id: Int,
        @SerializedName("others_post_see_timeline")
        val othersPostSeeTimeline: String,
        @SerializedName("post_timeline")
        val postTimeline: String,
        @SerializedName("public_post_comments")
        val publicPostComments: String,
        @SerializedName("review_post_before_appear")
        val reviewPostBeforeAppear: String,
        @SerializedName("review_tags_before_appear")
        val reviewTagsBeforeAppear: String,
        @SerializedName("see_future_post")
        val seeFuturePost: String,
        @SerializedName("see_past_post")
        val seePastPost: String,
        @SerializedName("see_tagged_post_timeline")
        val seeTaggedPostTimeline: String,
        @SerializedName("updated_at")
        val updatedAt: String,
        @SerializedName("user_id")
        val userId: Int,
        @SerializedName("who_can_add_whene_tagged_but_not_seen_by_you")
        val whoCanAddWheneTaggedButNotSeenByYou: String,
        @SerializedName("who_can_followe_me")
        val whoCanFolloweMe: String
    )

    data class UserSetting(
        @SerializedName("about_seen")
        val aboutSeen: String,
        @SerializedName("call_rigtone")
        val callRigtone: String,
        @SerializedName("call_vibrate")
        val callVibrate: String,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("font_size")
        val fontSize: String,
        @SerializedName("gallery_image")
        val galleryImage: String,
        @SerializedName("group_is_popup_notification")
        val groupIsPopupNotification: Int,
        @SerializedName("group_notification_tone_id")
        val groupNotificationToneId: Int,
        @SerializedName("group_use_high_priority_notification")
        val groupUseHighPriorityNotification: Int,
        @SerializedName("group_vibrate_status")
        val groupVibrateStatus: String,
        @SerializedName("id")
        val id: Int,
        @SerializedName("is_audio_autodownload")
        val isAudioAutodownload: Int,
        @SerializedName("is_audio_autodownload_roaming")
        val isAudioAutodownloadRoaming: Int,
        @SerializedName("is_audio_autodownload_wifi")
        val isAudioAutodownloadWifi: Int,
        @SerializedName("is_chat_notification")
        val isChatNotification: Int,
        @SerializedName("is_color_wallpaper")
        val isColorWallpaper: Int,
        @SerializedName("is_default_wallpaper")
        val isDefaultWallpaper: Int,
        @SerializedName("is_delete_request")
        val isDeleteRequest: Int,
        @SerializedName("is_document_autodownload")
        val isDocumentAutodownload: Int,
        @SerializedName("is_document_autodownload_roaming")
        val isDocumentAutodownloadRoaming: Int,
        @SerializedName("is_document_autodownload_wifi")
        val isDocumentAutodownloadWifi: Int,
        @SerializedName("is_enter_send")
        val isEnterSend: Int,
        @SerializedName("is_fingerprint_lock_enabled")
        val isFingerprintLockEnabled: Int,
        @SerializedName("is_followers_viewable")
        val isFollowersViewable: Int,
        @SerializedName("is_friend_enable")
        val isFriendEnable: Int,
        @SerializedName("is_gallery_wallpaper")
        val isGalleryWallpaper: Int,
        @SerializedName("is_image_wallpaper")
        val isImageWallpaper: Int,
        @SerializedName("is_media_visible")
        val isMediaVisible: Int,
        @SerializedName("is_new_post_notify")
        val isNewPostNotify: Int,
        @SerializedName("is_no_wallpaper")
        val isNoWallpaper: Int,
        @SerializedName("is_photo_autodownload")
        val isPhotoAutodownload: Int,
        @SerializedName("is_photo_autodownload_roaming")
        val isPhotoAutodownloadRoaming: Int,
        @SerializedName("is_photo_autodownload_wifi")
        val isPhotoAutodownloadWifi: Int,
        @SerializedName("is_popup_notification")
        val isPopupNotification: Int,
        @SerializedName("is_show_security_notification")
        val isShowSecurityNotification: Int,
        @SerializedName("is_tag_notification")
        val isTagNotification: Int,
        @SerializedName("is_two_step_verification_enabled")
        val isTwoStepVerificationEnabled: Int,
        @SerializedName("is_video_autodownload")
        val isVideoAutodownload: Int,
        @SerializedName("is_video_autodownload_roaming")
        val isVideoAutodownloadRoaming: Int,
        @SerializedName("is_video_autodownload_wifi")
        val isVideoAutodownloadWifi: Int,
        @SerializedName("is_visible")
        val isVisible: Int,
        @SerializedName("languages")
        val languages: String,
        @SerializedName("last_seen")
        val lastSeen: String,
        @SerializedName("live_location_sharing")
        val liveLocationSharing: Int,
        @SerializedName("near_location")
        val nearLocation: String,
        @SerializedName("notification_tone_id")
        val notificationToneId: Int,
        @SerializedName("profile_image")
        val profileImage: Any,
        @SerializedName("profile_seen")
        val profileSeen: String,
        @SerializedName("sound")
        val sound: String,
        @SerializedName("story_download")
        val storyDownload: String,
        @SerializedName("story_view")
        val storyView: String,
        @SerializedName("theme_id")
        val themeId: Int,
        @SerializedName("updated_at")
        val updatedAt: String,
        @SerializedName("use_high_priority_notification")
        val useHighPriorityNotification: Int,
        @SerializedName("user_id")
        val userId: Int,
        @SerializedName("vibrate_status")
        val vibrateStatus: String,
        @SerializedName("wall_paper_color")
        val wallPaperColor: Any,
        @SerializedName("wallpaper_id")
        val wallpaperId: Int,
        @SerializedName("who_can_add_me_in_group")
        val whoCanAddMeInGroup: String
    )
}