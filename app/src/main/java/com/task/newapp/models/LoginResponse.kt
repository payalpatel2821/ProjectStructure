package com.task.newapp.models


import com.google.gson.annotations.SerializedName
import com.task.newapp.models.chat.ChatModel
import com.task.newapp.models.chat.GetAllGroup
import com.task.newapp.models.chat.GetAllRequest
import com.task.newapp.models.chat.HookData

data class LoginResponse(
    @SerializedName("success")
    val success: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("backup")
    val backup: Int,
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("get_all_request")
    val getAllRequest: List<GetAllRequest>,
    @SerializedName("block_user")
    val blockUser: List<OtherUserModel>,
    @SerializedName("user_setting")
    val userSetting: UserSetting,
    @SerializedName("post_setting")
    val postSetting: PostSetting,
    @SerializedName("archive_data")
    val archiveData: List<ArchiveData>,
    @SerializedName("hook_data")
    val hookData: List<HookData>,
    @SerializedName("get_all_group")
    val getAllGroup: List<GetAllGroup>,
    @SerializedName("broadcast")
    val broadcast: List<Broadcast>,
    @SerializedName("birthday_reminder")
    val birthdayReminder: List<Any>,
    @SerializedName("wedding_reminder")
    val weddingReminder: List<Any>,
    @SerializedName("friend_reminder")
    val friendReminder: List<Any>,
    @SerializedName("friend_settings")
    val friendSettings: List<FriendSetting>
) {
    data class ArchiveData(
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
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("updated_at")
        val updatedAt: String,
        @SerializedName("archiver")
        val archiver: OtherUserModel
    )

    data class Broadcast(
        @SerializedName("id")
        val id: Int,
        @SerializedName("user_id")
        val userId: Int,
        @SerializedName("name")
        val name: String,
        @SerializedName("icon")
        val icon: String,
        @SerializedName("total_users")
        val totalUsers: Int,
        @SerializedName("other_user_id")
        val otherUserId: String,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("updated_at")
        val updatedAt: String,
        @SerializedName("is_deleted")
        val isDeleted: Int,
        @SerializedName("chat")
        val chat: ChatModel,
    )

    data class Data(
        @SerializedName("token")
        val token: String,
        @SerializedName("user")
        val user: User
    )






}