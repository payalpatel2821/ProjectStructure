package com.task.newapp.models.contact

data class ContactSyncAPIModel(
    var fullName: String, var photoUri: String,
    var phoneNumbers: ArrayList<String>,
    var emails: ArrayList<String>,
)
