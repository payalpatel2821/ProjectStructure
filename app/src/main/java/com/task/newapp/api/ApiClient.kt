package com.task.newapp.api

import com.task.newapp.BuildConfig
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

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

            builder.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))

                .addInterceptor { chain ->
                    val newRequest = chain.request().newBuilder()
                        .header("content-type", "application/json")
                        .header("charset", "utf-8")
//                        .header(
//                            "",
//                            ""
//                        )
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