package com.task.newapp.models

import com.google.gson.annotations.SerializedName

data class PostSetting(
        @SerializedName("id")
    val id: Int,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("public_post_comments")
    val publicPostComments: String,
    @SerializedName("who_can_followe_me")
    val whoCanFolloweMe: String,
    @SerializedName("see_future_post")
    val seeFuturePost: String,
    @SerializedName("see_past_post")
    val seePastPost: String,
    @SerializedName("post_timeline")
    val postTimeline: String,
    @SerializedName("others_post_see_timeline")
    val othersPostSeeTimeline: String,
    @SerializedName("allow_others_to_share")
    val allowOthersToShare: String,
    @SerializedName("see_tagged_post_timeline")
    val seeTaggedPostTimeline: String,
    @SerializedName("who_can_add_whene_tagged_but_not_seen_by_you")
    val whoCanAddWhenTaggedButNotSeenByYou: String,
    @SerializedName("review_tags_before_appear")
    val reviewTagsBeforeAppear: String,
    @SerializedName("review_post_before_appear")
    val reviewPostBeforeAppear: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String
)