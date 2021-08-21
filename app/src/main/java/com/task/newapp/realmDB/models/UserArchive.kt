// Please note : @LinkingObjects and default values are not represented in the schema and thus will not be part of the generated models
package com.task.newapp.realmDB.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class UserArchive : RealmObject() {
    @PrimaryKey
    var id: Int = 0
    var userId: Int = 0
    var isFriend: Int = 0
    var isGroup: Int = 0
    var friendId: Int = 0
    var groupId: Int = 0

    companion object {
        fun create(id: Int, userId: Int, isFriend: Int, isGroup: Int, friendId: Int, groupId: Int): UserArchive {
            val userArchive = UserArchive()
            userArchive.id = id
            userArchive.userId = userId
            userArchive.isFriend = isFriend
            userArchive.isGroup = isGroup
            userArchive.friendId = friendId
            userArchive.groupId = groupId


            return userArchive

        }
    }


}
