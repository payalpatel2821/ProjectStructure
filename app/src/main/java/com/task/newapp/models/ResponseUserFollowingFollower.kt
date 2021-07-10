package com.task.newapp.models


import com.google.gson.annotations.SerializedName

data class ResponseUserFollowingFollower(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("message")
    val message: String,
    @SerializedName("success")
    val success: Int
) {
    data class Data(
        @SerializedName("about")
        val about: Any,
        @SerializedName("account_id")
        val accountId: String,
        @SerializedName("age")
        val age: String,
        @SerializedName("date_of_birth")
        val dateOfBirth: String,
        @SerializedName("del_request")
        val delRequest: Int,
        @SerializedName("email")
        val email: String,
        @SerializedName("expired_at")
        val expiredAt: String,
        @SerializedName("first_name")
        val firstName: String,
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
        @SerializedName("is_follow")
        var isFollow: Int,
        @SerializedName("is_last_seen")
        val isLastSeen: Int,
        @SerializedName("is_online")
        val isOnline: Int,
        @SerializedName("is_reported_by_admin")
        val isReportedByAdmin: Int,
        @SerializedName("is_verified_email")
        val isVerifiedEmail: Int,
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
        val mobile: Any,
        @SerializedName("profile_image")
        val profileImage: String,
        @SerializedName("profile_views")
        val profileViews: Int,
        @SerializedName("request_date")
        val requestDate: String,
        @SerializedName("status")
        val status: String,
        @SerializedName("view_date")
        val view_date: String
    )
}