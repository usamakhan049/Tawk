package com.example.tawk.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tawk.data.db.AppDatabase
import com.example.tawk.data.db.entity.User
import com.example.tawk.data.network.MyApi
import com.example.tawk.data.network.SafeApiRequest
import com.example.tawk.util.Coroutines
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class ProfileRepository(
    private val api: MyApi,
    private val db: AppDatabase
) : SafeApiRequest() {

    private val userProfile = MutableLiveData<User>()

    init {
        userProfile.observeForever {
            setUserProfile(it)
        }
    }

    suspend fun getProfile(userName: String, uid: Int, notes: String?): LiveData<User> {
        return withContext(Dispatchers.IO) {
            fetchProfile(userName, uid, notes)
            db.getUserDao().getProfileData(uid)
        }
    }

    fun setUserNotes(uid: Int, notes: String) {
        Coroutines.io {
            db.getUserDao().setProfileData(uid, notes)
        }
    }

    private suspend fun fetchProfile(userName: String, uid: Int, notes: String?) {
        val response = apiRequest { api.getUser(userName) }
        response.notes = notes
        response.uid = uid
        userProfile.postValue(response)
    }

    private fun setUserProfile(user: User) {
        Coroutines.io {
            db.getUserDao().setProfile(user)
        }
    }
}