package com.task.newapp.models

import com.google.gson.annotations.SerializedName

data class ChatDocumentModel(

    @SerializedName("id")
    val id: Int,
    @SerializedName("chat_id")
    val chat_id: Int,
    @SerializedName("document")
    var document: String,
    @SerializedName("title")
    var title: String,
    @SerializedName("type")
    var type: String,
    @SerializedName("no_of_pages")
    var no_of_pages: Int,
    @SerializedName("size")
    var size: Double,
    @SerializedName("delete_for")
    var delete_for: String,
    @SerializedName("created_at")
    var created_at: String,
    @SerializedName("updated_at")
    var updated_at: String,
    @SerializedName("local_path")
    var local_path: String,
    @SerializedName("flag")
    var flag: String
)
