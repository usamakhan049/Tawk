package com.example.tawk.data.db

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import com.example.tawk.data.db.entity.User

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAllUser(user: List<User>)

    @Query("SELECT * FROM user")
    fun getAllUsersModel(): PagingSource<Int, User>

    @Query("SELECT * FROM user where login LIKE  '%' || :query || '%' OR  notes LIKE  '%' || :query || '%'")
    fun getSearchUsersModel(query: String): PagingSource<Int, User>

    @Query("SELECT * from User where uid = :uid")
    fun getProfileData(uid: Int): LiveData<User>

    @Query("UPDATE User SET notes= :notes  where uid = :uid")
    fun setProfileData(uid: Int, notes: String?)

    @Update
    fun setProfile(user: User)

    @Query("DELETE FROM User")
    suspend fun clearUsers()
}