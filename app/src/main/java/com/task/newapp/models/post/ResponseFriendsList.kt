package com.task.newapp.models.post


import com.google.gson.annotations.SerializedName

data class ResponseFriendsList(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("message")
    val message: String,
    @SerializedName("success")
    val success: Int
) {
    data class Data(
        @SerializedName("about")
        val about: String,
        @SerializedName("account_id")
        val accountId: String,
        @SerializedName("email")
        val email: String,
        @SerializedName("first_name")
        val firstName: String,
        @SerializedName("flag")
        val flag: String,
        @SerializedName("id")
        val id: Int,
        @SerializedName("last_name")
        val lastName: String?,
        @SerializedName("mobile")
        val mobile: String,
        @SerializedName("profile_image")
        val profileImage: String,
        @SerializedName("profile_color")
        val profileColor: String,

        //Add New
        var isSelected: Boolean = false
    )
}