package com.task.newapp.models.chat

import com.google.gson.annotations.SerializedName
import com.task.newapp.models.OtherUserModel

data class HookData(
        @SerializedName("id")
        val id: Int,
        @SerializedName("user_id")
        val userId: Int,
        @SerializedName("is_friend")
        val isFriend: Int,
        @SerializedName("is_group")
        val isGroup: Int,
        @SerializedName("friend_id")
        val friendId: Int,
        @SerializedName("group_id")
        val groupId: Int,
        @SerializedName("hook_id")
        val hookId: Any,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("updated_at")
        val updatedAt: String,
        @SerializedName("friend")
        val friend: OtherUserModel?
    )