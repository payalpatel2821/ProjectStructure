// Please note : @LinkingObjects and default values are not represented in the schema and thus will not be part of the generated models
package com.task.newapp.realmDB.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class FriendRequest : RealmObject() {
    @PrimaryKey
    var id: Int = 0
    var userId: Int = 0
    var friendId: Int = 0
    var isRequest: Int = 0
    var status: String = ""
    var senderRequestCount: Int = 0
    var receiverRequestCount: Int = 0
    var createdAt: String = ""
    var updatedAt: String = ""

    companion object {
        fun create(
            id: Int, userId: Int, friendId: Int, isRequest: Int, status: String, senderRequestCount: Int,
            receiverRequestCount: Int, createdAt: String, updatedAt: String
        ): FriendRequest {
            val friendRequest = FriendRequest()
            friendRequest.id = id
            friendRequest.userId = userId
            friendRequest.friendId = friendId
            friendRequest.isRequest = isRequest
            friendRequest.status = status
            friendRequest.senderRequestCount = senderRequestCount
            friendRequest.receiverRequestCount = receiverRequestCount
            friendRequest.createdAt = createdAt
            friendRequest.updatedAt = updatedAt
            return friendRequest

        }
    }
}
