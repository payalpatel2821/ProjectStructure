package com.task.newapp.models

import com.google.gson.annotations.SerializedName

data class FriendSetting(
    @SerializedName("id")
    val id: Int,
    @SerializedName("friend_id")
    val friendId: Int,
    @SerializedName("is_custom_notification_enable")
    val isCustomNotificationEnable: Int,
    @SerializedName("vibrate_status")
    val vibrateStatus: String,
    @SerializedName("notification_tone_id")
    val notificationToneId: Int,
    @SerializedName("mute_notification")
    val muteNotification: Int,
    @SerializedName("tone_name")
    val toneName: String,
    @SerializedName("friend")
    val friend: OtherUserModel
    /* @SerializedName("is_popup_notification")
     val isPopupNotification: Int,
     @SerializedName("use_high_priority_notification")
     val useHighPriorityNotification: Int,
     @SerializedName("call_rigtone")
     val callRingtone: String,
     @SerializedName("call_vibrate")
     val callVibrate: String,
     @SerializedName("user_id")
     val userId: Int,*/
)
