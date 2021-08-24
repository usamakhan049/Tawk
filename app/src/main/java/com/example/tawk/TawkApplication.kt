package com.example.tawk

import android.app.Application
import androidx.paging.ExperimentalPagingApi
import com.example.tawk.data.db.AppDatabase
import com.example.tawk.data.network.MyApi
import com.example.tawk.data.network.NetworkConnectionInterceptor
import com.example.tawk.data.repositories.HomeRepository
import com.example.tawk.data.repositories.ProfileRepository
import com.example.tawk.ui.home.HomeViewModelFactory
import com.example.tawk.ui.profile.ProfileViewModelFactory
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton

class TawkApplication : Application(), KodeinAware {
    override val kodein = Kodein.lazy {
        import(androidXModule(this@TawkApplication))

        bind() from singleton { NetworkConnectionInterceptor(instance()) }
        bind() from singleton { MyApi(instance()) }
        bind() from singleton { AppDatabase(instance()) }
        bind() from singleton { HomeRepository(instance(), instance()) }
        bind() from provider { HomeViewModelFactory(instance()) }
        bind() from singleton { ProfileRepository(instance(), instance()) }
        bind() from provider { ProfileViewModelFactory(instance()) }
    }
}