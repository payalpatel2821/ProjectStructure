// Please note : @LinkingObjects and default values are not represented in the schema and thus will not be part of the generated models
package com.task.newapp.realmDB.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class UserSettings : RealmObject() {
    @PrimaryKey
    var id: Int = 0
    var userId: Int = 0
    var audioAutoDownload: String = ""
    var backgroundColor: String? = ""
    var backgroundImage: String = ""
    var backgroundType: String = ""
    var documentAutoDownload: String = ""
    var fontSize: Int = 0
    var groupNotificationToneId: String = ""
    var imageAutoDownload: String? = ""
    var isAcceptFollowRequestNotify: Int = 0
    var isAllowMessageFromOther: Int = 0
    var isChatMessagePreviewNotify: Int = 0
    var isChatVibrateStatus: Int = 0
    var isFollowRequestNotify: Int = 0
    var isGroupMessagePreviewNotify: Int = 0
    var isGroupVibrateStatus: Int = 0
    var isInAppPreview: Int = 0
    var isInAppSound: Int = 0
    var isInAppVibrate: Int = 0
    var isPauseNotification: Int = 0
    var isPostCommentNotify:  Int = 0
    var isPostLikeNotify: Int = 0
    var isProfileViewNotify: Int = 0
    var isSavedEditedImage: Int = 0
    var isShowChatNotify: Int = 0
    var isShowGroupNotify: Int = 0
    var isStorySharingAsMessage: Int = 0
    var isTaggedPostLikeCommentNotify: Int = 0
    var isTaggedPostNotify: Int = 0
    var notificationToneId: String = ""
    var openLinkIn: String = ""
    var storyDownload:String = ""
    var storyReply: String = ""
    var storyView: String = ""
    var updatedAt: String = ""
    var createdAt: String = ""
    var videoAutoDownload: String = ""
    var wallpaperId: Int? = 0
    var whoCanAddGroup: String = ""
    var whoCanMention: String = ""
    var whoCanMessageMe: String = ""
    var whoCanTag: String = ""
    var whoSeeAbout: String = ""
    var whoSeeLastSeen: String = ""
    var whoSeeProfilePhoto: String = ""


    companion object {
        fun create(
             id: Int , userId: Int, audioAutoDownload: String , backgroundColor: String, backgroundImage: String, backgroundType: String , documentAutoDownload: String ,
             fontSize: Int , groupNotificationToneId: String , imageAutoDownload: String, isAcceptFollowRequestNotify: Int, isAllowMessageFromOther: Int, isChatMessagePreviewNotify: Int,
             isChatVibrateStatus: Int , isFollowRequestNotify: Int , isGroupMessagePreviewNotify: Int , isGroupVibrateStatus: Int , isInAppPreview: Int , isInAppSound: Int ,
             isInAppVibrate: Int , isPauseNotification: Int , isPostCommentNotify:  Int , isPostLikeNotify: Int , isProfileViewNotify: Int , isSavedEditedImage: Int ,
             isShowChatNotify: Int , isShowGroupNotify: Int , isStorySharingAsMessage: Int , isTaggedPostLikeCommentNotify: Int , isTaggedPostNotify: Int , notificationToneId: String ,
             openLinkIn: String , storyDownload:String , storyReply: String , storyView: String , updatedAt: String , createdAt: String , videoAutoDownload: String , wallpaperId: Int,
             whoCanAddGroup: String , whoCanMention: String , whoCanMessageMe: String , whoCanTag: String , whoSeeAbout: String , whoSeeLastSeen: String , whoSeeProfilePhoto: String
        ): UserSettings {
            val userSettings = UserSettings()
            userSettings.id = id
            userSettings.userId = userId
            userSettings.wallpaperId = wallpaperId
            userSettings.audioAutoDownload = audioAutoDownload
            userSettings.backgroundColor = backgroundColor
            userSettings.backgroundImage = backgroundImage
            userSettings.backgroundType = backgroundType
            userSettings.documentAutoDownload = documentAutoDownload
            userSettings.notificationToneId = notificationToneId
            userSettings.fontSize = fontSize
            userSettings.groupNotificationToneId = groupNotificationToneId
            userSettings.imageAutoDownload = imageAutoDownload
            userSettings.isAcceptFollowRequestNotify = isAcceptFollowRequestNotify
            userSettings.isAllowMessageFromOther = isAllowMessageFromOther
            userSettings.isChatMessagePreviewNotify = isChatMessagePreviewNotify
            userSettings.isChatVibrateStatus = isChatVibrateStatus
            userSettings.isFollowRequestNotify = isFollowRequestNotify
            userSettings.isGroupMessagePreviewNotify = isGroupMessagePreviewNotify
            userSettings.isGroupVibrateStatus = isGroupVibrateStatus
            userSettings.isInAppPreview = isInAppPreview
            userSettings.isInAppSound = isInAppSound
            userSettings.isInAppVibrate = isInAppVibrate
            userSettings.isPauseNotification = isPauseNotification
            userSettings.isPostCommentNotify = isPostCommentNotify
            userSettings.isPostLikeNotify = isPostLikeNotify
            userSettings.isProfileViewNotify = isProfileViewNotify
            userSettings.isSavedEditedImage = isSavedEditedImage
            userSettings.isShowChatNotify = isShowChatNotify
            userSettings.isShowGroupNotify = isShowGroupNotify
            userSettings.isStorySharingAsMessage = isStorySharingAsMessage
            userSettings.isTaggedPostLikeCommentNotify = isTaggedPostLikeCommentNotify
            userSettings.isTaggedPostNotify = isTaggedPostNotify
            userSettings.notificationToneId = notificationToneId
            userSettings.openLinkIn = openLinkIn
            userSettings.storyDownload = storyDownload
            userSettings.storyView = storyView
            userSettings.storyDownload = storyDownload
            userSettings.storyReply = storyReply
            userSettings.updatedAt = updatedAt
            userSettings.createdAt = createdAt
            userSettings.videoAutoDownload = videoAutoDownload
            userSettings.whoCanAddGroup = whoCanAddGroup
            userSettings.whoCanMention = whoCanMention
            userSettings.whoCanMessageMe = whoCanMessageMe
            userSettings.whoCanTag = whoCanTag
            userSettings.whoSeeAbout = whoSeeAbout
            userSettings.whoSeeLastSeen = whoSeeLastSeen
            userSettings.whoSeeProfilePhoto = whoSeeProfilePhoto
         
            return userSettings
        }
    }
}
