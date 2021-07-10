package com.task.newapp.api

import com.task.newapp.models.*
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

}