// Please note : @LinkingObjects and default values are not represented in the schema and thus will not be part of the generated models
package com.task.newapp.realmDB.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class UserHook : RealmObject() {
    companion object {
        const val PROPERTY_id = "id"
        const val PROPERTY_user_id = "user_id"
        const val PROPERTY_is_friend = "is_friend"
        const val PROPERTY_is_group = "is_group"
        const val PROPERTY_friend_id = "friend_id"
        const val PROPERTY_group_id = "group_id"
    }

    @PrimaryKey
    var id: Int = 0
    var user_id: Int = 0
    var is_friend: Int = 0
    var is_group: Int = 0
    var friend_id: Int = 0
    var group_id: Int = 0

}
