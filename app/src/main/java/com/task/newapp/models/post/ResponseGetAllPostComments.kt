package com.task.newapp.models.post


import com.google.gson.annotations.SerializedName

data class ResponseGetAllPostComments(
    @SerializedName("data")
    val `data`: List<AllPostCommentData>,
    @SerializedName("message")
    val message: String,
    @SerializedName("success")
    val success: Int
) {
    data class AllPostCommentData(
        @SerializedName("comment_id")
        val commentId: Int = 0,
        @SerializedName("comment_reply")
        var commentReply: CommentReply? = null,
        @SerializedName("comment_text")
        val commentText: String = "",
        @SerializedName("comments_count")
        val commentsCount: Int = 0,
        @SerializedName("created_at")
        val createdAt: String = "",
        @SerializedName("id")
        val id: Int = 0,
        @SerializedName("is_comment_reply")
        val isCommentReply: Int = 0,
        @SerializedName("is_deleted")
        val isDeleted: Int = 0,
        @SerializedName("post_id")
        val postId: Int = 0,
        @SerializedName("status")
        val status: String = "",
        @SerializedName("updated_at")
        val updatedAt: String = "",
        @SerializedName("user")
        val user: CommentUser? = null,
        @SerializedName("user_id")
        val userId: Int = 0
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

        data class CommentUser(
            @SerializedName("about")
            val about: String = "",
            @SerializedName("account_id")
            val accountId: String = "",
            @SerializedName("age")
            val age: Any? = null,
            @SerializedName("anniversary")
            val anniversary: Any? = null,
            @SerializedName("app_version")
            val appVersion: String = "",
            @SerializedName("blood_gp")
            val bloodGp: String = "",
            @SerializedName("created_at")
            val createdAt: String = "",
            @SerializedName("date_of_birth")
            val dateOfBirth: Any? = null,
            @SerializedName("del_request")
            val delRequest: Int = 0,
            @SerializedName("delete_reason")
            val deleteReason: Any? = null,
            @SerializedName("device_arn")
            val deviceArn: String = "",
            @SerializedName("device_token")
            val deviceToken: String = "",
            @SerializedName("device_type")
            val deviceType: String = "",
            @SerializedName("email")
            val email: String = "",
            @SerializedName("expired_at")
            val expiredAt: String = "",
            @SerializedName("first_name")
            val firstName: String = "",
            @SerializedName("flag")
            val flag: String = "",
            @SerializedName("forgot_token")
            val forgotToken: String = "",
            @SerializedName("gender")
            val gender: String = "",
            @SerializedName("id")
            val id: Int = 0,
            @SerializedName("is_blocked_by_admin")
            val isBlockedByAdmin: Int = 0,
            @SerializedName("is_deleted")
            val isDeleted: Int = 0,
            @SerializedName("is_last_seen")
            val isLastSeen: Int = 0,
            @SerializedName("is_login")
            val isLogin: Int = 0,
            @SerializedName("is_online")
            val isOnline: Int = 0,
            @SerializedName("is_reported_by_admin")
            val isReportedByAdmin: Int = 0,
            @SerializedName("is_verified_email")
            val isVerifiedEmail: Int = 0,
            @SerializedName("last_logout_time")
            val lastLogoutTime: Any? = null,
            @SerializedName("last_name")
            val lastName: String = "",
            @SerializedName("last_seen_time")
            val lastSeenTime: String = "",
            @SerializedName("latitude")
            val latitude: String = "",
            @SerializedName("location")
            val location: Any? = null,
            @SerializedName("longitude")
            val longitude: String = "",
            @SerializedName("mobile")
            val mobile: String = "",
            @SerializedName("profession")
            val profession: Any? = null,
            @SerializedName("profile_color")
            val profileColor: Any? = null,
            @SerializedName("profile_image")
            val profileImage: String = "",
            @SerializedName("profile_views")
            val profileViews: Int = 0,
            @SerializedName("request_date")
            val requestDate: String = "",
            @SerializedName("secret_password")
            val secretPassword: String = "",
            @SerializedName("status")
            val status: String = "",
            @SerializedName("u_name")
            val uName: String = "",
            @SerializedName("updated_at")
            val updatedAt: String = ""
        )
    }
}