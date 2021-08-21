// Please note : @LinkingObjects and default values are not represented in the schema and thus will not be part of the generated models
package com.task.newapp.realmDB.models

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class BroadcastTable : RealmObject() {
    @PrimaryKey
    var broadcastId: Int = 0
    var userId: Int = 0
    var broadcastName: String? = null
    var broadcastIcon: String? = null
    var broadcastTotalUser: Int = 0
    var broadcastOtherUserId: String? = null
    var createdAt: String? = null
    var updatedAt: String? = null
    var chats: RealmList<ChatList> = RealmList()

    companion object {
        fun create(
            broadcastId: Int, userId: Int, broadcastName: String, broadcastIcon: String,
            broadcastTotalUser: Int, broadcastOtherUserId: String, createdAt: String, updatedAt: String
        ): BroadcastTable {
            val broadcastTable = BroadcastTable()
            broadcastTable.broadcastId = broadcastId
            broadcastTable.userId = userId
            broadcastTable.broadcastName = broadcastName
            broadcastTable.broadcastIcon = broadcastIcon
            broadcastTable.broadcastTotalUser = broadcastTotalUser
            broadcastTable.broadcastOtherUserId = broadcastOtherUserId
            broadcastTable.createdAt = createdAt
            broadcastTable.updatedAt = updatedAt
            return broadcastTable

        }
    }
}
