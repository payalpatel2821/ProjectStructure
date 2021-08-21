// Please note : @LinkingObjects and default values are not represented in the schema and thus will not be part of the generated models
package com.task.newapp.realmDB.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class GroupUser : RealmObject() {
    @PrimaryKey
    var id: Int = 0
    var userId: Int = 0
    var groupId: Int = 0
    var labelColor: String? = null
    var location: String? = null
    var isAdmin: Int = 0
    var isAllowToAddPost: Int = 0
    var isMuteNotification: Int = 0
    var isReport: Int = 0
    var status: String? = null
    var isDeleted: Int = 0
    var isAllowToEditInfo: Int = 0
    var muteTime: String? = null
    var endMuteTime: String? = null
    var mediaViewable: Int = 0
    var customNotificationEnable: Int = 0
    var vibrateStatus: String? = null
    var isPopupNotification: Int = 0
    var light: String? = null
    var useHighPriorityNotification: Int = 0
    var notificationToneId: Int = 0
    var createdAt: String? = null
    var updatedAt: String? = null

    companion object {
        fun create(
            id: Int, userId: Int, groupId: Int, labelColor: String, location: String, isAdmin: Int, isAllowToAddPost: Int, isMuteNotification: Int, isReport: Int,
            status: String, isDeleted: Int, isAllowToEditInfo: Int, muteTime: String, endMuteTime: String, mediaViewable: Int, customNotificationEnable: Int, vibrateStatus: String,
            isPopupNotification: Int, light: String, useHighPriorityNotification: Int, notificationToneId: Int, createdAt: String, updatedAt: String,
        ): GroupUser {
            val groupUser = GroupUser()
            groupUser.id = id
            groupUser.userId = userId
            groupUser.groupId = groupId
            groupUser.labelColor = labelColor
            groupUser.location = location
            groupUser.isAdmin = isAdmin
            groupUser.isAllowToAddPost = isAllowToAddPost
            groupUser.isMuteNotification = isMuteNotification
            groupUser.isReport = isReport
            groupUser.status = status
            groupUser.isDeleted = isDeleted
            groupUser.isAllowToEditInfo = isAllowToEditInfo
            groupUser.muteTime = muteTime
            groupUser.endMuteTime = endMuteTime
            groupUser.mediaViewable = mediaViewable
            groupUser.customNotificationEnable = customNotificationEnable
            groupUser.vibrateStatus = vibrateStatus
            groupUser.isPopupNotification = isPopupNotification
            groupUser.light = light
            groupUser.useHighPriorityNotification = useHighPriorityNotification
            groupUser.notificationToneId = notificationToneId
            groupUser.createdAt = createdAt
            groupUser.updatedAt = updatedAt

            return groupUser

        }
    }
}
