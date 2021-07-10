package com.task.newapp.models


import com.google.gson.annotations.SerializedName

data class ResponseUserSetting(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("success")
    val success: Int
) {
    data class Data(
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
        val profileImage: String,
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
        val wallPaperColor: String,
        @SerializedName("wallpaper_id")
        val wallpaperId: Int,
        @SerializedName("who_can_add_me_in_group")
        val whoCanAddMeInGroup: String
    )
}