// Please note : @LinkingObjects and default values are not represented in the schema and thus will not be part of the generated models
package com.task.newapp.realmDB.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class ChatLocation : RealmObject() {
    companion object {
        const val PROPERTY_location_id = "location_id"
        const val PROPERTY_id = "id"
        const val PROPERTY_chat_id = "chat_id"
        const val PROPERTY_user_id = "user_id"
        const val PROPERTY_location = "location"
        const val PROPERTY_latitude = "latitude"
        const val PROPERTY_longitude = "longitude"
        const val PROPERTY_size = "size"
        const val PROPERTY_created_at = "created_at"
        const val PROPERTY_updated_at = "updated_at"
        const val PROPERTY_type = "type"
        const val PROPERTY_delete_for = "delete_for"
        const val PROPERTY_sharing_time = "sharing_time"
        const val PROPERTY_end_time = "end_time"
        const val PROPERTY_location_data = "location_data"
    }

    @PrimaryKey
    var location_id: Int = 0
    var id: Int = 0
    var chat_id: Int = 0
    var user_id: Int = 0
    var location: String = ""
    var latitude: String = ""
    var longitude: String = ""
    var size: Double = 0.0
    var created_at: String = ""
    var updated_at: String = ""
    var type: String = ""
    var delete_for: String? = ""
    var sharing_time: String = ""
    var end_time: String = ""
    var location_data: ByteArray? = null

}
