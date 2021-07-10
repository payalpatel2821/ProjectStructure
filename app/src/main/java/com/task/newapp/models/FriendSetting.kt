package com.task.newapp.models

import com.google.gson.annotations.SerializedName

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