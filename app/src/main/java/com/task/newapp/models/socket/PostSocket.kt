package com.task.newapp.models.socket

import com.google.gson.annotations.SerializedName
import com.task.newapp.models.post.ResponseGetAllPostComments
import com.task.newapp.models.post.ResponseGetAllPostComments.AllPostCommentData
import com.task.newapp.models.post.ResponseGetAllPostComments.AllPostCommentData.CommentReply

data class PostSocket(
    @SerializedName("user_id") var user_id: Int,
    @SerializedName("post_id") var post_id: Int,
    var isDeleteComment: Boolean = false,
    var commentModel: AllPostCommentData? = AllPostCommentData(),
    var mainCommentId: Int? = 0,
    var replyCommentId: Int? = 0,
    var isReplyDelete: Boolean? = false,
    var commentReplyModel: CommentReply? = CommentReply(),
    var totalComments: Int? = 0
)