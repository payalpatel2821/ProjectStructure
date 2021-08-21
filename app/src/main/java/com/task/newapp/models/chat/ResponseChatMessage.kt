package com.task.newapp.models.chat

import com.google.gson.annotations.SerializedName
import com.task.newapp.models.FriendRequestModel
import com.task.newapp.models.OtherUserModel
import java.io.Serializable


data class ResponseChatMessage(
    @SerializedName("create_request")
    val createRequest: FriendRequestModel? = null,
    @SerializedName("data")
    val data: ChatModel? = null,
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("receiver")
    val receiver: OtherUserModel? = null,
    @SerializedName("sender")
    val sender: OtherUserModel? = null,
    @SerializedName("success")
    val success: Int? = null
) : Serializable


