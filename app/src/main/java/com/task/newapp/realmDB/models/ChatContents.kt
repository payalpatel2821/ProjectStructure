// Please note : @LinkingObjects and default values are not represented in the schema and thus will not be part of the generated models
package com.task.newapp.realmDB.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class ChatContents : RealmObject() {
    companion object {
        const val PROPERTY_id = "id"
        const val PROPERTY_content_id = "content_id"
        const val PROPERTY_chat_id = "chat_id"
        const val PROPERTY_content = "content"
        const val PROPERTY_type = "type"
        const val PROPERTY_caption = "caption"
        const val PROPERTY_size = "size"
        const val PROPERTY_delete_for = "delete_for"
        const val PROPERTY_created_at = "created_at"
        const val PROPERTY_updated_at = "updated_at"
        const val PROPERTY_local_path = "local_path"
        const val PROPERTY_is_download = "is_download"
        const val PROPERTY_assets = "assets"
    }

    @PrimaryKey
    var id: Int = 0
    var content_id: Int = 0
    var chat_id: Int = 0
    var content: String = ""
    var type: String = ""
    var caption: String? = ""
    var size: Double = 0.0
    var delete_for: String? = ""
    var created_at: String = ""
    var updated_at: String = ""
    var local_path: String = ""
    var is_download: Boolean = false
    var assets: UploadDataObject? = null

}
