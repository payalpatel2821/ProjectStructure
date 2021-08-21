// Please note : @LinkingObjects and default values are not represented in the schema and thus will not be part of the generated models
package com.task.newapp.realmDB.models

import io.realm.RealmList
import io.realm.RealmObject

open class LocalImageVideoDataTask : RealmObject() {
    var title: String? = null
    var desc: String? = null
    var isLocation: String? = null
    var latitude: String? = null
    var longitude: String? = null
    var locationName: String? = null
    var hashTag: String? = null
    var postCommentDisable: String? = null
    var userTags: String? = null
    var assets: RealmList<UploadDataObject> = RealmList()

    companion object {
        fun create(
            title: String, desc: String, isLocation: String, latitude: String, longitude: String, locationName: String, hashTag: String, postCommentDisable: String,
            userTags: String, assets: RealmList<UploadDataObject>
        ): LocalImageVideoDataTask {
            val localImageVideoDataTask = LocalImageVideoDataTask()
            localImageVideoDataTask.title = title
            localImageVideoDataTask.desc = desc
            localImageVideoDataTask.isLocation = isLocation
            localImageVideoDataTask.latitude = latitude
            localImageVideoDataTask.longitude = longitude
            localImageVideoDataTask.locationName = locationName
            localImageVideoDataTask.hashTag = hashTag
            localImageVideoDataTask.postCommentDisable = postCommentDisable
            localImageVideoDataTask.userTags = userTags
            localImageVideoDataTask.assets = assets

            return localImageVideoDataTask

        }
    }
}
