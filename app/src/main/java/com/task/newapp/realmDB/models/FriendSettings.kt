// Please note : @LinkingObjects and default values are not represented in the schema and thus will not be part of the generated models
package com.task.newapp.realmDB.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class FriendSettings : RealmObject() {
    @PrimaryKey
    var id: Int = 0
    var userId: Int = 0
    var friendId: Int = 0
    var notificationToneId: Int = 0
    var muteNotification: Int = 0
    var isCustomNotificationEnable: Int = 0
    var vibrateStatus: String = ""
    var isPopupNotification: Int = 0
    var useHighPriorityNotification: Int = 0
    var callRingtone: String = ""
    var callVibrate: String = ""
    var sound: String = ""
    var user: Users? = null

    companion object {
        fun create(
            id: Int, userId: Int, friendId: Int, notificationToneId: Int, muteNotification: Int, isCustomNotificationEnable: Int, vibrateStatus: String,
            isPopupNotification: Int, useHighPriorityNotification: Int, callRingtone: String, callVibrate: String, sound: String, user: Users
        ): FriendSettings {
            val friendSettings = FriendSettings()
            friendSettings.id = id
            friendSettings.userId = userId
            friendSettings.friendId = friendId
            friendSettings.notificationToneId = notificationToneId
            friendSettings.muteNotification = muteNotification
            friendSettings.isCustomNotificationEnable = isCustomNotificationEnable
            friendSettings.vibrateStatus = vibrateStatus
            friendSettings.isPopupNotification = isPopupNotification
            friendSettings.useHighPriorityNotification = useHighPriorityNotification
            friendSettings.callRingtone = callRingtone
            friendSettings.callVibrate = callVibrate
            friendSettings.sound = sound
            friendSettings.user = user
            return friendSettings

        }
    }
}
