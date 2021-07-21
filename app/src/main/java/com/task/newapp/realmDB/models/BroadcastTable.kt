// Please note : @LinkingObjects and default values are not represented in the schema and thus will not be part of the generated models
package com.task.newapp.realmDB.models

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class BroadcastTable : RealmObject() {
    companion object {
        const val PROPERTY_broadcast_id = "broadcast_id"
        const val PROPERTY_user_id = "user_id"
        const val PROPERTY_broad_name = "broad_name"
        const val PROPERTY_broad_icon = "broad_icon"
        const val PROPERTY_broad_total_user = "broad_total_user"
        const val PROPERTY_broad_other_userid = "broad_other_userid"
        const val PROPERTY_created_at = "created_at"
        const val PROPERTY_is_blocked = "is_blocked"
        const val PROPERTY_updated_at = "updated_at"
        const val PROPERTY_login_user = "login_user"
        const val PROPERTY_chats = "chats"
    }

    @PrimaryKey
    var broadcast_id: Int = 0
    var user_id: Int = 0
    var broad_name: String? = null
    var broad_icon: String? = null
    var broad_total_user: Int = 0
    var broad_other_userid: String? = null
    var created_at: String? = null
    var updated_at: String? = null
    var login_user: Int = 0
    var chats: RealmList<ChatList> = RealmList()

}
