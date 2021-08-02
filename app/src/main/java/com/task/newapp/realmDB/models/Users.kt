// Please note : @LinkingObjects and default values are not represented in the schema and thus will not be part of the generated models
package com.task.newapp.realmDB.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Users : RealmObject() {
    companion object {
        const val PROPERTY_receiver_id = "receiver_id"
        const val PROPERTY_first_name = "first_name"
        const val PROPERTY_last_name = "last_name"
        const val PROPERTY_user_name = "user_name"
        const val PROPERTY_profile_image = "profile_image"
        const val PROPERTY_profile_color = "profile_color"
    }


    @PrimaryKey
    var receiver_id: Int = 0
    var first_name: String? = null
    var last_name: String? = null
    var user_name: String? = null
    var profile_image: String? = null
    var profile_color: String? = null
}
