package com.task.newapp.utils

interface OnCommentItemClickListener {
    fun onDeleteCommentClick(positionMain: Int, positionSub: Int)
    fun onDeleteCommentReplyClick(positionMain: Int, positionSub: Int)
    fun onCommentReplyClick(positionMain: Int, positionSub: Int, commentId: Int, replyText: String, mainCommentId: Int)
}