// Please note : @LinkingObjects and default values are not represented in the schema and thus will not be part of the generated models
package com.task.newapp.realmDB.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class ChatAudio : RealmObject() {
    companion object {
        const val PROPERTY_audio_local_id = "audio_local_id"
        const val PROPERTY_id = "id"
        const val PROPERTY_chat_id = "chat_id"
        const val PROPERTY_audio = "audio"
        const val PROPERTY_audio_filename = "audio_filename"
        const val PROPERTY_audio_caption = "audio_caption"
        const val PROPERTY_size = "size"
        const val PROPERTY_delete_for = "delete_for"
        const val PROPERTY_created_at = "created_at"
        const val PROPERTY_updated_at = "updated_at"
        const val PROPERTY_duration = "duration"
        const val PROPERTY_local_path = "local_path"
        const val PROPERTY_is_download = "is_download"
        const val PROPERTY_audio_data = "audio_data"
    }

    @PrimaryKey
    var audio_local_id: Int = 0
    var id: Int = 0
    var chat_id: Int = 0
    var audio: String = ""
    var audio_filename: String = ""
    var audio_caption: String = ""
    var size: Double = 0.0
    var delete_for: String? = ""
    var created_at: String = ""
    var updated_at: String = ""
    var duration: String = ""
    var local_path: String = ""
    var is_download: Boolean = false
    var audio_data: ByteArray? = null

}
