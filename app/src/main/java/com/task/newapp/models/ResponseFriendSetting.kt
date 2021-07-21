package com.task.newapp.models


import com.google.gson.annotations.SerializedName

data class ResponseFriendSetting(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("success")
    val success: Int
) {
    data class Data(
        @SerializedName("anniversary")
        val anniversary: Any,
        @SerializedName("birth_date")
        val birthDate: Any,
        @SerializedName("call_rigtone")
        val callRigtone: String,
        @SerializedName("call_vibrate")
        val callVibrate: String,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("end_mute_time")
        val endMuteTime: Any,
        @SerializedName("friend_id")
        val friendId: Int,
        @SerializedName("id")
        val id: Int,
        @SerializedName("is_anniversary_remember")
        val isAnniversaryRemember: Int,
        @SerializedName("is_birthday_remember")
        val isBirthdayRemember: Int,
        @SerializedName("is_custom_notification_enable")
        var isCustomNotificationEnable: Int,
        @SerializedName("is_hook")
        val isHook: Int,
        @SerializedName("is_media_viewable")
        val isMediaViewable: Int,
        @SerializedName("is_popup_notification")
        val isPopupNotification: Int,
        @SerializedName("light")
        val light: String,
        @SerializedName("mute_notification")
        var muteNotification: Int,
        @SerializedName("mute_time")
        val muteTime: Any,
        @SerializedName("nick_name")
        val nickName: Any,
        @SerializedName("notification_tone_id")
        var notificationToneId: Int,
        @SerializedName("relationship")
        val relationship: String,
        @SerializedName("sound")
        val sound: String,
        @SerializedName("status")
        val status: String,
        @SerializedName("updated_at")
        val updatedAt: String,
        @SerializedName("use_high_priority_notification")
        val useHighPriorityNotification: Int,
        @SerializedName("user_id")
        val userId: Int,
        @SerializedName("vibrate_status")
        var vibrateStatus: String
    )
}