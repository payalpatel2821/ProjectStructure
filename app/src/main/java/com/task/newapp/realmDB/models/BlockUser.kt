// Please note : @LinkingObjects and default values are not represented in the schema and thus will not be part of the generated models
package com.task.newapp.realmDB.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class BlockUser : RealmObject() {
    @PrimaryKey
    var blockedUserId: Int = 0
    var isBlocked: Int = 0
    var isReported: Int = 0

    companion object {
        fun create(blockUserId: Int, isBlocked: Int, isReported: Int): BlockUser {
            val blockUser = BlockUser()
            blockUser.blockedUserId = blockUserId
            blockUser.isBlocked = isBlocked
            blockUser.isReported = isReported
            return blockUser

        }
    }
}
