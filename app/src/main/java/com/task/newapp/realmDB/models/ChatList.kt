// Please note : @LinkingObjects and default values are not represented in the schema and thus will not be part of the generated models
package com.task.newapp.realmDB.models

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class ChatList : RealmObject() {
    companion object {
        const val PROPERTY_local_chat_id = "local_chat_id"
        const val PROPERTY_id = "id"
        const val PROPERTY_user_id = "user_id"
        const val PROPERTY_receiver_id = "receiver_id"
        const val PROPERTY_sender_id = "sender_id"
        const val PROPERTY_is_group_chat = "is_group_chat"
        const val PROPERTY_group_id = "group_id"
        const val PROPERTY_type = "type"
        const val PROPERTY_message_text = "message_text"
        const val PROPERTY_is_shared = "is_shared"
        const val PROPERTY_is_forward = "is_forward"
        const val PROPERTY_is_deleted = "is_deleted"
        const val PROPERTY_deleted_for_all = "deleted_for_all"
        const val PROPERTY_deleted_by = "deleted_by"
        const val PROPERTY_tick = "tick"
        const val PROPERTY_is_star = "is_star"
        const val PROPERTY_is_reply = "is_reply"
        const val PROPERTY_is_reply_to_message = "is_reply_to_message"
        const val PROPERTY_is_reply_to_story = "is_reply_to_story"
        const val PROPERTY_story_id = "is_story_id"
        const val PROPERTY_is_replyback_to_reply = "is_replyback_to_reply"
        const val PROPERTY_is_secret = "is_secret"
        const val PROPERTY_is_read = "is_read"
        const val PROPERTY_created_at = "created_at"
        const val PROPERTY_is_activity_label = "is_activity_label"
        const val PROPERTY_event = "event"
        const val PROPERTY_is_broadcast_chat = "is_broadcast_chat"
        const val PROPERTY_broadcast_id = "broadcast_id"
        const val PROPERTY_broadcast_chat_id = "broadcast_chat_id"
        const val PROPERTY_is_content_reply = "is_content_reply"
        const val PROPERTY_content_id = "content_id"
        const val PROPERTY_grp_other_user_id = "grp_other_user_id"
        const val PROPERTY_entry_id = "entry_id"
        const val PROPERTY_chat_id = "chat_id"
        const val PROPERTY_isSync = "isSync"
        const val PROPERTY_chat_contents = "chat_contents"
        const val PROPERTY_chat_audio = "chat_audio"
        const val PROPERTY_chat_location = "chat_location"
        const val PROPERTY_chat_contacts = "chat_contacts"
        const val PROPERTY_chat_voice = "chat_voice"
        const val PROPERTY_chat_document = "chat_document"
        const val PROPERTY_user_data = "user_data"
        const val PROPERTY_grp_label_color = "grp_label_color"

    }

    @PrimaryKey
    var local_chat_id: Int = 0
    var id: Int = 0
    var user_id: Int = 0
    var receiver_id: Int = 0
    var sender_id: Int = 0
    var is_group_chat: Int = 0
    var group_id: Int = 0
    var type: String? = null
    var message_text: String? = null
    var is_shared: Int = 0
    var is_forward: Int = 0
    var is_deleted: Int = 0
    var deleted_for_all: Int = 0
    var deleted_by: Int = 0
    var tick: String = ""
    var is_star: Int = 0
    var is_reply: Int = 0
    var is_reply_to_message: Int = 0
    var is_reply_to_story: Int = 0
    var story_id: Int = 0
    var is_replyback_to_reply: Int = 0
    var is_secret: Int = 0
    var is_read: Int = 0
    var created_at: String? = null
    var is_activity_label: Int = 0
    var event: String? = null
    var is_broadcast_chat: Int = 0
    var broadcast_id: Int = 0
    var broadcast_chat_id: Int = 0
    var is_content_reply: Int = 0
    var content_id: Int = 0
    var grp_other_user_id: String? = null
    var entry_id: Int = 0
    var chat_id: Int = 0
    var isSync: Boolean = false
    var chat_contents: RealmList<ChatContents> = RealmList()
    var chat_audio: RealmList<ChatAudio> = RealmList()
    var chat_location: ChatLocation? = null
    var chat_contacts: RealmList<ChatContacts> = RealmList()
    var chat_voice: RealmList<ChatVoice> = RealmList()
    var chat_document: RealmList<ChatDocument> = RealmList()
    var user_data: Users? = null
    var grp_label_color: String? = null

}
