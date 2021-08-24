package com.example.tawk.data.repositories

import androidx.lifecycle.LiveData
import androidx.paging.*
import com.example.tawk.data.db.AppDatabase
import com.example.tawk.data.db.entity.User
import com.example.tawk.data.network.MyApi
import com.example.tawk.data.network.SafeApiRequest
import com.example.tawk.data.paging.HomeMediator

const val DEFAULT_PAGE_SIZE = 15
private const val MAX_SIZE = 45

class HomeRepository(
    private val api: MyApi,
    private val db: AppDatabase
) : SafeApiRequest() {

    @ExperimentalPagingApi
    fun getAllUsersFlowDb(pagingConfig: PagingConfig = getDefaultPageConfig()): LiveData<PagingData<User>> {

        val pagingSourceFactory = { db.getUserDao().getAllUsersModel() }

        return Pager(
            config = pagingConfig,
            pagingSourceFactory = pagingSourceFactory,
            remoteMediator = HomeMediator(api, db)
        ).liveData
    }

    fun getSearchUsers(query: String): LiveData<PagingData<User>> {

        val pagingSourceFactory = { db.getUserDao().getSearchUsersModel(query) }
        return Pager(
            config = getDefaultPageConfig(),
            pagingSourceFactory = pagingSourceFactory,
        ).liveData
    }


    fun getDefaultPageConfig(): PagingConfig {
        return PagingConfig(
            pageSize = DEFAULT_PAGE_SIZE,
            maxSize = MAX_SIZE,
            enablePlaceholders = false
        )
    }
}