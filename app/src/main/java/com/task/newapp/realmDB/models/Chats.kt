// Please note : @LinkingObjects and default values are not represented in the schema and thus will not be part of the generated models
package com.task.newapp.realmDB.models

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Chats : RealmObject() {
    companion object {
        const val PROPERTY_id = "id"
        const val PROPERTY_name = "name"
        const val PROPERTY_group_data = "group_data"
        const val PROPERTY_group_user_with_settings = "group_user_with_settings"
        const val PROPERTY_chat_list = "chat_list"
        const val PROPERTY_is_group = "is_group"
        const val PROPERTY_is_hook = "is_hook"
        const val PROPERTY_is_archive = "is_archive"
        const val PROPERTY_is_block = "is_block"
        const val PROPERTY_updated_at = "updated_at"
        const val PROPERTY_current_time = "current_time"
        const val PROPERTY_is_online = "is_online"
        const val PROPERTY_user_data = "user_data"
    }

    @PrimaryKey
    var id: Int = 0
    var name: String? = null
    var is_online: Boolean = false
    var is_group: Boolean = false
    var is_hook: Boolean = false
    var is_archive: Boolean = false
    var is_block: Boolean = false
    var group_data: Groups? = null
    var group_user_with_settings: RealmList<GroupUser> = RealmList()
    var chat_list: ChatList? = null
    var updated_at: String? = null
    var current_time: String? = null
    var user_data: Users? = null

}
