// Please note : @LinkingObjects and default values are not represented in the schema and thus will not be part of the generated models
package com.task.newapp.realmDB.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class UserSettings : RealmObject() {
    @PrimaryKey
    var id: Int = 0
    var userId: Int = 0
    var themeId: Int = 0
    var wallpaperId: Int = 0
    var isChatNotification: Int = 0
    var isNewPostNotify: Int = 0
    var isTagNotification: Int = 0
    var profileImage: String? = ""
    var profileColor: String? = ""
    var notificationToneId: Int = 0
    var isFollowersViewable: Int = 0
    var isFriendEnable: Int = 0
    var isImageWallpaper: Int = 0
    var isColorWallpaper: Int = 0
    var wallpaperColor: String? = ""
    var isDefaultWallpaper: Int = 0
    var isNoWallpaper: Int = 0
    var isGalleryWallpaper: Int = 0
    var galleryImage: String = ""
    var fontSize: String = ""
    var languages: String = ""
    var isEnterSend: Int = 0
    var isMediaVisible: Int = 0
    var isPhotoAutoDownload: Int = 0
    var isAudioAutoDownload: Int = 0
    var isVideoAutoDownload: Int = 0
    var isDocumentAutoDownload: Int = 0
    var isPhotoAutoDownloadWifi: Int = 0
    var isAudioAutoDownloadWifi: Int = 0
    var isVideoAutoDownloadWifi: Int = 0
    var isDocumentAutoDownloadWifi: Int = 0
    var isPhotoAutoDownloadRoaming: Int = 0
    var isAudioAutoDownloadRoaming: Int = 0
    var isVideoAutoDownloadRoaming: Int = 0
    var isDocumentAutoDownloadRoaming: Int = 0
    var isDeleteRequest: Int = 0
    var storyView: String = ""
    var storyDownload: String = ""
    var vibrateStatus: String = ""
    var isPopupNotification: Int = 0
    var useHighPriorityNotification: Int = 0
    var lastSeen: String = ""
    var isVisible: Int = 0
    var profileSeen: String = ""
    var callRingtone: String = ""
    var callVibrate: String = ""
    var aboutSeen: String = ""
    var whoCanAddMeInGroup: String = ""
    var liveLocationSharing: Int = 0
    var isFingerprintLockEnabled: Int = 0
    var isShowSecurityNotification: Int = 0
    var isTwoStepVerificationEnabled: Int = 0
    var groupVibrateStatus: String = ""
    var groupIsPopupNotification: Int = 0
    var groupUseHighPriorityNotification: Int = 0
    var groupNotificationToneId: Int = 0
    var nearLocation: Int = 0
    var toneName: String = ""
    var user: Users? = null
    var createdAt: String = ""
    var updatedAt: String = ""

    companion object {
        fun create(
            id: Int, userId: Int, themeId: Int, wallpaperId: Int, isChatNotification: Int, isNewPostNotify: Int, isTagNotification: Int, profileImage: String,
            profileColor: String, notificationToneId: Int, isFollowersViewable: Int, isFriendEnable: Int, isImageWallpaper: Int, isColorWallpaper: Int,
            wallpaperColor: String, isDefaultWallpaper: Int, isNoWallpaper: Int, isGalleryWallpaper: Int, galleryImage: String, fontSize: String, languages: String,
            isEnterSend: Int, isMediaVisible: Int, isPhotoAutoDownload: Int, isAudioAutoDownload: Int, isVideoAutoDownload: Int, isDocumentAutoDownload: Int,
            isPhotoAutoDownloadWifi: Int, isAudioAutoDownloadWifi: Int, isVideoAutoDownloadWifi: Int, isDocumentAutoDownloadWifi: Int, isPhotoAutoDownloadRoaming: Int,
            isAudioAutoDownloadRoaming: Int, isVideoAutoDownloadRoaming: Int, isDocumentAutoDownloadRoaming: Int, isDeleteRequest: Int, storyView: String, storyDownload: String,
            vibrateStatus: String, isPopupNotification: Int, useHighPriorityNotification: Int, lastSeen: String, isVisible: Int, profileSeen: String, callRingtone: String,
            callVibrate: String, aboutSeen: String, whoCanAddMeInGroup: String, liveLocationSharing: Int, isFingerprintLockEnabled: Int, isShowSecurityNotification: Int,
            isTwoStepVerificationEnabled: Int, groupVibrateStatus: String, groupIsPopupNotification: Int, groupUseHighPriorityNotification: Int, groupNotificationToneId: Int,
            nearLocation: Int, toneName: String, user: Users, createdAt: String, updatedAt: String,
        ): UserSettings {
            val userSettings = UserSettings()
            userSettings.id = id
            userSettings.userId = userId
            userSettings.themeId = themeId
            userSettings.wallpaperId = wallpaperId
            userSettings.isChatNotification = isChatNotification
            userSettings.isNewPostNotify = isNewPostNotify
            userSettings.isTagNotification = isTagNotification
            userSettings.profileImage = profileImage
            userSettings.profileColor = profileColor
            userSettings.notificationToneId = notificationToneId
            userSettings.isFollowersViewable = isFollowersViewable
            userSettings.isFriendEnable = isFriendEnable
            userSettings.isImageWallpaper = isImageWallpaper
            userSettings.isColorWallpaper = isColorWallpaper
            userSettings.wallpaperColor = wallpaperColor
            userSettings.isDefaultWallpaper = isDefaultWallpaper
            userSettings.isNoWallpaper = isNoWallpaper
            userSettings.isGalleryWallpaper = isGalleryWallpaper
            userSettings.galleryImage = galleryImage
            userSettings.fontSize = fontSize
            userSettings.languages = languages
            userSettings.isEnterSend = isEnterSend
            userSettings.isMediaVisible = isMediaVisible
            userSettings.isPhotoAutoDownload = isPhotoAutoDownload
            userSettings.isAudioAutoDownload = isAudioAutoDownload
            userSettings.isVideoAutoDownload = isVideoAutoDownload
            userSettings.isDocumentAutoDownload = isDocumentAutoDownload
            userSettings.isPhotoAutoDownloadWifi = isPhotoAutoDownloadWifi
            userSettings.isAudioAutoDownloadWifi = isAudioAutoDownloadWifi
            userSettings.isVideoAutoDownloadWifi = isVideoAutoDownloadWifi
            userSettings.isDocumentAutoDownloadWifi = isDocumentAutoDownloadWifi
            userSettings.isPhotoAutoDownloadRoaming = isPhotoAutoDownloadRoaming
            userSettings.isAudioAutoDownloadRoaming = isAudioAutoDownloadRoaming
            userSettings.isVideoAutoDownloadRoaming = isVideoAutoDownloadRoaming
            userSettings.isDocumentAutoDownloadRoaming = isDocumentAutoDownloadRoaming
            userSettings.isDeleteRequest = isDeleteRequest
            userSettings.storyView = storyView
            userSettings.storyDownload = storyDownload
            userSettings.vibrateStatus = vibrateStatus
            userSettings.isPopupNotification = isPopupNotification
            userSettings.useHighPriorityNotification = useHighPriorityNotification
            userSettings.lastSeen = lastSeen
            userSettings.isVisible = isVisible
            userSettings.profileSeen = profileSeen
            userSettings.callRingtone = callRingtone
            userSettings.callVibrate = callVibrate
            userSettings.aboutSeen = aboutSeen
            userSettings.whoCanAddMeInGroup = whoCanAddMeInGroup
            userSettings.liveLocationSharing = liveLocationSharing
            userSettings.isFingerprintLockEnabled = isFingerprintLockEnabled
            userSettings.isShowSecurityNotification = isShowSecurityNotification
            userSettings.isTwoStepVerificationEnabled = isTwoStepVerificationEnabled
            userSettings.groupVibrateStatus = groupVibrateStatus
            userSettings.groupIsPopupNotification = groupIsPopupNotification
            userSettings.groupUseHighPriorityNotification = groupUseHighPriorityNotification
            userSettings.groupNotificationToneId = groupNotificationToneId
            userSettings.nearLocation = nearLocation
            userSettings.toneName = toneName
            userSettings.user = user
            userSettings.createdAt = createdAt
            userSettings.updatedAt = updatedAt
            return userSettings
        }
    }
}
