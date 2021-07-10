// Please note : @LinkingObjects and default values are not represented in the schema and thus will not be part of the generated models
package com.task.newapp.realmDB.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class UserSettings : RealmObject() {

    companion object {
        const val PROPERTY_id = "id"
        const val PROPERTY_user_id = "user_id"
        const val PROPERTY_theme_id = "theme_id"
        const val PROPERTY_wallpaper_id = "wallpaper_id"
        const val PROPERTY_is_chat_notification = "is_chat_notification"
        const val PROPERTY_is_new_post_notify = "is_new_post_notify"
        const val PROPERTY_is_tag_notification = "is_tag_notification"
        const val PROPERTY_profile_image = "profile_image"
        const val PROPERTY_profile_color = "profile_color"
        const val PROPERTY_notification_tone_id = "notification_tone_id"
        const val PROPERTY_is_followers_viewable = "is_followers_viewable"
        const val PROPERTY_is_friend_enable = "is_friend_enable"
        const val PROPERTY_is_image_wallpaper = "is_image_wallpaper"
        const val PROPERTY_is_color_wallpaper = "is_color_wallpaper"
        const val PROPERTY_wall_paper_color = "wall_paper_color"
        const val PROPERTY_is_default_wallpaper = "is_default_wallpaper"
        const val PROPERTY_is_no_wallpaper = "is_no_wallpaper"
        const val PROPERTY_is_gallery_wallpaper = "is_gallery_wallpaper"
        const val PROPERTY_gallery_image = "gallery_image"
        const val PROPERTY_font_size = "font_size"
        const val PROPERTY_languages = "languages"
        const val PROPERTY_is_enter_send = "is_enter_send"
        const val PROPERTY_is_media_visible = "is_media_visible"
        const val PROPERTY_is_photo_autodownload = "is_photo_autodownload"
        const val PROPERTY_is_audio_autodownload = "is_audio_autodownload"
        const val PROPERTY_is_video_autodownload = "is_video_autodownload"
        const val PROPERTY_is_document_autodownload = "is_document_autodownload"
        const val PROPERTY_is_photo_autodownload_wifi = "is_photo_autodownload_wifi"
        const val PROPERTY_is_audio_autodownload_wifi = "is_audio_autodownload_wifi"
        const val PROPERTY_is_video_autodownload_wifi = "is_video_autodownload_wifi"
        const val PROPERTY_is_document_autodownload_wifi = "is_document_autodownload_wifi"
        const val PROPERTY_is_photo_autodownload_roaming = "is_photo_autodownload_roaming"
        const val PROPERTY_is_audio_autodownload_roaming = "is_audio_autodownload_roaming"
        const val PROPERTY_is_video_autodownload_roaming = "is_video_autodownload_roaming"
        const val PROPERTY_is_document_autodownload_roaming = "is_document_autodownload_roaming"
        const val PROPERTY_is_delete_request = "is_delete_request"
        const val PROPERTY_story_view = "story_view"
        const val PROPERTY_story_download = "story_download"
        const val PROPERTY_vibrate_status = "vibrate_status"
        const val PROPERTY_is_popup_notification = "is_popup_notification"
        const val PROPERTY_use_high_priority_notification = "use_high_priority_notification"
        const val PROPERTY_last_seen = "last_seen"
        const val PROPERTY_is_visible = "is_visible"
        const val PROPERTY_profile_seen = "profile_seen"
        const val PROPERTY_call_ringtone = "call_ringtone"
        const val PROPERTY_call_vibrate = "call_vibrate"
        const val PROPERTY_about_seen = "about_seen"
        const val PROPERTY_who_can_add_me_in_group = "who_can_add_me_in_group"
        const val PROPERTY_live_location_sharing = "live_location_sharing"
        const val PROPERTY_is_fingerprint_lock_enabled = "is_fingerprint_lock_enabled"
        const val PROPERTY_is_show_security_notification = "is_show_security_notification"
        const val PROPERTY_is_two_step_verification_enabled = "is_two_step_verification_enabled"
        const val PROPERTY_group_vibrate_status = "group_vibrate_status"
        const val PROPERTY_group_is_popup_notification = "group_is_popup_notification"
        const val PROPERTY_group_use_high_priority_notification = "group_use_high_priority_notification"
        const val PROPERTY_group_notification_tone_id = "group_notification_tone_id"
        const val PROPERTY_near_location = "near_location"
        const val PROPERTY_sound = "sound"
        const val PROPERTY_mute_notification = "mute_notification"
        const val PROPERTY_is_custom_notification_enable = "is_custom_notification_enable"
        const val PROPERTY_user = "user"
        const val PROPERTY_created_at = "created_at"
        const val PROPERTY_updated_at = "updated_at"
    }

    @PrimaryKey
    var id: Int = 0
    var user_id: Int = 0
    var theme_id: Int = 0
    var wallpaper_id: Int = 0
    var is_chat_notification: Int = 0
    var is_new_post_notify: Int = 0
    var is_tag_notification: Int = 0
    var profile_image: String? = ""
    var profile_color: String? = ""
    var notification_tone_id: Int = 0
    var is_followers_viewable: Int = 0
    var is_friend_enable: Int = 0
    var is_image_wallpaper: Int = 0
    var is_color_wallpaper: Int = 0
    var wall_paper_color: String? = ""
    var is_default_wallpaper: Int = 0
    var is_no_wallpaper: Int = 0
    var is_gallery_wallpaper: Int = 0
    var gallery_image: String = ""
    var font_size: String = ""
    var languages: String = ""
    var is_enter_send: Int = 0
    var is_media_visible: Int = 0
    var is_photo_autodownload: Int = 0
    var is_audio_autodownload: Int = 0
    var is_video_autodownload: Int = 0
    var is_document_autodownload: Int = 0
    var is_photo_autodownload_wifi: Int = 0
    var is_audio_autodownload_wifi: Int = 0
    var is_video_autodownload_wifi: Int = 0
    var is_document_autodownload_wifi: Int = 0
    var is_photo_autodownload_roaming: Int = 0
    var is_audio_autodownload_roaming: Int = 0
    var is_video_autodownload_roaming: Int = 0
    var is_document_autodownload_roaming: Int = 0
    var is_delete_request: Int = 0
    var story_view: String = ""
    var story_download: String = ""
    var vibrate_status: String = ""
    var is_popup_notification: Int = 0
    var use_high_priority_notification: Int = 0
    var last_seen: String = ""
    var is_visible: Int = 0
    var profile_seen: String = ""
    var call_ringtone: String = ""
    var call_vibrate: String = ""
    var about_seen: String = ""
    var who_can_add_me_in_group: String = ""
    var live_location_sharing: Int = 0
    var is_fingerprint_lock_enabled: Int = 0
    var is_show_security_notification: Int = 0
    var is_two_step_verification_enabled: Int = 0
    var group_vibrate_status: String = ""
    var group_is_popup_notification: Int = 0
    var group_use_high_priority_notification: Int = 0
    var group_notification_tone_id: Int = 0
    var near_location: Int = 0
    var sound: String = ""
    var user: Users? = null
    var created_at: String = ""
    var updated_at: String = ""
}
