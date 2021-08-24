package com.example.tawk.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.tawk.data.db.entity.User
import com.example.tawk.data.repositories.HomeRepository


class HomeViewModel(private val repository: HomeRepository) : ViewModel() {

    @ExperimentalPagingApi
    val users = repository.getAllUsersFlowDb().cachedIn(viewModelScope)

    fun searchUsers(query: String): LiveData<PagingData<User>> {
        return repository.getSearchUsers(query).cachedIn(viewModelScope)
    }
}