package com.example.tawk.ui.profile

import android.R
import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.*
import com.example.tawk.data.db.entity.User
import com.example.tawk.data.repositories.ProfileRepository
import com.example.tawk.util.*


class ProfileViewModel(private val repository: ProfileRepository) : ViewModel() {

    var notes: MutableLiveData<String> = MutableLiveData("")
    var followers: MutableLiveData<String> = MutableLiveData("Followers: ")
    var followings: MutableLiveData<String> = MutableLiveData("Followings: ")
    var userData: MutableLiveData<String> = MutableLiveData("")
    var avatarUrl: MutableLiveData<String> = MutableLiveData("")
    var uid: Int? = null
    var networkListner: INetworkErrorListner? = null

    fun getUser(userName: String, uid: Int, n: String?) {
        this.uid = uid
        Coroutines.main {
            try {
                repository.getProfile(userName, uid, n).observeForever(Observer { it ->
                    it?.let {
                        updateData(it)
                    }
                })
            } catch (e: NoInternetConnectionException) {
                networkListner?.onFailure(e.message.toString())
            } catch (e: ApiException) {
                networkListner?.onFailure(e.message.toString())
            } catch (e: Exception) {
                networkListner?.onFailure(e.message.toString())
            }
        }

    }

    private fun updateData(user: User) {
        avatarUrl.value = user.avatar_url
        notes.value = user.notes
        followers.value = "Followers: ${user.followers ?: ""}"
        followings.value = "Followings: ${user.following ?: ""}"
        userData.value =
            "Name:  ${user.login ?: ""} \nCompany:  ${user.company ?: ""} \nBlog:  ${user.blog ?: ""}"
    }

    fun saveNotes(view: View) {
        if (uid != -1 && notes.value.isNullOrEmpty()) {
            view.context.toast("Please Insert some data")
        }
        uid?.let {
            Coroutines.io {
                repository.setUserNotes(it, notes.value ?: "")
            }
        }
    }

    companion object {
        @JvmStatic
        @BindingAdapter("bind:imageUrl")
        fun loadImage(view: ImageView, imageUrl: String?) {
            view.load(imageUrl, false, false)
        }
    }
}