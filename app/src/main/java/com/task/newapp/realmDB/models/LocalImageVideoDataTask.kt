// Please note : @LinkingObjects and default values are not represented in the schema and thus will not be part of the generated models
package com.task.newapp.realmDB.models

import io.realm.RealmList
import io.realm.RealmObject

open class LocalImageVideoDataTask : RealmObject() {
    companion object {
        const val PROPERTY_title = "title"
        const val PROPERTY_desc = "desc"
        const val PROPERTY_is_location = "is_location"
        const val PROPERTY_latitude = "latitude"
        const val PROPERTY_longitude = "longitude"
        const val PROPERTY_location_name = "location_name"
        const val PROPERTY_hashtag = "hashtag"
        const val PROPERTY_post_comment_disable = "post_comment_disable"
        const val PROPERTY_user_tags = "user_tags"
        const val PROPERTY_assets = "assets"
    }

    var title: String? = null
    var desc: String? = null
    var is_location: String? = null
    var latitude: String? = null
    var longitude: String? = null
    var location_name: String? = null
    var hashtag: String? = null
    var post_comment_disable: String? = null
    var user_tags: String? = null
    var assets: RealmList<UploadDataObject> = RealmList()

}
