// Please note : @LinkingObjects and default values are not represented in the schema and thus will not be part of the generated models
package com.task.newapp.realmDB.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class UploadDataObject : RealmObject() {
    companion object {
        const val PROPERTY_id = "id"
        const val PROPERTY_asset_type = "asset_type"
        const val PROPERTY_asset_name = "asset_name"
        const val PROPERTY_caption = "caption"
        const val PROPERTY_data = "data"
        const val PROPERTY_thumb_data = "thumb_data"
    }

    @PrimaryKey
    var id: String = ""
    var asset_type: String? = null
    var asset_name: String? = null
    var caption: String? = null
    var data: ByteArray? = null
    var thumb_data: ByteArray? = null

}
