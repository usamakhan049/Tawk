package com.example.tawk.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

inline fun <reified T : Activity> Context?.showActivity(bundle: Bundle? = null) {
    val intent = Intent(this, T::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
    bundle?.let {
        intent.putExtras(bundle)
    }
    this?.startActivity(intent)
}


fun Context.isNetworkAvailable(): Boolean {
    var isAvailable = false
    val connectivityManager =
        this.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    connectivityManager?.let {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            it.getNetworkCapabilities(connectivityManager.activeNetwork)?.apply {
                isAvailable = when {
                    hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    else -> false
                }
            }
        } else {
            connectivityManager?.activeNetworkInfo.also {
                return it != null && it.isConnected
            }
        }
        return isAvailable
    }
}

fun Context.registerConnectivityReceiver(connectivityCallback: ConnectivityCallback) {
    val cm = getSystemService(AppCompatActivity.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
        cm.registerDefaultNetworkCallback(connectivityCallback)
    }
}

fun Context.unRegisterConnectivityReceiver(connectivityCallback: ConnectivityCallback) {
    val cm = getSystemService(AppCompatActivity.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
        cm.unregisterNetworkCallback(connectivityCallback)
    }
}