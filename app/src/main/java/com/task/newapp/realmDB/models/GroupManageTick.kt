// Please note : @LinkingObjects and default values are not represented in the schema and thus will not be part of the generated models
package com.task.newapp.realmDB.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class GroupManageTick : RealmObject() {
    @PrimaryKey
    var id: Long = 0
    var userId: Int = 0
    var groupUserId: Int = 0
    var chatId: Long = 0
    var groupId: Int = 0
    var tick: Int = 0
    var isRead: Int = 0
    var deliverTime: String = ""
    var readTime: String = ""

    companion object {
        fun create(id: Long, userId: Int, groupUserId: Int, chatId: Long, groupId: Int, tick: Int, isRead: Int, deliverTime: String, readTime: String): GroupManageTick {
            val groupManageTick = GroupManageTick()
            groupManageTick.id = id
            groupManageTick.userId = userId
            groupManageTick.groupUserId = groupUserId
            groupManageTick.chatId = chatId
            groupManageTick.groupId = groupId
            groupManageTick.tick = tick
            groupManageTick.isRead = isRead
            groupManageTick.deliverTime = deliverTime
            groupManageTick.readTime = readTime

            return groupManageTick

        }
    }

}
