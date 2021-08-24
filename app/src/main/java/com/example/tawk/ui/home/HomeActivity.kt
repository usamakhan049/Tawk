package com.example.tawk.ui.home

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tawk.R
import com.example.tawk.data.db.entity.User
import com.example.tawk.databinding.ActivityHomeBinding
import com.example.tawk.ui.profile.ProfileActivity
import com.example.tawk.util.*
import com.example.tawk.util.Constants.KEY_NOTES
import com.example.tawk.util.Constants.KEY_UID
import com.example.tawk.util.Constants.KEY_USERNAME
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance


@ExperimentalPagingApi
class HomeActivity : AppCompatActivity(), KodeinAware,
    IUserListItemClickListner, ConnectivityCallback.ConnectivityReceiverListener {

    override val kodein by kodein()
    private val factory: HomeViewModelFactory by instance()
    private lateinit var binding: ActivityHomeBinding
    private lateinit var viewModel: HomeViewModel
    private var homeAdapter: HomeRecyclerViewAdapter? = null
    private var searchOserver: Observer<PagingData<User>>? = null
    private var userDataObserver: Observer<PagingData<User>>? = null
    private var serachLiveData: LiveData<PagingData<User>>? = null
    private var isConnectionChange = false
    private var connectivityCallback: ConnectivityCallback? = null
    private var isProfileOpen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        viewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)

        connectivityCallback = ConnectivityCallback(this)
        registerConnectivityReceiver(connectivityCallback!!)
        bindUi()
    }

    private fun bindUi() {
        initRecyclerView()
    }

    private fun initRecyclerView() {
        homeAdapter = HomeRecyclerViewAdapter()
        homeAdapter?.setItemClickListner(this)
        binding.rvUserList.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity)
            adapter = homeAdapter
        }
        updateLoadStates()
        observeUserProfile()
    }

    private fun updateLoadStates() {
        lifecycleScope.launch {
            homeAdapter?.loadStateFlow?.collectLatest { loadStates ->
                if (loadStates.refresh is LoadState.Loading || loadStates.append is LoadState.Loading) {
                    binding.pbUser.show()
                    return@collectLatest
                }
                if (loadStates.refresh is LoadState.NotLoading || loadStates.append is LoadState.NotLoading) {
                    binding.pbUser.hide()
                    //return@collectLatest
                }
                if (loadStates.refresh is LoadState.Error || loadStates.append is LoadState.Error) {
                    binding.pbUser.hide()
                    toast(getString(R.string.internet_connection_error))
                    return@collectLatest
                }
            }
        }
    }

    private fun observeUserProfile() {
        userDataObserver = Observer {
            homeAdapter?.submitData(lifecycle, it)
            homeAdapter?.notifyDataSetChanged()
        }
        viewModel.users.observe(this, userDataObserver!!)

    }

    private fun removeUserProfileObserver() {
        userDataObserver?.let {
            viewModel.users.removeObserver(it)
        }

    }

    private fun removeSearchObserver() {
        if (serachLiveData != null && searchOserver != null) {
            serachLiveData?.removeObserver(searchOserver!!)
        }
    }

    private fun searchData(query: String) {
        serachLiveData = viewModel.searchUsers(query)
        searchOserver = Observer { data ->
            homeAdapter = HomeRecyclerViewAdapter()
            homeAdapter?.setItemClickListner(this)
            binding.rvUserList.apply {
                adapter = homeAdapter
            }
            homeAdapter?.submitData(lifecycle, data)
        }
        serachLiveData?.observe(this@HomeActivity, searchOserver!!)
    }

    override fun onItemClick(user: User) {
        isProfileOpen = true
        removeUserProfileObserver()

        val bundle = Bundle()
        bundle.putString(KEY_USERNAME, user.login)
        bundle.putString(KEY_NOTES, user.notes)
        bundle.putInt(KEY_UID, user.uid)
        showActivity<ProfileActivity>(bundle)
    }

    override fun onDestroy() {
        super.onDestroy()
        connectivityCallback?.let {
            unRegisterConnectivityReceiver(it)
        }
    }

    override fun onResume() {
        super.onResume()
        if (isProfileOpen) {
            isProfileOpen = false
            initRecyclerView()
        }
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        if (isConnected && isConnectionChange != isConnected) {
            homeAdapter?.retry()
        }
        isConnectionChange = isConnected
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)
        val searchItem: MenuItem = menu.findItem(R.id.action_search)
        val searchManager =
            applicationContext.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        var searchView: SearchView? = null
        searchItem?.let {
            searchView = it.getActionView() as SearchView
        }
        searchView?.let {
            it.setSearchableInfo(searchManager.getSearchableInfo(this.getComponentName()))
            it.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

                override fun onQueryTextChange(newText: String): Boolean {
                    return true
                }

                override fun onQueryTextSubmit(query: String?): Boolean {
                    removeSearchObserver()
                    query?.let { searchData(it) }
                    return true
                }
            })
        }
        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                removeUserProfileObserver()
                return true;
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                removeSearchObserver()
                initRecyclerView()
                return true;
            }

        })
        return super.onCreateOptionsMenu(menu)
    }
}