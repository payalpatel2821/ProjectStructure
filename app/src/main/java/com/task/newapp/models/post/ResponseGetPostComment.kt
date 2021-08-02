package com.task.newapp.models.post

import com.google.gson.annotations.SerializedName

data class ResponseGetPostComment(
    @SerializedName("data")
    val data: Data?,
    @SerializedName("success")
    val success: Int = 0,
    @SerializedName("total_comment")
    val totalComment: Int = 0,
    @SerializedName("message")
    val message: String = ""
) {
    data class Data(
        @SerializedName("comment_text")
        val commentText: String? = "",
        @SerializedName("is_deleted")
        val isDeleted: Int = 0,
        @SerializedName("post_id")
        val postId: Int = 0,
        @SerializedName("updated_at")
        val updatedAt: String = "",
        @SerializedName("user_id")
        val userId: Int = 0,
        @SerializedName("is_comment_reply")
        val isCommentReply: Int = 0,
        @SerializedName("created_at")
        val createdAt: String = "",
        @SerializedName("id")
        val id: Int = 0,
        @SerializedName("comment_id")
        val commentId: Int = 0,
        @SerializedName("status")
        val status: String = ""
    )
}