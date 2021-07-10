// Please note : @LinkingObjects and default values are not represented in the schema and thus will not be part of the generated models
package com.task.newapp.realmDB.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class FriendRequest : RealmObject() {
    companion object {
        const val PROPERTY_id = "_id"
        const val PROPERTY_user_id = "user_id"
        const val PROPERTY_friend_id = "friend_id"
        const val PROPERTY_is_request = "is_request"
        const val PROPERTY_status = "status"
        const val PROPERTY_sender_request_count = "sender_request_count"
        const val PROPERTY_receiver_request_count = "receiver_request_count"
    }

    @PrimaryKey
    var _id: Int = 0
    var user_id: Int = 0
    var friend_id: Int = 0
    var is_request: Int = 0
    var status: String = ""
    var sender_request_count: Int = 0
    var receiver_request_count: Int = 0
    var created_at: String = ""
    var updated_at: String = ""

}
