package com.task.newapp.models.chat

import com.google.gson.annotations.SerializedName
import com.task.newapp.models.OtherUserModel

data class GetAllRequest(
        @SerializedName("id")
        val id: Int,
        @SerializedName("user_id")
        val userId: Int,
        @SerializedName("friend_id")
        val friendId: Int,
        @SerializedName("is_request")
        val isRequest: Int,
        @SerializedName("sender_request_count")
        val senderRequestCount: Int,
        @SerializedName("receiver_request_count")
        val receiverRequestCount: Int,
        @SerializedName("status")
        val status: String,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("updated_at")
        val updatedAt: String,
        @SerializedName("user")
        val user: OtherUserModel,
        @SerializedName("friend")
        val friend: OtherUserModel
    )