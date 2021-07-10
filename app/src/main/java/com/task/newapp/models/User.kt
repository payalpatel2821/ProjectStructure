package com.task.newapp.models

import com.google.gson.annotations.SerializedName

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
    val bloodGp: Any,
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
    val expiredAt: Any,
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("flag")
    val flag: String,
    @SerializedName("forgot_token")
    val forgotToken: Any,
    @SerializedName("gender")
    val gender: Any,
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
    val mobile: Any,
    @SerializedName("profession")
    val profession: Any,
    @SerializedName("profile_image")
    val profileImage: String,
    @SerializedName("profile_color")
    val profileColor: String,
    @SerializedName("profile_views")
    val profileViews: Int,
    @SerializedName("request_date")
    val requestDate: String,
    @SerializedName("secret_password")
    val secretPassword: Any,
    @SerializedName("status")
    val status: String,
    @SerializedName("u_name")
    val uName: String,
    @SerializedName("updated_at")
    val updatedAt: String
)