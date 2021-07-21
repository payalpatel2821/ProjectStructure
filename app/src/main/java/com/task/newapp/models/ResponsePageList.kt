package com.task.newapp.models


import com.google.gson.annotations.SerializedName

data class ResponsePageList(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("message")
    val message: String,
    @SerializedName("success")
    val success: Int
) {
    data class Data(
        @SerializedName("category_id")
        val categoryId: Int,
        @SerializedName("cover_image")
        val coverImage: String,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("description")
        val description: Any,
        @SerializedName("email")
        val email: Any,
        @SerializedName("flag")
        val flag: String,
        @SerializedName("followers")
        val followers: Int,
        @SerializedName("icon")
        val icon: String,
        @SerializedName("id")
        val id: Int,
        @SerializedName("is_deleted")
        val isDeleted: Int,
        @SerializedName("is_faq")
        val isFaq: Int,
        @SerializedName("is_follow")
        val isFollow: Int,
        @SerializedName("is_need_approve_join_page")
        val isNeedApproveJoinPage: Int,
        @SerializedName("latitude")
        val latitude: String,
        @SerializedName("likes")
        val likes: Int,
        @SerializedName("location")
        val location: String,
        @SerializedName("longitude")
        val longitude: String,
        @SerializedName("mobile")
        val mobile: Any,
        @SerializedName("name")
        val name: String,
        @SerializedName("other_category_id")
        val otherCategoryId: String,
        @SerializedName("page_website")
        val pageWebsite: Any,
        @SerializedName("posts")
        val posts: Int,
        @SerializedName("shares")
        val shares: Int,
        @SerializedName("status")
        val status: String,
        @SerializedName("updated_at")
        val updatedAt: String,
        @SerializedName("user_id")
        val userId: Int,
        @SerializedName("user_name")
        val userName: String,
        @SerializedName("users")
        val users: Int
    )
}