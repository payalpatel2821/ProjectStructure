package com.task.newapp.interfaces

interface OnSocketEventsListener {
    fun onOnlineOfflineSocketEvent(userId: Int, status: Boolean)
    fun onPostLikeDislikeEvent()

}