package com.task.newapp.models


import com.google.gson.annotations.SerializedName

data class ResponseIsAppUser(
    @SerializedName("data")
    var `data`: List<Data>,
    @SerializedName("message")
    var message: String,
    @SerializedName("success")
    var success: Int
) {
    data class Data(
        @SerializedName("account_id")
        var accountId: String = "",
        @SerializedName("app_user_id")
        var appUserId: String = "",
        @SerializedName("is_app_user")
        var isAppUser: Int = 0,
        @SerializedName("email")
        var email: String = "",
        @SerializedName("first_name")
        var firstName: String = "",
        @SerializedName("fullname")
        var fullname: String = "",
        @SerializedName("flag")
        var flag: String = "",
        @SerializedName("id")
        var id: Int = 0,
        @SerializedName("is_visible")
        var isVisible: Int = 0,
        @SerializedName("last_name")
        var lastName: String = "",
        @SerializedName("mobile")
        var mobile: String = "",
        @SerializedName("number")
        var number: String = "",
        @SerializedName("profile_color")
        var profileColor: String = "",
        @SerializedName("profile_image")
        var profileImage: String = "",
        @SerializedName("about")
        var about: String = ""
    )
}