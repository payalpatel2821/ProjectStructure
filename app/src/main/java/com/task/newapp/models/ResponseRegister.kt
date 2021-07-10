package com.task.newapp.models


import com.google.gson.annotations.SerializedName

data class ResponseRegister(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("emergency_contacts")
    val emergencyContacts: List<Any>,
    @SerializedName("message")
    val message: String,
    @SerializedName("post_setting")
    val postSetting: PostSetting,
    @SerializedName("success")
    val success: Int,
    @SerializedName("user_setting")
    val userSetting: UserSetting
) {
    data class Data(
        @SerializedName("token")
        val token: String,
        @SerializedName("user")
        val user: User
    )

  /*  data class PostSetting(
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
        @SerializedName("call_rigtone")
        val callRigtone: String,
        @SerializedName("call_vibrate")
        val callVibrate: String,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("font_size")
        val fontSize: String,
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
        @SerializedName("is_followers_viewable")
        val isFollowersViewable: Int,
        @SerializedName("is_friend_enable")
        val isFriendEnable: Int,
        @SerializedName("is_image_wallpaper")
        val isImageWallpaper: Int,
        @SerializedName("is_media_visible")
        val isMediaVisible: Int,
        @SerializedName("is_new_post_notify")
        val isNewPostNotify: Int,
        @SerializedName("is_photo_autodownload")
        val isPhotoAutodownload: Int,
        @SerializedName("is_photo_autodownload_roaming")
        val isPhotoAutodownloadRoaming: Int,
        @SerializedName("is_photo_autodownload_wifi")
        val isPhotoAutodownloadWifi: Int,
        @SerializedName("is_popup_notification")
        val isPopupNotification: Int,
        @SerializedName("is_tag_notification")
        val isTagNotification: Int,
        @SerializedName("is_video_autodownload")
        val isVideoAutodownload: Int,
        @SerializedName("is_video_autodownload_wifi")
        val isVideoAutodownloadWifi: Int,
        @SerializedName("languages")
        val languages: String,
        @SerializedName("last_seen")
        val lastSeen: String,
        @SerializedName("notification_tone_id")
        val notificationToneId: Int,
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
        val vibrateStatus: String
    )*/
}