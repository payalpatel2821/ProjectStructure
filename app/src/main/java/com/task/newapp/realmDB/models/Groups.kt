package com.task.newapp.realmDB.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Groups : RealmObject() {
    @PrimaryKey
    var id: Int = 0
    var userId: Int = 0
    var createdBy: Int = 0
    var name: String? = null
    var description: String? = null
    var icon: String? = null
    var profileColor: String? = null
    var totalUser: Int = 0
    var otherUserId: String? = null
    var createdAt: String? = null
    var updatedAt: String? = null
    var editInfoPermission: String? = null
    var sendMessage: String? = null

    companion object {
        fun create(id: Int, userId: Int, createdBy: Int, name: String, description: String, icon: String, profileColor: String, totalUser: Int,
                   otherUserId: String, createdAt: String, updatedAt: String, editInfoPermission: String, sendMessage: String, ): Groups {
            val groups = Groups()
            groups.id = id
            groups.userId = userId
            groups.createdBy = createdBy
            groups.name = name
            groups.description = description
            groups.icon = icon
            groups.profileColor = profileColor
            groups.totalUser = totalUser
            groups.otherUserId = otherUserId
            groups.createdAt = createdAt
            groups.updatedAt = updatedAt
            groups.editInfoPermission = editInfoPermission
            groups.sendMessage = sendMessage

            return groups

        }
    }

}