package com.task.newapp.models


import com.google.gson.annotations.SerializedName

data class ResponseMyContact(
    @SerializedName("data")
    var `data`: List<Data>,
    @SerializedName("message")
    var message: String,
    @SerializedName("success")
    var success: Int
) {
    data class Data(
        @SerializedName("app_user_id")
        var appUserId: Int,
        @SerializedName("created_at")
        var createdAt: String,
        @SerializedName("email")
        var email: String,
        @SerializedName("fullname")
        var fullName: String,
        @SerializedName("id")
        var id: Int,
        @SerializedName("is_app_user")
        var isAppUser: Int,
        @SerializedName("number")
        var number: String,
        @SerializedName("photo_uri")
        var photoUri: String,
        @SerializedName("profile_color")
        var profileColor: String,
        @SerializedName("profile_image")
        var profileImage: String,
        @SerializedName("register_type")
        var registerType: String,
        @SerializedName("updated_at")
        var updatedAt: String,
        @SerializedName("user_id")
        var userId: Int
    )
}