package com.example.tawk.data.network

import com.example.tawk.data.db.entity.User
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MyApi {

    @GET("users")
    suspend fun getUsers(
        @Query("since") page: Int
    ): List<User>

    @GET("users/{username}")
    suspend fun getUser(
        @Path(value = "username", encoded = true) username: String
    ): Response<User>

    companion object {
        operator fun invoke(networkConnectionInterceptor: NetworkConnectionInterceptor): MyApi {
            val dispatcher = Dispatcher()
            dispatcher.setMaxRequests(1)

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(networkConnectionInterceptor)
                .dispatcher(dispatcher)
                .build()

            return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl("https://api.github.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(MyApi::class.java)
        }
    }
}