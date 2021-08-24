package com.example.tawk.ui.profile

import com.example.tawk.data.db.entity.User


interface INetworkErrorListner {
    fun onFailure(message : String)
}