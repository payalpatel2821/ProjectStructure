// Please note : @LinkingObjects and default values are not represented in the schema and thus will not be part of the generated models
package com.task.newapp.realmDB.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class ChatContacts : RealmObject() {
    companion object {
        const val PROPERTY_contact_id = "contact_id"
        const val PROPERTY_id = "id"
        const val PROPERTY_chat_id = "chat_id"
        const val PROPERTY_name = "name"
        const val PROPERTY_number = "number"
        const val PROPERTY_email = "email"
        const val PROPERTY_size = "size"
        const val PROPERTY_created_at = "created_at"
        const val PROPERTY_updated_at = "updated_at"
        const val PROPERTY_profile_image = "profile_image"
        const val PROPERTY_profile_color = "profile_color"
        const val PROPERTY_delete_for = "delete_for"
        const val PROPERTY_flag = "flag"
    }

    @PrimaryKey
    var contact_id: Int = 0
    var id: Int = 0
    var chat_id: Int = 0
    var name: String = ""
    var number: String = ""
    var email: String = ""
    var size: Double = 0.0
    var created_at: String = ""
    var updated_at: String = ""
    var profile_image: String = ""
    var profile_color: String = ""
    var delete_for: String? = ""
    var flag: String = ""

}
