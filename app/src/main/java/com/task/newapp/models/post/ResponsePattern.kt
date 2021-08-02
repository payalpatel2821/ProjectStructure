package com.task.newapp.models.post


import com.google.gson.annotations.SerializedName

data class ResponsePattern(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("message")
    val message: String,
    @SerializedName("success")
    val success: Int
) {
    data class Data(
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("id")
        val id: Int,
        @SerializedName("name")
        val name: String,
        @SerializedName("updated_at")
        val updatedAt: String
    )
}