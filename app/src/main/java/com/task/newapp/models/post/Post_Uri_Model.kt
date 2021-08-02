package com.task.newapp.models.post

import com.google.gson.annotations.SerializedName

data class Post_Uri_Model(
    @SerializedName("file_path") var file_path: String,
    @SerializedName("type") var type: String
)