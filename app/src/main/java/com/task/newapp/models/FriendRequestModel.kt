package com.task.newapp.models

import com.google.gson.annotations.SerializedName

data class FriendRequestModel(
            @SerializedName("created_at")
            val createdAt: String,
            @SerializedName("friend_id")
            val friendId: Int,
            @SerializedName("id")
            val id: Int,
            @SerializedName("is_request")
            val isRequest: Int,
            @SerializedName("receiver_request_count")
            val receiverRequestCount: Int,
            @SerializedName("sender_request_count")
            val senderRequestCount: Int,
            @SerializedName("status")
            val status: String,
            @SerializedName("updated_at")
            val updatedAt: String,
            @SerializedName("user_id")
            val userId: Int
        )