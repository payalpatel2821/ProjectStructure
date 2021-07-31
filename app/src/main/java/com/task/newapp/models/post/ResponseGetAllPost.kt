package com.task.newapp.models.post


import com.google.gson.annotations.SerializedName

data class ResponseGetAllPost(
    @SerializedName("count")
    val count: Count,
    @SerializedName("data")
    val `data`: List<All_Post_Data>,
    @SerializedName("message")
    val message: String,
    @SerializedName("success")
    val success: Int
) {
    data class Count(
        @SerializedName("get_all_follwers")
        val getAllFollwers: Int,
        @SerializedName("get_all_follwing")
        val getAllFollwing: Int,
        @SerializedName("get_all_friends")
        val getAllFriends: Int
    )

    data class All_Post_Data(
        @SerializedName("latest_comment")
        var latest_comment: ResponsePostComment.Data,
        @SerializedName("comments_count")
        var commentsCount: Int,
        @SerializedName("contents")
        val contents: List<Content>,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("description")
        val description: Any,
        @SerializedName("end_time")
        val endTime: Any,
        @SerializedName("flag")
        val flag: String,
        @SerializedName("group_id")
        val groupId: Any,
        @SerializedName("hastags")
        val hastags: Any,
        @SerializedName("id")
        val id: Int,
        @SerializedName("is_added_from_story")
        val isAddedFromStory: Int,
        @SerializedName("is_deleted")
        val isDeleted: Int,
        @SerializedName("is_like")
        var isLike: Int,
        @SerializedName("is_location")
        val isLocation: Int,
        @SerializedName("is_post_for_page")
        val isPostForPage: Int,
        @SerializedName("is_save")
        var isSave: Int,
        @SerializedName("is_shared_group")
        val isSharedGroup: Int,
        @SerializedName("is_shared_to_page")
        val isSharedToPage: Int,
        @SerializedName("latitude")
        val latitude: Any,
        @SerializedName("like")
        val like: String,
        @SerializedName("likes_count")
        var likesCount: Int,
        @SerializedName("location")
        val location: String,
        @SerializedName("longitude")
        val longitude: Any,
        @SerializedName("page")
        val page: List<Page>,
        @SerializedName("page_id")
        val pageId: Any,
        @SerializedName("post_website")
        val postWebsite: Any,
        @SerializedName("privacy")
        val privacy: String,
        @SerializedName("see_future_post")
        val seeFuturePost: String,
        @SerializedName("shares")
        val shares: String,
        @SerializedName("status")
        val status: String,
        @SerializedName("story_id")
        val storyId: Int,
        @SerializedName("title")
        val title: String,
        @SerializedName("turn_off_comment")
        val turnOffComment: Int,
        @SerializedName("type")
        val type: String,
        @SerializedName("updated_at")
        val updatedAt: String,
        @SerializedName("user")
        val user: User,
        @SerializedName("user_id")
        val userId: Int,
        @SerializedName("view_count")
        val viewCount: String,
        @SerializedName("views")
        val views: List<Any>,
        @SerializedName("tagged")
        val tagged: List<Tagged>,
    ) {
        data class Content(
            @SerializedName("alignment")
            val alignment: String,
            @SerializedName("background_type")
            val backgroundType: String,
            @SerializedName("caption")
            val caption: String,
            @SerializedName("color")
            val color: String,
            @SerializedName("font_color")
            val fontColor: String?,
            @SerializedName("content")
            val content: String,
            @SerializedName("created_at")
            val createdAt: String,
            @SerializedName("font_style")
            val fontStyle: String,
            @SerializedName("height")
            val height: Int,
            @SerializedName("id")
            val id: Int,
            @SerializedName("is_bold")
            val isBold: Int,
            @SerializedName("is_italic")
            val isItalic: Int,
            @SerializedName("is_underline")
            val isUnderline: Int,
            @SerializedName("likes")
            val likes: Int,
            @SerializedName("pattern_id")
            val patternId: String,
            @SerializedName("post_id")
            val postId: Int,
            @SerializedName("size")
            val size: Any,
            @SerializedName("thought_type")
            val thoughtType: String,
            @SerializedName("thumb")
            val thumb: String,
            @SerializedName("type")
            val type: String,
            @SerializedName("updated_at")
            val updatedAt: String,
            @SerializedName("width")
            val width: Int
        )

        data class Tagged(
            @SerializedName("id") val id: Int,
            @SerializedName("first_name") val first_name: String?,
            @SerializedName("last_name") val last_name: String?,
            @SerializedName("profile_image") val profile_image: String
        )

        data class Latest_comment(
            @SerializedName("id") val id: Int,
            @SerializedName("post_id") val post_id: Int,
            @SerializedName("user_id") val user_id: Int,
            @SerializedName("is_comment_reply") val is_comment_reply: Int,
            @SerializedName("comment_id") val comment_id: String,
            @SerializedName("comment_text") val comment_text: String,
            @SerializedName("status") val status: String,
            @SerializedName("is_deleted") val is_deleted: Int,
            @SerializedName("created_at") val created_at: String,
            @SerializedName("updated_at") val updated_at: String
        )

//        data class Comments(
//            @SerializedName("id") val id: Int,
//            @SerializedName("post_id") val post_id: Int,
//            @SerializedName("user_id") val user_id: Int,
//            @SerializedName("is_comment_reply") val is_comment_reply: Int,
//            @SerializedName("comment_id") val comment_id: Int,
//            @SerializedName("comment_text") var comment_text: String,
//            @SerializedName("status") val status: String,
//            @SerializedName("is_deleted") val is_deleted: Int,
//            @SerializedName("created_at") val created_at: String,
//            @SerializedName("updated_at") val updated_at: String
//        )

        data class Page(
            @SerializedName("flag")
            val flag: String,
            @SerializedName("icon")
            val icon: String,
            @SerializedName("id")
            val id: Int,
            @SerializedName("name")
            val name: String
        )

        data class User(
            @SerializedName("first_name")
            val firstName: String?,
            @SerializedName("flag")
            val flag: String,
            @SerializedName("id")
            val id: Int,
            @SerializedName("last_name")
            val lastName: String?,
            @SerializedName("profile_image")
            val profileImage: String
        )
    }
}