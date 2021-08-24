package com.example.tawk.ui.profile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.tawk.R
import com.example.tawk.databinding.ActivityProfileBinding
import com.example.tawk.util.ConnectivityCallback
import com.example.tawk.util.Constants.KEY_NOTES
import com.example.tawk.util.Constants.KEY_UID
import com.example.tawk.util.Constants.KEY_USERNAME
import com.example.tawk.util.registerConnectivityReceiver
import com.example.tawk.util.toast
import com.example.tawk.util.unRegisterConnectivityReceiver
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class ProfileActivity : AppCompatActivity(), KodeinAware, INetworkErrorListner,
    ConnectivityCallback.ConnectivityReceiverListener {

    override val kodein by kodein()
    private lateinit var binding: ActivityProfileBinding
    private val factory: ProfileViewModelFactory by instance()
    private lateinit var viewModel: ProfileViewModel
    private var isConnectionChange = false
    private var connectivityCallback: ConnectivityCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile)
        viewModel = ViewModelProvider(this, factory).get(ProfileViewModel::class.java)
        viewModel.networkListner = this
        binding.viewModel = viewModel
        binding.setLifecycleOwner(this)

        connectivityCallback = ConnectivityCallback(this)
        registerConnectivityReceiver(connectivityCallback!!)

        getUserData()
    }

    private fun getUserData() {
        intent?.let {
            if (it.hasExtra(KEY_USERNAME) && it.hasExtra(KEY_NOTES) && it.hasExtra(KEY_UID)) {
                val userName = it.getStringExtra(KEY_USERNAME)
                val notes = it.getStringExtra(KEY_NOTES)
                val uid = it.getIntExtra(KEY_UID, -1)

                if (userName != null && uid != -1) {
                    viewModel.getUser(userName, uid, notes)
                }
            }
        }
    }

    override fun onFailure(message: String) {
        toast(message)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        connectivityCallback?.let {
            unRegisterConnectivityReceiver(it)
        }
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        if (isConnected && isConnectionChange != isConnected) {
            getUserData()
        }
        isConnectionChange = isConnected
    }
}