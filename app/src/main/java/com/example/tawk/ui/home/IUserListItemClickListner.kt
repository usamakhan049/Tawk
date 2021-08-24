package com.example.tawk.ui.home

import com.example.tawk.data.db.entity.User

interface IUserListItemClickListner {
    fun onItemClick(user: User)
}