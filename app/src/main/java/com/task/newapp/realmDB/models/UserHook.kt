// Please note : @LinkingObjects and default values are not represented in the schema and thus will not be part of the generated models
package com.task.newapp.realmDB.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class UserHook : RealmObject() {
    @PrimaryKey
    var id: Int = 0
    var userId: Int = 0
    var isFriend: Int = 0
    var isGroup: Int = 0
    var friendId: Int = 0
    var groupId: Int = 0

    companion object {
        fun create(id: Int, userId: Int, isFriend: Int, isGroup: Int, friendId: Int, groupId: Int): UserHook {
            val userHook = UserHook()
            userHook.id = id
            userHook.userId = userId
            userHook.isFriend = isFriend
            userHook.isGroup = isGroup
            userHook.friendId = friendId
            userHook.groupId = groupId


            return userHook

        }
    }
}
