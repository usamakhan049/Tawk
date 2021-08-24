package com.example.tawk.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tawk.data.repositories.HomeRepository
import com.example.tawk.data.repositories.ProfileRepository

class ProfileViewModelFactory(val repository: ProfileRepository) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ProfileViewModel(repository) as T
    }
}