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
    val blockUser: List<OtherUserModel>,
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
        val archiver: OtherUserModel,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("friend_id")
        val friendId: Int,
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
        val chat: ChatLabel,
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
        val totalUsers: Int,
        @SerializedName("updated_at")
        val updatedAt: String,
        @SerializedName("user_id")
        val userId: Int
    )

    data class Data(
        @SerializedName("token")
        val token: String,
        @SerializedName("user")
        val user: User
    )

    data class GetAllGroup(
        @SerializedName("add_user_in_gp")
        val addUserInGp: ChatLabel?,
        @SerializedName("create_group_lbl")
        val createGroupLbl: ChatLabel?,
        @SerializedName("group_data")
        val groupData: GroupData,
        @SerializedName("group_user_with_settings")
        val groupUserWithSettings: List<GroupUserWithSetting>
    ) {


        data class GroupData(
            @SerializedName("created_at")
            val createdAt: String,
            @SerializedName("created_by")
            val createdBy: Int,
            @SerializedName("description")
            val description: String,
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
            val totalUsers: Int,
            @SerializedName("updated_at")
            val updatedAt: String,
            @SerializedName("user_id")
            val userId: Int
        )

        data class GroupUserWithSetting(
            @SerializedName("created_at")
            val createdAt: String,
            @SerializedName("end_mute_time")
            val endMuteTime: String,
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
            val location: String,
            @SerializedName("mute_time")
            val muteTime: String,
            @SerializedName("notification_tone_id")
            val notificationToneId: Int,
            @SerializedName("status")
            val status: String,
            @SerializedName("updated_at")
            val updatedAt: String,
            @SerializedName("use_high_priority_notification")
            val useHighPriorityNotification: Int,
            @SerializedName("user")
            val user: OtherUserModel,
            @SerializedName("user_id")
            val userId: Int,
            @SerializedName("vibrate_status")
            val vibrateStatus: String
        )
    }

    data class GetAllRequest(
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("friend")
        val friend: OtherUserModel,
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
        val user: OtherUserModel,
        @SerializedName("user_id")
        val userId: Int
    )

    data class HookData(
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("friend")
        val friend: OtherUserModel?,
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
    )
}