package com.kuroakevizago.dicodingapp.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.kuroakevizago.dicodingapp.R
import com.kuroakevizago.dicodingapp.adapter.EventViewAdapter
import com.kuroakevizago.dicodingapp.data.display.IDisplayableItem
import com.kuroakevizago.dicodingapp.data.remote.response.ListEventsItem
import com.kuroakevizago.dicodingapp.databinding.ActivityMainBinding
import com.kuroakevizago.dicodingapp.enumeration.ViewState
import com.kuroakevizago.dicodingapp.helper.ViewModelFactory
import com.kuroakevizago.dicodingapp.interfaces.OnItemClickListener
import com.kuroakevizago.dicodingapp.preferences.SettingsPreferences
import com.kuroakevizago.dicodingapp.preferences.dataStore
import com.kuroakevizago.dicodingapp.ui.detail.DetailActivity
import com.kuroakevizago.dicodingapp.ui.home.HomeViewModel
import com.kuroakevizago.dicodingapp.ui.settings.SettingsViewModel


class MainActivity : AppCompatActivity(), OnItemClickListener<List<IDisplayableItem?>> {

    private lateinit var binding: ActivityMainBinding
    private lateinit var homeViewModel : HomeViewModel

    private lateinit var searchEventList: List<ListEventsItem?>
    private var searchQuery : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main)
        val navController = navHostFragment!!.findNavController()

        val factory = ViewModelFactory.getInstance(application)
        homeViewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]

        loadSettings()
        observeSearchEvents()

        setSupportActionBar(binding.topAppBar)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        // Find the search item and set up SearchView
        val searchItem = menu?.findItem(R.id.action_search)
        val searchView: SearchView? = searchItem?.actionView as SearchView?

        // Set up SearchView's listeners (optional)
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchQuery = newText
                if (!newText.isNullOrEmpty()) {
                    homeViewModel.fetchSearchEvents(newText)
                    setSearchView(View.VISIBLE)
                }
                else {
                    setSearchErrorRetry(View.INVISIBLE)
                    setSearchView(View.INVISIBLE)
                    setSearchIsNotFound(View.INVISIBLE)
                }
                return true
            }

        })

        return super.onCreateOptionsMenu(menu)
    }

    private fun loadSettings() {
        //View Model Setup
        val pref = SettingsPreferences.getInstance(this.application.dataStore)
        val viewModelFactory = ViewModelFactory.getInstance(pref)
        val settingsViewModel = ViewModelProvider(this, viewModelFactory)[SettingsViewModel::class.java]

        settingsViewModel.getThemeSettings().observe(this) { isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }



    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeSearchEvents() {
        val searchEventsViewAdapter = EventViewAdapter(this)
        binding.rvSearchResult.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvSearchResult.adapter = searchEventsViewAdapter

        homeViewModel.loadStateSearchEvents.observe(this) { loadState ->
            searchEventsViewAdapter.viewState = loadState
            binding.rvSearchResult.adapter = searchEventsViewAdapter
            searchEventsViewAdapter.notifyDataSetChanged()
            if (loadState == ViewState.Failed) {
                setSearchErrorRetry(View.VISIBLE)
            }
        }

        homeViewModel.searchEvents.observe(this) { listEvents ->
            if (listEvents != null) {
                searchEventList = listEvents
                searchEventsViewAdapter.eventList = searchEventList
                searchEventsViewAdapter.notifyDataSetChanged()

                if (!searchQuery.isNullOrEmpty() && listEvents.isEmpty())
                    setSearchIsNotFound(View.VISIBLE)
                else
                    setSearchIsNotFound(View.INVISIBLE)
            }
        }
    }

    private fun setSearchView(visibility: Int) {
        if (visibility == View.VISIBLE) {
            binding.navHostFragmentActivityMain.visibility = View.INVISIBLE
            binding.rvSearchResult.visibility = View.VISIBLE
        }
        else {
            binding.navHostFragmentActivityMain.visibility = View.VISIBLE
            binding.rvSearchResult.visibility = View.INVISIBLE
        }
    }

    private  fun setSearchIsNotFound(visibility: Int) {
        binding.tvEventsNotFound.visibility = visibility
    }

    private fun setSearchErrorRetry(visibility: Int) {
        if (visibility == View.VISIBLE) {
            binding.rvSearchResult.visibility = View.INVISIBLE
            binding.errorSearchRetry.root.visibility = View.VISIBLE

            binding.errorSearchRetry.btnRetryConnection.setOnClickListener {
                setSearchErrorRetry(View.INVISIBLE)
                homeViewModel.fetchFinishedEvents()
            }
        } else if (visibility == View.INVISIBLE) {
            binding.rvSearchResult.visibility = View.VISIBLE
            binding.errorSearchRetry.root.visibility = View.INVISIBLE
        }
    }

    override fun onItemClick(position: Int, itemList: List<IDisplayableItem?>) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra(
            "event_id",
            itemList[position]?.id
        )
        startActivity(intent)
    }
}