package com.task.newapp.api

import com.appizona.yehiahd.fastsave.FastSave
import com.task.newapp.BuildConfig
import com.task.newapp.utils.Constants
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApiClient {
    companion object {
        private var mRetrofit: Retrofit? = null

        fun create(): ApiHelper {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            val rxAdapter = RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io())

//            val client = OkHttpClient.Builder().addInterceptor(interceptor)
//                .build()

            val builder = OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
            builder.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))

                .addInterceptor { chain ->
                    val newRequest = chain.request().newBuilder()
                        .header("content-type", "application/json")
                        .header("charset", "utf-8")
                        .header(
                            "Authorization",
                            if (FastSave.getInstance().getString(Constants.prefToken, "") != "") {
                                "bearer " + FastSave.getInstance().getString(Constants.prefToken, "")
                            } else {
                                ""
                            }
                        )
                        .build()
                    chain.proceed(newRequest)
                }

            if (mRetrofit == null) {
                mRetrofit = Retrofit.Builder().baseUrl(BuildConfig.server_url)
                    .client(builder.build()).addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(rxAdapter).build()
            }
            return mRetrofit!!.create(ApiHelper::class.java)
        }
    }
}