// Please note : @LinkingObjects and default values are not represented in the schema and thus will not be part of the generated models
package com.task.newapp.realmDB.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class LoginUsers : RealmObject() {
    @PrimaryKey
    var id: Int = 0
    var userName: String = ""
    var password: String = ""
    var isRemember: Boolean = false
    var lastLoginTime: String = ""

    companion object {
        fun create(id: Int, userName: String, password: String, isRemember: Boolean, lastLoginTime: String): LoginUsers {
            val loginUsers = LoginUsers()
            loginUsers.id = id
            loginUsers.userName = userName
            loginUsers.password = password
            loginUsers.isRemember = isRemember
            loginUsers.lastLoginTime = lastLoginTime


            return loginUsers

        }
    }
}
