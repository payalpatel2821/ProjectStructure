package com.task.newapp.models


import com.google.gson.annotations.SerializedName


data class UserSetting(
    @SerializedName("id")
    val id: Int,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("audio_auto_download")
    val audioAutoDownload: String,
    @SerializedName("background_color")
    val backgroundColor: String,
    @SerializedName("background_image")
    val backgroundImage: String,
    @SerializedName("background_type")
    val backgroundType: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("document_auto_download")
    val documentAutoDownload: String,
    @SerializedName("font_size")
    val fontSize: Int,
    @SerializedName("group_notification_tone_id")
    val groupNotificationToneId: String,
    @SerializedName("image_auto_download")
    val imageAutoDownload: String,
    @SerializedName("is_accept_follow_request_notify")
    val isAcceptFollowRequestNotify: Int,
    @SerializedName("is_allow_message_from_other")
    val isAllowMessageFromOther: Int,
    @SerializedName("is_chat_message_preview_notify")
    val isChatMessagePreviewNotify: Int,
    @SerializedName("is_chat_vibrate_status")
    val isChatVibrateStatus: Int,
    @SerializedName("is_follow_request_notify")
    val isFollowRequestNotify: Int,
    @SerializedName("is_group_message_preview_notify")
    val isGroupMessagePreviewNotify: Int,
    @SerializedName("is_group_vibrate_status")
    val isGroupVibrateStatus: Int,
    @SerializedName("is_in_app_preview")
    val isInAppPreview: Int,
    @SerializedName("is_in_app_sound")
    val isInAppSound: Int,
    @SerializedName("is_in_app_vibrate")
    val isInAppVibrate: Int,
    @SerializedName("is_pause_notification")
    val isPauseNotification: Int,
    @SerializedName("is_post_comment_notify")
    val isPostCommentNotify: Int,
    @SerializedName("is_post_like_notify")
    val isPostLikeNotify: Int,
    @SerializedName("is_profile_view_notify")
    val isProfileViewNotify: Int,
    @SerializedName("is_saved_edited_image")
    val isSavedEditedImage: Int,
    @SerializedName("is_show_chat_notify")
    val isShowChatNotify: Int,
    @SerializedName("is_show_group_notify")
    val isShowGroupNotify: Int,
    @SerializedName("is_story_sharing_as_message")
    val isStorySharingAsMessage: Int,
    @SerializedName("is_tagged_post_like_comment_notify")
    val isTaggedPostLikeCommentNotify: Int,
    @SerializedName("is_tagged_post_notify")
    val isTaggedPostNotify: Int,
    @SerializedName("notification_tone_id")
    val notificationToneId: String,
    @SerializedName("open_link_in")
    val openLinkIn: String,
    @SerializedName("story_download")
    val storyDownload: String,
    @SerializedName("story_reply")
    val storyReply: String,
    @SerializedName("story_view")
    val storyView: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("video_auto_download")
    val videoAutoDownload: String,
    @SerializedName("wallpaper_id")
    val wallpaperId: Int,
    @SerializedName("who_can_add_group")
    val whoCanAddGroup: String,
    @SerializedName("who_can_mention")
    val whoCanMention: String,
    @SerializedName("who_can_message_me")
    val whoCanMessageMe: String,
    @SerializedName("who_can_tag")
    val whoCanTag: String,
    @SerializedName("who_see_about")
    val whoSeeAbout: String,
    @SerializedName("who_see_last_seen")
    val whoSeeLastSeen: String,
    @SerializedName("who_see_profile_photo")
    val whoSeeProfilePhoto: String
)
