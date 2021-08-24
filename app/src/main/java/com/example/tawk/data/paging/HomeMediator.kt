package com.example.tawk.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.tawk.data.db.AppDatabase
import com.example.tawk.data.db.entity.RemoteKeys
import com.example.tawk.data.db.entity.User
import com.example.tawk.data.network.MyApi
import retrofit2.HttpException
import java.io.IOException
import java.io.InvalidObjectException

private const val DEFAULT_PAGE_INDEX = 1

@ExperimentalPagingApi
class HomeMediator(val apiService: MyApi, val appDatabase: AppDatabase) :
    RemoteMediator<Int, User>() {

    override suspend fun load(
        loadType: LoadType, state: PagingState<Int, User>
    ): MediatorResult {

        val pageKeyData = getKeyPageData(loadType, state)
        val page = when (pageKeyData) {
            is MediatorResult.Success -> {
                return pageKeyData
            }
            else -> {
                pageKeyData as Int
            }
        }

        try {
            val response = apiService.getUsers(page)
            val isEndOfList = response.isEmpty()
            appDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    appDatabase.getRemoteKeysDao().clearRemoteKeys()
                    appDatabase.getUserDao().clearUsers()
                }
                val prevKey = if (page == DEFAULT_PAGE_INDEX) null else page - 1
                val nextKey = if (isEndOfList) null else page + 1
                val keys = response.map {
                    RemoteKeys(repoId = it.id, prevKey = prevKey, nextKey = nextKey)
                }
                appDatabase.getRemoteKeysDao().saveAllRemoteKeys(keys)
                appDatabase.getUserDao().saveAllUser(response)
            }
            return MediatorResult.Success(endOfPaginationReached = isEndOfList)
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }
    }

    suspend fun getKeyPageData(loadType: LoadType, state: PagingState<Int, User>): Any? {
        return when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getClosestRemoteKey(state)
                remoteKeys?.nextKey?.minus(1) ?: DEFAULT_PAGE_INDEX
            }
            LoadType.APPEND -> {
                val remoteKeys = getLastRemoteKey(state)
                    ?: throw InvalidObjectException("Remote key should not be null for")
                remoteKeys.nextKey ?: MediatorResult.Success(endOfPaginationReached = true)
            }
            LoadType.PREPEND -> {
                val remoteKeys =
                    getFirstRemoteKey(state)
                //end of list condition reached
                if (remoteKeys != null) {
                    return return MediatorResult.Success(endOfPaginationReached = true)
                } else {
                    return DEFAULT_PAGE_INDEX
                }
                remoteKeys?.prevKey ?: return MediatorResult.Success(endOfPaginationReached = true)
            }
        }
    }

    private suspend fun getLastRemoteKey(state: PagingState<Int, User>): RemoteKeys? {
        return state.pages
            .lastOrNull { it.data.isNotEmpty() }
            ?.data?.lastOrNull()
            ?.let { user -> appDatabase.getRemoteKeysDao().getRemoteKeysById(user.id) }
    }

    private suspend fun getFirstRemoteKey(state: PagingState<Int, User>): RemoteKeys? {
        return state.pages
            .firstOrNull() { it.data.isNotEmpty() }
            ?.data?.firstOrNull()
            ?.let { user -> appDatabase.getRemoteKeysDao().getRemoteKeysById(user.id) }
    }

    private suspend fun getClosestRemoteKey(state: PagingState<Int, User>): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { repoId ->
                appDatabase.getRemoteKeysDao().getRemoteKeysById(repoId)
            }
        }
    }

}