package com.task.newapp.models

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id")
    val id: Int,
    @SerializedName("account_id")
    val accountId: String,
    @SerializedName("u_name")
    val uName: String,
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("last_name")
    val lastName: String,
    @SerializedName("about")
    val about: String,
    @SerializedName("email")
    var email: String,
    @SerializedName("mobile")
    val mobile: String,
    @SerializedName("register_type")
    val registerType: String,
    @SerializedName("anniversary")
    val anniversary: String,
    @SerializedName("date_of_birth")
    val dateOfBirth: String,
    @SerializedName("age")
    val age: Any,
    @SerializedName("gender")
    val gender: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("is_verified_email")
    val isVerifiedEmail: Int,
    @SerializedName("location")
    val location: Any,
    @SerializedName("latitude")
    val latitude: String,
    @SerializedName("longitude")
    val longitude: String,
    @SerializedName("is_online")
    val isOnline: Int,
    @SerializedName("is_last_seen")
    val isLastSeen: Int,
    @SerializedName("last_seen_time")
    val lastSeenTime: String,
    @SerializedName("del_request")
    val delRequest: Int,
    @SerializedName("request_date")
    val requestDate: String,
    @SerializedName("profile_color")
    val profileColor: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("is_deleted")
    val isDeleted: Int,
    @SerializedName("delete_reason")
    val deleteReason: String,
    @SerializedName("is_blocked_by_admin")
    val isBlockedByAdmin: Int,
    @SerializedName("is_reported_by_admin")
    val isReportedByAdmin: Int,
    @SerializedName("forgot_token")
    val forgotToken: String,
    @SerializedName("expired_at")
    val expiredAt: String,
    @SerializedName("profile_views")
    val profileViews: Int,
    @SerializedName("profile_image")
    var profileImage: String = "",
    @SerializedName("device_arn")
    val deviceArn: String,
    @SerializedName("device_token")
    val deviceToken: String,
    @SerializedName("device_type")
    val deviceType: String,
    @SerializedName("profession")
    val profession: Any,
    @SerializedName("blood_gp")
    val bloodGp: String,
    @SerializedName("secret_password")
    val secretPassword: Any,
    @SerializedName("is_login")
    val isLogin: Int,
    @SerializedName("last_logout_time")
    val lastLogoutTime: String,
    @SerializedName("app_version")
    val appVersion: String,
    @SerializedName("flag")
    val flag: String
)