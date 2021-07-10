// Please note : @LinkingObjects and default values are not represented in the schema and thus will not be part of the generated models
package com.task.newapp.realmDB.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class ChatDocument : RealmObject() {
    companion object {
        const val PROPERTY_document_id = "document_id"
        const val PROPERTY_id = "id"
        const val PROPERTY_chat_id = "chat_id"
        const val PROPERTY_document = "document"
        const val PROPERTY_title = "title"
        const val PROPERTY_type = "type"
        const val PROPERTY_size = "size"
        const val PROPERTY_delete_for = "delete_for"
        const val PROPERTY_created_at = "created_at"
        const val PROPERTY_updated_at = "updated_at"
        const val PROPERTY_no_of_pages = "no_of_pages"
        const val PROPERTY_local_path = "local_path"
        const val PROPERTY_flag = "flag"
        const val PROPERTY_is_download = "is_download"
        const val PROPERTY_document_data = "document_data"
    }

    @PrimaryKey
    var document_id: Int = 0
    var id: Int = 0
    var chat_id: Int = 0
    var document: String = ""
    var title: String = ""
    var type: String = ""
    var size: Double = 0.0
    var delete_for: String? = ""
    var created_at: String = ""
    var updated_at: String = ""
    var no_of_pages: Int = 0
    var local_path: String = ""
    var flag: String = ""
    var is_download: Boolean = false
    var document_data: ByteArray? = null

}
