package com.example.tawk.data.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.tawk.data.db.entity.RemoteKeys
import com.example.tawk.data.db.entity.User
import com.example.tawk.util.getOrAwaitValue
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.runner.RunWith
import java.util.*

class AppDatabaseTest {
}


@RunWith(AndroidJUnit4::class)
class SpendDatabaseTest : TestCase() {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var db: AppDatabase
    private lateinit var dao: UserDao

    @Before
    public override fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        dao = db.getUserDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun testNoKeyReturned_whenNoKeyEnter() = runBlocking {
        // Given that the RoomDataSource returns an empty list of remote keys
        assertEquals(db.getRemoteKeysDao().getRemoteKeysById(1), null)
    }


    @Test
    fun testKeyReturned_whenKeySaved() = runBlocking {
        // save mock object
        val remoteKey = RemoteKeys(1, null, 2)

        val keyList = ArrayList<RemoteKeys>()
        keyList.add(remoteKey)
        db.getRemoteKeysDao().saveAllRemoteKeys(keyList)

        // get keys
        val keysFromDb = db.getRemoteKeysDao().getRemoteKeysById(1)

        // Given that the RoomDataSource returns saved key
        Assert.assertEquals(remoteKey, keysFromDb)
    }

    @Test
    fun testNoProfileReturned_whenNoProfileEnter() = runBlocking {
        // Given that the RoomDataSource returns an empty list of profile
        assertEquals(db.getUserDao().getProfileData(1).getOrAwaitValue(), null)
    }

    @Test
    fun testProfileReturned_whenProfileSaved() = runBlocking {
        // save mock object
        val user = User(
            "login", 1, "Description", "avatar_url",
            "gravatar_id", "url", "html", "foolowerUrl", "folowig_url",
            "gists_Url", "starred_url", "subscription", "organzation",
            "repos", "events", "recieved", "type", false, "name",
            "comapny", "blog", "location", "email", "hirable", "bio", "twiter",
            3, 2, 10, 20, "12", "10", "Notes"
        )

        var userList = ArrayList<User>()
        userList.add(user)
        dao.saveAllUser(userList)

        // get user profile
        val userData = dao.getProfileData(1)

        // Given that the RoomDataSource returns saved profile
        assertEquals(userData.getOrAwaitValue().uid, 1)
    }

}