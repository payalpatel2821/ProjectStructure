// Please note : @LinkingObjects and default values are not represented in the schema and thus will not be part of the generated models
package com.task.newapp.realmDB.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey


open class NotificationTone : RealmObject() {
    @PrimaryKey
    var id: Int = 0
    var displayName: String = ""
    //var soundName: String = ""
    var isSet: Boolean = false
    var notificationUrl: String = ""

    companion object {
        fun create(id: Int, displayName: String,/* soundName: String,*/ isSet: Boolean = false, notificationUrl: String): NotificationTone {
            val notificationTone = NotificationTone()
            notificationTone.id = id
            notificationTone.displayName = displayName
         //   notificationTone.soundName = soundName
            notificationTone.isSet = isSet
            notificationTone.notificationUrl = notificationUrl


            return notificationTone

        }
    }
}
