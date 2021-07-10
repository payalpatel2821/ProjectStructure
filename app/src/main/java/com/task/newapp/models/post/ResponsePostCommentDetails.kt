package com.task.newapp.models.post


import com.google.gson.annotations.SerializedName

data class ResponsePostCommentDetails(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("message")
    val message: String,
    @SerializedName("success")
    val success: Int
) {
    data class Data(
        @SerializedName("comment_id")
        val commentId: Any,
        @SerializedName("comment_reply")
        val commentReply: CommentReply,
        @SerializedName("comment_text")
        val commentText: String,
        @SerializedName("comments_count")
        val commentsCount: Int,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("id")
        val id: Int,
        @SerializedName("is_comment_reply")
        val isCommentReply: Int,
        @SerializedName("is_deleted")
        val isDeleted: Int,
        @SerializedName("post_id")
        val postId: Int,
        @SerializedName("status")
        val status: String,
        @SerializedName("updated_at")
        val updatedAt: String,
        @SerializedName("user")
        val user: User,
        @SerializedName("user_id")
        val userId: Int
    ) {
        data class CommentReply(
            @SerializedName("comment_id")
            val commentId: Int,
            @SerializedName("comment_text")
            val commentText: String,
            @SerializedName("created_at")
            val createdAt: String,
            @SerializedName("id")
            val id: Int,
            @SerializedName("is_comment_reply")
            val isCommentReply: Int,
            @SerializedName("is_deleted")
            val isDeleted: Int,
            @SerializedName("post_id")
            val postId: Int,
            @SerializedName("status")
            val status: String,
            @SerializedName("updated_at")
            val updatedAt: String,
            @SerializedName("user")
            val user: User,
            @SerializedName("user_id")
            val userId: Int
        ) {
            data class User(
                @SerializedName("first_name")
                val firstName: String,
                @SerializedName("flag")
                val flag: String,
                @SerializedName("id")
                val id: Int,
                @SerializedName("last_name")
                val lastName: String,
                @SerializedName("profile_image")
                val profileImage: String
            )
        }

        data class User(
            @SerializedName("about")
            val about: String,
            @SerializedName("account_id")
            val accountId: String,
            @SerializedName("age")
            val age: Any,
            @SerializedName("anniversary")
            val anniversary: Any,
            @SerializedName("app_version")
            val appVersion: String,
            @SerializedName("blood_gp")
            val bloodGp: String,
            @SerializedName("created_at")
            val createdAt: String,
            @SerializedName("date_of_birth")
            val dateOfBirth: Any,
            @SerializedName("del_request")
            val delRequest: Int,
            @SerializedName("delete_reason")
            val deleteReason: Any,
            @SerializedName("device_arn")
            val deviceArn: String,
            @SerializedName("device_token")
            val deviceToken: String,
            @SerializedName("device_type")
            val deviceType: String,
            @SerializedName("email")
            val email: String,
            @SerializedName("expired_at")
            val expiredAt: String,
            @SerializedName("first_name")
            val firstName: String,
            @SerializedName("flag")
            val flag: String,
            @SerializedName("forgot_token")
            val forgotToken: String,
            @SerializedName("gender")
            val gender: String,
            @SerializedName("id")
            val id: Int,
            @SerializedName("is_blocked_by_admin")
            val isBlockedByAdmin: Int,
            @SerializedName("is_deleted")
            val isDeleted: Int,
            @SerializedName("is_last_seen")
            val isLastSeen: Int,
            @SerializedName("is_login")
            val isLogin: Int,
            @SerializedName("is_online")
            val isOnline: Int,
            @SerializedName("is_reported_by_admin")
            val isReportedByAdmin: Int,
            @SerializedName("is_verified_email")
            val isVerifiedEmail: Int,
            @SerializedName("last_logout_time")
            val lastLogoutTime: Any,
            @SerializedName("last_name")
            val lastName: String,
            @SerializedName("last_seen_time")
            val lastSeenTime: String,
            @SerializedName("latitude")
            val latitude: String,
            @SerializedName("location")
            val location: Any,
            @SerializedName("longitude")
            val longitude: String,
            @SerializedName("mobile")
            val mobile: String,
            @SerializedName("profession")
            val profession: Any,
            @SerializedName("profile_color")
            val profileColor: Any,
            @SerializedName("profile_image")
            val profileImage: String,
            @SerializedName("profile_views")
            val profileViews: Int,
            @SerializedName("request_date")
            val requestDate: String,
            @SerializedName("secret_password")
            val secretPassword: String,
            @SerializedName("status")
            val status: String,
            @SerializedName("u_name")
            val uName: String,
            @SerializedName("updated_at")
            val updatedAt: String
        )
    }
}