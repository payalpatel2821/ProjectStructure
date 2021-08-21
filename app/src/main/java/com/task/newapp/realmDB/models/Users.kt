// Please note : @LinkingObjects and default values are not represented in the schema and thus will not be part of the generated models
package com.task.newapp.realmDB.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Users : RealmObject() {
    @PrimaryKey
    var receiverId: Int = 0
    var firstName: String? = null
    var lastName: String? = null
    var userName: String? = null
    var profileImage: String? = null
    var profileColor: String? = null

    companion object {
        fun create(receiverId: Int, firstName: String, lastName: String, userName: String, profileImage: String, profileColor: String): Users {
            val user = Users()
            user.receiverId = receiverId
            user.firstName = firstName
            user.lastName = lastName
            user.userName = userName
            user.profileImage = profileImage
            user.profileColor = profileColor
            return user
        }
    }
}
