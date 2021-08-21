// Please note : @LinkingObjects and default values are not represented in the schema and thus will not be part of the generated models
package com.task.newapp.realmDB.models

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Chats : RealmObject() {
    @PrimaryKey
    var id: Int = 0
    var name: String? = null
    var isOnline: Boolean = false
    var groupData: Groups? = null
    var groupUsers: RealmList<GroupUser> = RealmList()
    var chatList: ChatList? = null
    var isGroup: Boolean = false
    var isHook: Boolean = false
    var isArchive: Boolean = false
    var isBlock: Boolean = false
    var updatedAt: String? = null
    var currentTime: String? = null
    var userData: Users? = null

    companion object {
        fun create(
            id: Int, name: String, isOnline: Boolean, groupData: Groups, groupUsers: RealmList<GroupUser>, chatList: ChatList, isGroup: Boolean,
            isHook: Boolean, isArchive: Boolean, isBlock: Boolean, updatedAt: String, currentTime: String, userData: Users
        ): Chats {
            val chats = Chats()
            chats.id = id
            chats.name = name
            chats.isOnline = isOnline
            chats.groupData = groupData
            chats.groupUsers = groupUsers
            chats.chatList = chatList
            chats.isGroup = isGroup
            chats.isHook = isHook
            chats.isArchive = isArchive
            chats.isBlock = isBlock
            chats.updatedAt = updatedAt
            chats.currentTime = currentTime
            chats.userData = userData

            return chats

        }
    }
}
