package com.task.newapp.api

import com.task.newapp.models.*
import com.task.newapp.models.post.ResponseGetAllPost
import com.task.newapp.models.post.ResponseGetPostLikeUnlike
import com.task.newapp.models.post.ResponsePostComment
import com.task.newapp.models.post.ResponsePostCommentDetails
import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiHelper {

    @POST(login_url)
    fun doLogin(@Body hashMap: HashMap<String, Any>): Observable<LoginResponse>

    @POST(reset_password_url)
    fun doResetPassword(@Body hashMap: HashMap<String, Any>): Observable<CommonResponse>

    @POST(send_code_forgot_url)
    fun sendCodeForgotPass(@Body hashMap: HashMap<String, Any>): Observable<ResponseSendCode>

    @POST(verify_OTP_url)
    fun verifyOTPForgotPass(@Body hashMap: HashMap<String, Any>): Observable<ResponseVerifyOTP>

    @POST(register_url)
    fun doRegister(@Body data: RequestBody): Observable<ResponseRegister>

    @GET(check_username_url)
    fun checkUsername(@Path("user") user: String): Observable<ResponseVerifyOTP>

    @GET(get_username_url)
    fun getUsername(@Path("user") user: String): Observable<ResponseGetUsername>

    @POST(send_code_normal_url)
    fun sendCodeNormalUrl(@Body hashMap: HashMap<String, Any>): Observable<ResponseSendCode>

    @POST(verify_OTP_normal_url)
    fun verifyOTPNormal(@Body hashMap: HashMap<String, Any>): Observable<ResponseVerifyOTP>

    @GET(get_unread_messages)
    fun getUnreadMessage(): Observable<ResponseGetUnreadMessage>

    @POST(hook_chat)
    fun hookChat(@Body hashMap: HashMap<String, Any>): Observable<CommonResponse>

    @POST(archive_chat)
    fun archiveChat(@Body hashMap: HashMap<String, Any>): Observable<CommonResponse>

    @POST(delete_chat)
    fun deleteChat(@Body hashMap: HashMap<String, Any>): Observable<CommonResponse>

    @GET(get_all_posts)
    fun get_all_posts(@Path("limit") limit: Int, @Path("offset") offset: Int): Observable<ResponseGetAllPost>

    @POST(add_postlikeunlike)
    fun add_postlikeunlike(@Body hashMap: HashMap<String, Any>): Observable<ResponseGetPostLikeUnlike>

    @POST(postSaveUnsave)
    fun postSaveUnsave(@Body hashMap: HashMap<String, Any>): Observable<CommonResponse>

    @POST(allpostcomment)
    fun allpostcomment(@Body hashMap: HashMap<String, Any>): Observable<ResponsePostCommentDetails>

    @POST(post_comment)
    fun post_comment(@Body hashMap: HashMap<String, Any>): Observable<ResponsePostComment>
}