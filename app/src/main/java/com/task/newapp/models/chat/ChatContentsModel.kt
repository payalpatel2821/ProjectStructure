package com.task.newapp.models.chat

import com.google.gson.annotations.SerializedName

data class ChatContentsModel(
    @SerializedName("id")
    var id: Int? = null,
    @SerializedName("chat_id")
    var chatId: Int? = null,
    @SerializedName("content")
    var content: String? = null,
    @SerializedName("type")
    var type: String? = null,
    @SerializedName("caption")
    var caption: String? = null,
    @SerializedName("size")
    var size: Double? = null,
    @SerializedName("duration")
    var duration: Double? = null,
    @SerializedName("title")
    var title: String? = null,
    @SerializedName("number")
    var number: String? = null,
    @SerializedName("name")
    var name: String? = null,
    @SerializedName("email")
    var email: String? = null,
    @SerializedName("profile_image")
    var profileImage: String? = "",
    @SerializedName("location")
    var location: String? = "",
    @SerializedName("latitude")
    var latitude: Double? = null,
    @SerializedName("longitude")
    var longitude: Double? = null,
    @SerializedName("end_time")
    var endTime: String? = null,
    @SerializedName("sharing_time")
    var sharingTime: String? = null,
    @SerializedName("location_type")
    var locationType: String? = null,
    @SerializedName("delete_for")
    var deleteFor: String? = null,
    @SerializedName("created_at")
    var createdAt: String? = null,
    @SerializedName("updated_at")
    var updatedAt: String? = null,
    @SerializedName("local_path")
    var localPath: String? = null
    /*  @SerializedName("id")
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
      var is_star : Int*/
)
