package com.task.newapp.models.chat

import com.google.gson.annotations.SerializedName
import com.task.newapp.models.OtherUserModel

data class GetAllGroup(
    @SerializedName("group_data")
    val groupData: GroupData,
    @SerializedName("group_user_with_settings")
    val groupUserWithSettings: List<GroupUserWithSetting>,
    @SerializedName("create_group_lbl")
    val createGroupLbl: ChatModel?,
    @SerializedName("add_user_in_gp")
    val addUserInGp: ChatModel?
) {


    data class GroupData(
        @SerializedName("id")
        val id: Int,
        @SerializedName("user_id")
        val userId: Int,
        @SerializedName("name")
        val name: String,
        @SerializedName("description")
        val description: String,
        @SerializedName("status")
        val status: String,
        @SerializedName("icon")
        val icon: String,
        @SerializedName("pending_requests")
        val pendingRequests: Any,
        @SerializedName("total_users")
        val totalUsers: Int,
        @SerializedName("other_user_id")
        val otherUserId: String,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("updated_at")
        val updatedAt: String,
        @SerializedName("edit_info_permission")
        val editInfoPermission: String,
        @SerializedName("send_msg")
        val sendMsg: String,
        @SerializedName("created_by")
        val createdBy: Int,
        @SerializedName("flag")
        val flag: String
    )

    data class GroupUserWithSetting(
        @SerializedName("id")
        val id: Int,
        @SerializedName("group_id")
        val groupId: Int,
        @SerializedName("user_id")
        val userId: Int,
        @SerializedName("label_color")
        val labelColor: String,
        @SerializedName("location")
        val location: String,
        @SerializedName("is_admin")
        val isAdmin: Int,
        @SerializedName("is_allow_to_add_post")
        val isAllowToAddPost: Int,
        @SerializedName("is_mute_notification")
        val isMuteNotification: Int,
        @SerializedName("is_report")
        val isReport: Int,
        @SerializedName("status")
        val status: String,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("updated_at")
        val updatedAt: String,
        @SerializedName("is_deleted")
        val isDeleted: Int,
        @SerializedName("is_allow_to_edit_info")
        val isAllowToEditInfo: Int,
        @SerializedName("mute_time")
        val muteTime: String,
        @SerializedName("end_mute_time")
        val endMuteTime: String,
        @SerializedName("is_media_viewable")
        val isMediaViewable: Int,
        @SerializedName("is_custom_notification_enable")
        val isCustomNotificationEnable: Int,
        @SerializedName("vibrate_status")
        val vibrateStatus: String,
        @SerializedName("is_popup_notification")
        val isPopupNotification: Int,
        @SerializedName("light")
        val light: String,
        @SerializedName("use_high_priority_notification")
        val useHighPriorityNotification: Int,
        @SerializedName("notification_tone_id")
        val notificationToneId: Int,
        @SerializedName("tone_name")
        val toneName: String,
        @SerializedName("is_send")
        val isSend: Int,
        @SerializedName("user")
        val user: OtherUserModel
    )
}