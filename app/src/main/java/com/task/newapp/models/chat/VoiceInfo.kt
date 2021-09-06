package com.task.newapp.models.chat

class VoiceInfo(
    val voicedata: ByteArray,
    val localurl: String,
    val duration: Double,
    val filename: String,
    val size: Double
) {
}