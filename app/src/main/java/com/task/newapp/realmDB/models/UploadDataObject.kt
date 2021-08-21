// Please note : @LinkingObjects and default values are not represented in the schema and thus will not be part of the generated models
package com.task.newapp.realmDB.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class UploadDataObject : RealmObject() {
    @PrimaryKey
    var id: String = ""
    var assetType: String? = null
    var assetName: String? = null
    var caption: String? = null
    var data: ByteArray? = null
    var thumbData: ByteArray? = null

    companion object {
        fun create(id: String, assetType: String, assetName: String, caption: String, data: ByteArray, thumbData: ByteArray): UploadDataObject {
            val uploadDataObject = UploadDataObject()
            uploadDataObject.id = id
            uploadDataObject.assetType = assetType
            uploadDataObject.assetName = assetName
            uploadDataObject.caption = caption
            uploadDataObject.data = data
            uploadDataObject.thumbData = thumbData


            return uploadDataObject

        }
    }
}
