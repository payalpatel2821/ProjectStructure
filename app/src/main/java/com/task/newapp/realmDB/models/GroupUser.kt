// Please note : @LinkingObjects and default values are not represented in the schema and thus will not be part of the generated models
package com.task.newapp.realmDB.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class GroupUser : RealmObject() {
    companion object {
        const val PROPERTY_id = "id"
        const val PROPERTY_user_id = "user_id"
        const val PROPERTY_label_color = "label_color"
        const val PROPERTY_location = "location"
        const val PROPERTY_is_admin = "is_admin"
        const val PROPERTY_is_allow_to_add_post = "is_allow_to_add_post"
        const val PROPERTY_is_mute_notification = "is_mute_notification"
        const val PROPERTY_is_report = "is_report"
        const val PROPERTY_status = "status"
        const val PROPERTY_created_at = "created_at"
        const val PROPERTY_updated_at = "updated_at"
        const val PROPERTY_is_deleted = "is_deleted"
        const val PROPERTY_is_allow_to_edit_info = "is_allow_to_edit_info"
        const val PROPERTY_mute_time = "mute_time"
        const val PROPERTY_end_mute_time = "end_mute_time"
        const val PROPERTY_media_viewable = "media_viewable"
        const val PROPERTY_custom_notification_enable = "custom_notification_enable"
        const val PROPERTY_vibrate_status = "vibrate_status"
        const val PROPERTY_is_popup_notification = "is_popup_notification"
        const val PROPERTY_light = "light"
        const val PROPERTY_high_priority_notification = "high_priority_notification"
        const val PROPERTY_notification_tone_id = "notification_tone_id"
        const val PROPERTY_grp_id = "grp_id"
    }

    @PrimaryKey
    var id: Int = 0
    var user_id: Int = 0
    var grp_id: Int = 0
    var label_color: String? = null
    var location: String? = null
    var is_admin: Int = 0
    var is_allow_to_add_post: Int = 0
    var is_mute_notification: Int = 0
    var is_report: Int = 0
    var status: String? = null
    var is_deleted: Int = 0
    var is_allow_to_edit_info: Int = 0
    var mute_time: String? = null
    var end_mute_time: String? = null
    var media_viewable: Int = 0
    var custom_notification_enable: Int = 0
    var vibrate_status: String? = null
    var is_popup_notification: Int = 0
    var light: String? = null
    var high_priority_notification: Int = 0
    var notification_tone_id: Int = 0
    var created_at: String? = null
    var updated_at: String? = null

}
