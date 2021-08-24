package com.example.tawk.util

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET

class ConnectivityCallback(var connectivityReceiverListener: ConnectivityReceiverListener) :
    ConnectivityManager.NetworkCallback() {

    override fun onCapabilitiesChanged(network: Network, capabilities: NetworkCapabilities) {
        val connected = capabilities.hasCapability(NET_CAPABILITY_INTERNET)
        connectivityReceiverListener.onNetworkConnectionChanged(connected)
    }

    override fun onLost(network: Network) {
        connectivityReceiverListener.onNetworkConnectionChanged(false)
    }

    interface ConnectivityReceiverListener {
        fun onNetworkConnectionChanged(isConnected: Boolean)
    }
}