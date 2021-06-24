package com.task.newapp.api

import com.task.newapp.models.CommonResponse
import com.task.newapp.models.LoginResponse
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiHelper {

    @POST(login_url)
    fun doLogin(@Body hashMap: HashMap<String, Any>): Observable<LoginResponse>

    @POST(reset_password_url)
    fun doResetPassword(@Body hashMap: HashMap<String, Any>): Observable<CommonResponse>
}