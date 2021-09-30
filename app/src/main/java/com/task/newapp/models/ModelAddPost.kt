package com.task.newapp.models

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Part

data class ModelAddPost(
    var turn_off_comment: String,
    var hastags: String,
    var title: String,
    var type: String,
    var latitude: String,
    var longitude: String,
    var location: String,
    var user_tags: String,

    //var mention: String,
    var captionarray: ArrayList<MultipartBody.Part>,
    var typearray: ArrayList<MultipartBody.Part>,
    var thumbarray: ArrayList<MultipartBody.Part>,
    var imagearray: ArrayList<MultipartBody.Part>

)


//@Part("turn_off_comment") turn_off_comment: RequestBody,
//@Part("hastags") hastags: RequestBody,
//@Part("title") title: RequestBody,
//@Part("type") type: RequestBody,
//@Part("latitude") latitude: RequestBody,
//@Part("longitude") longitude: RequestBody,
//@Part("location") location: RequestBody,
//@Part("user_tags") user_tags: RequestBody,
//
////        @Part("mention") mention: RequestBody,  //Add New
//@Body cartList: List<ModelMention>,
//
//@Part captionarray: List<MultipartBody.Part>,
//@Part typearray: List<MultipartBody.Part>,
//@Part thumbarray: List<MultipartBody.Part>,
//@Part imagevideoarray: List<MultipartBody.Part>