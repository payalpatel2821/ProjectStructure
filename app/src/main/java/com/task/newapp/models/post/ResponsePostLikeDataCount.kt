package com.task.newapp.models.post


import com.google.gson.annotations.SerializedName

data class ResponsePostLikeDataCount(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("success")
    val success: Int
) {
    data class Data(
        @SerializedName("comment_count")
        val commentCount: String,
        @SerializedName("flag")
        val flag: String,
        @SerializedName("id")
        val id: Int,
        @SerializedName("page_id")
        val pageId: Any,
        @SerializedName("post_like")
        val postLike: String,
        @SerializedName("post_page")
        val postPage: List<Any>,
        @SerializedName("post_view")
        val postView: List<Any>,
        @SerializedName("post_view_count")
        val postViewCount: String,
        @SerializedName("user_followers")
        val userFollowers: String,
        @SerializedName("user_id")
        val userId: Int,
        @SerializedName("you_like_post")
        val youLikePost: Int
    )
}