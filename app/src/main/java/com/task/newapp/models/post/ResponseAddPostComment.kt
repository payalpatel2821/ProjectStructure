package com.task.newapp.models.post

import com.google.gson.annotations.SerializedName

data class ResponseAddPostComment(
    @SerializedName("success")
    var success: Int,
    @SerializedName("message")
    var message: String,
    @SerializedName("data")
    var data: Add_Comment_Data?
) {
    data class Add_Comment_Data(
        @SerializedName("user_id")
        var user_id: Int = 0,

        @SerializedName("post_id")
        var post_id: String? = null,

        @SerializedName("comment_text")
        var comment_text: String? = null,

        @SerializedName("status")
        var status: String? = null,

        @SerializedName("updated_at")
        var updated_at: String? = null,

        @SerializedName("created_at")
        var created_at: String? = null,

        @SerializedName("first_name")
        var first_name: String? = null,

        @SerializedName("last_name")
        var last_name: String? = null,

        @SerializedName("total_comment")
        var total_comment: Int = 0,

        @SerializedName("id")
        var id: Int = 0
    )
}