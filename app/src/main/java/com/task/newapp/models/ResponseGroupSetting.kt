package com.task.newapp.models


import com.google.gson.annotations.SerializedName
import com.task.newapp.models.chat.GetAllGroup.GroupUserWithSetting


data class ResponseGroupSetting(
    @SerializedName("data")
    var `data`: GroupUserWithSetting,
    @SerializedName("message")
    var message: String,
    @SerializedName("success")
    var success: Int
)// {
//    data class Data(
//        @SerializedName("created_at")
//        var createdAt: String,
//        @SerializedName("end_mute_time")
//        var endMuteTime: Any,
//        @SerializedName("group_id")
//        var groupId: Int,
//        @SerializedName("id")
//        var id: Int,
//        @SerializedName("is_admin")
//        var isAdmin: Int,
//        @SerializedName("is_allow_to_add_post")
//        var isAllowToAddPost: Int,
//        @SerializedName("is_allow_to_edit_info")
//        var isAllowToEditInfo: Int,
//        @SerializedName("is_custom_notification_enable")
//        var isCustomNotificationEnable: Int,
//        @SerializedName("is_deleted")
//        var isDeleted: Int,
//        @SerializedName("is_media_viewable")
//        var isMediaViewable: Int,
//        @SerializedName("is_mute_notification")
//        var isMuteNotification: Int,
//        @SerializedName("is_popup_notification")
//        var isPopupNotification: Int,
//        @SerializedName("is_report")
//        var isReport: Int,
//        @SerializedName("is_send")
//        var isSend: Int,
//        @SerializedName("label_color")
//        var labelColor: String,
//        @SerializedName("light")
//        var light: String,
//        @SerializedName("location")
//        var location: Any,
//        @SerializedName("mute_time")
//        var muteTime: Any,
//        @SerializedName("notification_tone_id")
//        var notificationToneId: Int,
//        @SerializedName("status")
//        var status: String,
//        @SerializedName("updated_at")
//        var updatedAt: String,
//        @SerializedName("use_high_priority_notification")
//        var useHighPriorityNotification: Int,
//        @SerializedName("user_id")
//        var userId: Int,
//        @SerializedName("vibrate_status")
//        var vibrateStatus: String
//    )
//}