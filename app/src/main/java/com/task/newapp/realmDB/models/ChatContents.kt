// Please note : @LinkingObjects and default values are not represented in the schema and thus will not be part of the generated models
package com.task.newapp.realmDB.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class ChatContents : RealmObject() {
    @PrimaryKey
    var contentId: Long = 0
    var id: Int = 0
    var chatId: Int = 0
    var content: String = ""
    var type: String = ""
    var caption: String? = ""
    var size: Double = 0.0
    var duration: Double = 0.0
    var title: String = ""
    var name: String = ""
    var number: String = ""
    var email: String = ""
    var profileImage: String = ""
    var location: String = ""
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var endTime: String = ""
    var sharingTime: String = ""
    var locationType: String = ""
    var deleteFor: String? = ""
    var createdAt: String = ""
    var updatedAt: String = ""
    var localPath: String = ""
    var isDownload: Boolean = false
    var data: ByteArray? = null

    //var assets: UploadDataObject? = null
    companion object {
        fun create(
            contentId: Long, id: Int, chatId: Int, content: String, type: String, caption: String, size: Double, duration: Double,
            title: String, name: String, number: String, email: String, profileImage: String, location: String, latitude: Double, longitude: Double, endTime: String,
            sharingTime: String, locationType: String, deleteFor: String, createdAt: String, updatedAt: String, localPath: String, isDownload: Boolean, data: ByteArray
        ): ChatContents {
            val chatContents = ChatContents()
            chatContents.contentId = contentId
            chatContents.id = id
            chatContents.chatId = chatId
            chatContents.content = content
            chatContents.type = type
            chatContents.caption = caption
            chatContents.size = size
            chatContents.duration = duration
            chatContents.title = title
            chatContents.name = name
            chatContents.number = number
            chatContents.email = email
            chatContents.profileImage = profileImage
            chatContents.location = location
            chatContents.latitude = latitude
            chatContents.longitude = longitude
            chatContents.endTime = endTime
            chatContents.sharingTime = sharingTime
            chatContents.locationType = locationType
            chatContents.deleteFor = deleteFor
            chatContents.createdAt = createdAt
            chatContents.updatedAt = updatedAt
            chatContents.localPath = localPath
            chatContents.isDownload = isDownload
            chatContents.data = data

            return chatContents

        }
    }
}
