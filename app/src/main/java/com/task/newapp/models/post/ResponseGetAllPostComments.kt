package com.task.newapp.models.post


import com.google.gson.annotations.SerializedName
import com.task.newapp.models.post.ResponseGetAllPostCommentsOld.AllPostCommentData.*

data class ResponseGetAllPostComments(
    @SerializedName("data")
    val `data`: List<AllPostCommentData>,
    @SerializedName("message")
    val message: String,
    @SerializedName("success")
    val success: Int
) {
    data class AllPostCommentData(
        @SerializedName("id")
        val id: Int = 0,
        @SerializedName("post_id")
        val postId: Int = 0,
        @SerializedName("user_id")
        val userId: Int = 0,
        @SerializedName("is_comment_reply")
        val isCommentReply: Int = 0,
        @SerializedName("comment_id")
        val commentId: Int = 0,
        @SerializedName("main_comment_id")
        val mainCommentId: Int = 0,
        @SerializedName("comment_text")
        val commentText: String = "",
        @SerializedName("status")
        val status: String = "",
        @SerializedName("is_deleted")
        val isDeleted: Int = 0,
        @SerializedName("created_at")
        val createdAt: String = "",
        @SerializedName("updated_at")
        val updatedAt: String = "",
        @SerializedName("comments_count")
        val commentsCount: Int = 0,

        @SerializedName("user")
        val user: CommentUser? = null,

        @SerializedName("comment_reply")
        val commentReply: List<CommentReply> = ArrayList()
    ) {
        data class CommentReply(

            @SerializedName("id") val id: Int,
            @SerializedName("post_id") val post_id: Int,
            @SerializedName("user_id") val user_id: Int,
            @SerializedName("is_comment_reply") val is_comment_reply: Int,
            @SerializedName("comment_id") val comment_id: Int,
            @SerializedName("main_comment_id") val main_comment_id: Int,
            @SerializedName("comment_text") val comment_text: String,
            @SerializedName("status") val status: String,
            @SerializedName("is_deleted") val is_deleted: Int,
            @SerializedName("created_at") val created_at: String,
            @SerializedName("updated_at") val updated_at: String,
            @SerializedName("user") val user: User
        ) {
            data class User(
                @SerializedName("id") val id: Int,
                @SerializedName("first_name") val first_name: String,
                @SerializedName("last_name") val last_name: String,
                @SerializedName("account_id") val account_id: String,
                @SerializedName("profile_image") val profile_image: String,
                @SerializedName("profile_color") val profile_color: String,
                @SerializedName("is_visible") val is_visible: Int,
                @SerializedName("flag") val flag: String
            )
        }

        data class CommentUser(
            @SerializedName("id")
            val id: Int = 0,
            @SerializedName("first_name")
            val firstName: String? = "",
            @SerializedName("last_name")
            val lastName: String? = "",
            @SerializedName("account_id")
            val accountId: String = "",
            @SerializedName("profile_image")
            val profileImage: String = "",
            @SerializedName("profile_color")
            val profileColor: String = "",
            @SerializedName("is_visible")
            val isVisible: Int = 0
        )
    }
}