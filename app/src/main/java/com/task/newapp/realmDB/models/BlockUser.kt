// Please note : @LinkingObjects and default values are not represented in the schema and thus will not be part of the generated models
package com.task.newapp.realmDB.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class BlockUser : RealmObject() {
    companion object {
        const val PROPERTY_blocked_user_id = "blocked_user_id"
        const val PROPERTY_is_blocked = "is_blocked"
        const val PROPERTY_is_reported = "is_reported"
    }

    @PrimaryKey
    var blocked_user_id: Int = 0
    var is_blocked: Int = 0
    var is_reported: Int = 0

}
