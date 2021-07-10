// Please note : @LinkingObjects and default values are not represented in the schema and thus will not be part of the generated models
package com.task.newapp.realmDB.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class FriendSettings : RealmObject() {
    companion object {
        const val PROPERTY_id = "id"
        const val PROPERTY_user_id = "user_id"
        const val PROPERTY_friend_id = "friend_id"
        const val PROPERTY_notification_tone_id = "notification_tone_id"
        const val PROPERTY_mute_notification = "mute_notification"
        const val PROPERTY_is_custom_notification_enable = "is_custom_notification_enable"
        const val PROPERTY_vibrate_status = "vibrate_status"
        const val PROPERTY_is_popup_notification = "is_popup_notification"
        const val PROPERTY_use_high_priority_notification = "use_high_priority_notification"
        const val PROPERTY_call_ringtone = "call_ringtone"
        const val PROPERTY_call_vibrate = "call_vibrate"
        const val PROPERTY_sound = "sound"
        const val PROPERTY_user = "user"
    }

    @PrimaryKey
    var id: Int = 0
    var user_id: Int = 0
    var friend_id: Int = 0
    var notification_tone_id: Int = 0
    var mute_notification: Int = 0
    var is_custom_notification_enable: Int = 0
    var vibrate_status: String = ""
    var is_popup_notification: Int = 0
    var use_high_priority_notification: Int = 0
    var call_ringtone: String = ""
    var call_vibrate: String = ""
    var sound: String = ""
    var user: Users? = null

}
