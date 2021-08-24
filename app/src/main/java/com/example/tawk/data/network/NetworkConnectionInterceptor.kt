package com.example.tawk.data.network

import android.content.Context
import com.example.tawk.R
import com.example.tawk.util.NoInternetConnectionException
import com.example.tawk.util.isNetworkAvailable
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class NetworkConnectionInterceptor(
    private val context: Context
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        if (!context.isNetworkAvailable())
            throw NoInternetConnectionException(context.getString(R.string.internet_connection_error))
        try {
            return chain.proceed(chain.request())
        } catch (e: IOException) {
            throw NoInternetConnectionException(context.getString(R.string.internet_connection_error))
        }
    }
}