package com.task.newapp.models


import com.google.gson.annotations.SerializedName

data class ResponseGetSetting(
    @SerializedName("message")
    var message: String,
    @SerializedName("success")
    var success: Int,
    @SerializedName("user_setting")
    var userSetting: UserSetting
){

    data class UserSetting(
        @SerializedName("audio_auto_download")
        var audioAutoDownload: String,
        @SerializedName("background_color")
        var backgroundColor: Any,
        @SerializedName("background_image")
        var backgroundImage: String,
        @SerializedName("background_type")
        var backgroundType: String,
        @SerializedName("created_at")
        var createdAt: String,
        @SerializedName("document_auto_download")
        var documentAutoDownload: String,
        @SerializedName("font_size")
        var fontSize: Int,
        @SerializedName("group_notification_tone_id")
        var groupNotificationToneId: String,
        @SerializedName("id")
        var id: Int,
        @SerializedName("image_auto_download")
        var imageAutoDownload: String,
        @SerializedName("is_accept_follow_request_notify")
        var isAcceptFollowRequestNotify: Int,
        @SerializedName("is_allow_messgae_forom_other")
        var isAllowMessgaeForomOther: Int,
        @SerializedName("is_chat_message_preview_notify")
        var isChatMessagePreviewNotify: Int,
        @SerializedName("is_chat_vibrate_status")
        var isChatVibrateStatus: Int,
        @SerializedName("is_follow_request_notify")
        var isFollowRequestNotify: Int,
        @SerializedName("is_group_message_preview_notify")
        var isGroupMessagePreviewNotify: Int,
        @SerializedName("is_group_vibrate_status")
        var isGroupVibrateStatus: Int,
        @SerializedName("is_pause_notification")
        var isPauseNotification: Int,
        @SerializedName("is_post_comment_notify")
        var isPostCommentNotify: Int,
        @SerializedName("is_post_like_notify")
        var isPostLikeNotify: Int,
        @SerializedName("is_profile_view_notify")
        var isProfileViewNotify: Int,
        @SerializedName("is_saved_edited_image")
        var isSavedEditedImage: Int,
        @SerializedName("is_show_chat_notify")
        var isShowChatNotify: Int,
        @SerializedName("is_show_group_notify")
        var isShowGroupNotify: Int,
        @SerializedName("is_story_sharing_as_message")
        var isStorySharingAsMessage: Int,
        @SerializedName("is_tagged_post_like_comment_notify")
        var isTaggedPostLikeCommentNotify: Int,
        @SerializedName("is_tagged_post_notify")
        var isTaggedPostNotify: Int,
        @SerializedName("notification_tone_id")
        var notificationToneId: String,
        @SerializedName("open_link_in")
        var openLinkIn: String,
        @SerializedName("story_download")
        var storyDownload: String,
        @SerializedName("story_reply")
        var storyReply: String,
        @SerializedName("story_view")
        var storyView: String,
        @SerializedName("updated_at")
        var updatedAt: String,
        @SerializedName("user_id")
        var userId: Int,
        @SerializedName("video_auto_download")
        var videoAutoDownload: String,
        @SerializedName("wallpaper_id")
        var wallpaperId: Any,
        @SerializedName("who_can_mention")
        var whoCanMention: String,
        @SerializedName("who_can_message_me")
        var whoCanMessageMe: Any,
        @SerializedName("who_can_tag")
        var whoCanTag: String
    )

}