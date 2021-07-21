// Please note : @LinkingObjects and default values are not represented in the schema and thus will not be part of the generated models
package com.task.newapp.realmDB.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey


open class NotificationTone : RealmObject() {
companion object{
    const val PROPERTY_id = "id"
    const val PROPERTY_display_name = "display_name"
    const val PROPERTY_sound_name = "sound_name"
    const val PROPERTY_is_set = "is_set"
    const val PROPERTY_notification_url = "notification_url"
}
    @PrimaryKey
    var id: Int = 0
    var display_name: String = ""
    var sound_name: String = ""
    var is_set: Boolean = false
    var notification_url: String = ""

}
