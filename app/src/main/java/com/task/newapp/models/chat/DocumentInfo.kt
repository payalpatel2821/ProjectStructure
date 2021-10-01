package com.task.newapp.models.chat

class DocumentInfo(
    var chatType: String,
    var documentType: String,
    var data: ByteArray,
    var localUrl: String,
    var duration: Double,
    var fileName: String,
    var size: Double,
    var numberOfPage: Int
) {
}