package com.task.newapp.models.post


import com.google.gson.annotations.SerializedName

data class ResponsePostComment(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("success")
    val success: Int
) {
    data class Data(
        @SerializedName("comment_id")
        val commentId: String,
        @SerializedName("comment_text")
        val commentText: String,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("first_name")
        val firstName: String,
        @SerializedName("id")
        val id: Int,
        @SerializedName("is_comment_reply")
        val isCommentReply: Int,
        @SerializedName("last_name")
        val lastName: String,
        @SerializedName("post_id")
        val postId: String,
        @SerializedName("status")
        val status: String,
        @SerializedName("total_comment")
        val totalComment: Int,
        @SerializedName("updated_at")
        val updatedAt: String,
        @SerializedName("user_id")
        val userId: Int
    )
}