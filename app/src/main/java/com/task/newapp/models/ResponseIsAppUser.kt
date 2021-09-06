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
        var accountId: String,
        @SerializedName("email")
        var email: String,
        @SerializedName("first_name")
        var firstName: String,
        @SerializedName("flag")
        var flag: String,
        @SerializedName("id")
        var id: Int,
        @SerializedName("is_visible")
        var isVisible: Int,
        @SerializedName("last_name")
        var lastName: String,
        @SerializedName("mobile")
        var mobile: String,
        @SerializedName("profile_color")
        var profileColor: String,
        @SerializedName("profile_image")
        var profileImage: String
    )
}