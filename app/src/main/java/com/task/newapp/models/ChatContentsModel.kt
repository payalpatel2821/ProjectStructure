package com.task.newapp.models

import com.google.gson.annotations.SerializedName

data class ChatContentsModel(
    @SerializedName("id")
    var id : Int,
    @SerializedName("chat_id")
    var chat_id : Int,
    @SerializedName("content")
    var content : String,
    @SerializedName("type")
    var type : String,
    @SerializedName("caption")
    var caption : String,
    @SerializedName("size")
    var size : Double,
    @SerializedName("delete_for")
    var delete_for : String,
    @SerializedName("created_at")
    var created_at : String,
    @SerializedName("updated_at")
    var updated_at : String,
    @SerializedName("local_path")
    var local_path : String,
    @SerializedName("is_star")
    var is_star : Int
)
