// Please note : @LinkingObjects and default values are not represented in the schema and thus will not be part of the generated models
package com.task.newapp.realmDB.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.io.Serializable

open class Groups : RealmObject()   {

    companion object {
        const val PROPERTY_grp_id = "grp_id"
        const val PROPERTY_grp_user_id = "grp_user_id"
        const val PROPERTY_grp_name = "grp_name"
        const val PROPERTY_grp_description = "grp_description"
        const val PROPERTY_grp_icon = "grp_icon"
        const val PROPERTY_grp_profile_color = "profile_color"
        const val PROPERTY_grp_total_user = "grp_total_user"
        const val PROPERTY_grp_other_user_id = "grp_other_user_id"
        const val PROPERTY_grp_created_at = "grp_created_at"
        const val PROPERTY_grp_updated_at = "grp_updated_at"
        const val PROPERTY_grp_edit_info_permission = "grp_edit_info_permission"
        const val PROPERTY_grp_send_msg = "grp_send_msg"
    }

    @PrimaryKey
    var grp_id: Int = 0
    var grp_user_id: Int = 0
    var grp_name: String? = null
    var grp_description: String? = null
    var grp_icon: String? = null
    var grp_profile_color: String? = null
    var grp_total_user: Int = 0
    var grp_other_user_id: String? = null
    var grp_created_at: String? = null
    var grp_updated_at: String? = null
    var grp_edit_info_permission: String? = null
    var grp_send_msg: String? = null

}

