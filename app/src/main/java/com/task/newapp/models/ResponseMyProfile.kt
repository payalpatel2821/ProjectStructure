package com.task.newapp.models

import com.google.gson.annotations.SerializedName

data class ResponseMyProfile(

    @SerializedName("data")
    val `data`: MyProfileData,
    @SerializedName("message")
    val message: String,
    @SerializedName("success")
    val success: Int
) {

    data class MyProfileData(
        @SerializedName("id")
        val id: Int,
        @SerializedName("account_id")
        val accountId: String,
        @SerializedName("u_name")
        val uName: String,
        @SerializedName("first_name")
        val firstName: String,
        @SerializedName("last_name")
        val lastName: String,
        @SerializedName("about")
        val about: String?,
        @SerializedName("email")
        val email: String,
        @SerializedName("mobile")
        val mobile: String,
        @SerializedName("register_type")
        val registerType: String,
        @SerializedName("age")
        val age: String,
        @SerializedName("anniversary")
        val anniversary: Any,
        @SerializedName("app_version")
        val appVersion: String,
        @SerializedName("blood_gp")
        val bloodGp: String,
        @SerializedName("count_common_groups")
        val countCommonGroups: Int,
        @SerializedName("count_post")
        val countPost: Int,
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
        @SerializedName("expired_at")
        val expiredAt: String,
        @SerializedName("flag")
        val flag: String,
        @SerializedName("followers")
        val followers: String,
        @SerializedName("following")
        val following: String,
        @SerializedName("forgot_token")
        val forgotToken: String,
        @SerializedName("gender")
        val gender: String,
        @SerializedName("is_block")
        var isBlock: Int,
        @SerializedName("is_blocked_by_admin")
        val isBlockedByAdmin: Int,
        @SerializedName("is_deleted")
        val isDeleted: Int,
        @SerializedName("is_follow")
        val isFollow: Boolean,
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
        @SerializedName("last_seen_time")
        val lastSeenTime: String,
        @SerializedName("latitude")
        val latitude: String,
        @SerializedName("location")
        val location: Any,
        @SerializedName("longitude")
        val longitude: String,
        @SerializedName("profession")
        val profession: Any,
        @SerializedName("profile_image")
        val profileImage: String,
        @SerializedName("profile_color")
        val profileColor: String,
        @SerializedName("profile_views")
        val profileViews: Int,
        @SerializedName("request_date")
        val requestDate: String,
        @SerializedName("secret_password")
        val secretPassword: String,
        @SerializedName("status")
        val status: String,
        @SerializedName("updated_at")
        val updatedAt: String,
        @SerializedName("friendsetting")
        val friendSetting: FriendSetting,
        @SerializedName("is_visible")
        val isVisible: Int,
        @SerializedName("is_can_message_me")
        val isCanMessageMe: Boolean,
        @SerializedName("is_show_last_seen")
        val isShowLastSeen: Boolean,
        @SerializedName("is_see_about")
        val isSeeAbout: Boolean,
        @SerializedName("is_see_profile_photo")
        val isSeeProfilePhoto: Boolean,
        @SerializedName("is_block_by_user")
        val isBlockByUser: Int,
        @SerializedName("follow_request")
        val followRequest: String
    ) {
        data class FriendSetting(
            @SerializedName("is_custom_notification_enable")
            var isCustomNotificationEnable: Int,
            @SerializedName("vibrate_status")
            var vibrateStatus: String,
            @SerializedName("notification_tone_id")
            var notificationToneId: Int,
            @SerializedName("mute_notification")
            var muteNotification: Int,
            @SerializedName("tone_name")
            val toneName: String
        )
    }
}