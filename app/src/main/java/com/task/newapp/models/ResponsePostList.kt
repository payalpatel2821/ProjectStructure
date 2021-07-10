package com.task.newapp.models


import com.google.gson.annotations.SerializedName

data class ResponsePostList(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("message")
    val message: String,
    @SerializedName("success")
    val success: Int
) {
    data class Data(
        @SerializedName("comments_count")
        val commentsCount: Int,
        @SerializedName("contents")
        val contents: List<Content>,
        @SerializedName("flag")
        val flag: String,
        @SerializedName("getlikes_count")
        val getlikesCount: Int,
        @SerializedName("id")
        val id: Int,
        @SerializedName("is_post_for_page")
        val isPostForPage: Int,
        @SerializedName("like")
        val like: String,
        @SerializedName("page_id")
        val pageId: Any,
        @SerializedName("type")
        val type: String,
        @SerializedName("user_id")
        val userId: Int,
        @SerializedName("view_count")
        val viewCount: String,
        @SerializedName("you_like_post")
        val youLikePost: Int
    ) {
        data class Content(
            @SerializedName("alignment")
            val alignment: Any,
            @SerializedName("background_type")
            val backgroundType: Any,
            @SerializedName("caption")
            val caption: String,
            @SerializedName("color")
            val color: Any,
            @SerializedName("content")
            val content: String,
            @SerializedName("created_at")
            val createdAt: String,
            @SerializedName("font_style")
            val fontStyle: Any,
            @SerializedName("height")
            val height: Int,
            @SerializedName("id")
            val id: Int,
            @SerializedName("is_bold")
            val isBold: Any,
            @SerializedName("is_italic")
            val isItalic: Any,
            @SerializedName("is_underline")
            val isUnderline: Any,
            @SerializedName("likes")
            val likes: Int,
            @SerializedName("pattern_id")
            val patternId: Any,
            @SerializedName("post_id")
            val postId: Int,
            @SerializedName("size")
            val size: Any,
            @SerializedName("thought_type")
            val thoughtType: Any,
            @SerializedName("thumb")
            val thumb: String,
            @SerializedName("type")
            val type: String,
            @SerializedName("updated_at")
            val updatedAt: String,
            @SerializedName("width")
            val width: Int
        )
    }
}