package com.task.newapp.models

import com.google.gson.annotations.SerializedName
import com.task.newapp.realmDB.models.NotificationTone

data class NotificationToneWrapper(
    @SerializedName("notification_tone")
    var notificationTone: NotificationTone,
    @SerializedName("is_checked")
    var isChecked: Boolean = false

)
