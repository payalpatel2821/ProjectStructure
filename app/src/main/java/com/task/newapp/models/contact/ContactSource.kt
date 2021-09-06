package com.task.newapp.models.contact

import com.task.newapp.utils.ContactsConstants.Companion.SMT_PRIVATE

data class ContactSource(var name: String, var type: String, var publicName: String) {
    fun getFullIdentifier(): String {
        return if (type == SMT_PRIVATE) {
            type
        } else {
            "$name:$type"
        }
    }
}
