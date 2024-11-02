package com.kuroakevizago.dicodingapp.helper

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kuroakevizago.dicodingapp.preferences.SettingsPreferences
import com.kuroakevizago.dicodingapp.ui.detail.DetailViewModel
import com.kuroakevizago.dicodingapp.ui.favorite.FavoriteViewModel
import com.kuroakevizago.dicodingapp.ui.home.HomeViewModel
import com.kuroakevizago.dicodingapp.ui.settings.SettingsViewModel

class ViewModelFactory private constructor() : ViewModelProvider.NewInstanceFactory() {

    private lateinit var application: Application
    private lateinit var pref: SettingsPreferences

    constructor(application: Application) : this() {
        this.application = application
    }

    constructor(pref: SettingsPreferences) : this() {
        this.pref = pref
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        @JvmStatic
        fun getInstance(): ViewModelFactory {
            if (INSTANCE == null) {
                synchronized(ViewModelFactory::class.java) {
                    INSTANCE = ViewModelFactory()
                }
            }

            return INSTANCE as ViewModelFactory
        }

        @JvmStatic
        fun getInstance(application: Application): ViewModelFactory {
            if (INSTANCE == null) {
                synchronized(ViewModelFactory::class.java) {
                    INSTANCE = ViewModelFactory(application)
                }
            }
            else {
                INSTANCE!!.application = application
            }
            return INSTANCE as ViewModelFactory
        }

        @JvmStatic
        fun getInstance(pref: SettingsPreferences): ViewModelFactory {
            if (INSTANCE == null) {
                synchronized(ViewModelFactory::class.java) {
                    INSTANCE = ViewModelFactory(pref)
                }
            }
            else {
                INSTANCE!!.pref = pref
            }
            return INSTANCE as ViewModelFactory
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) && ::application.isInitialized -> {
                HomeViewModel() as T
            }
            modelClass.isAssignableFrom(DetailViewModel::class.java) && ::application.isInitialized -> {
                DetailViewModel() as T
            }
            modelClass.isAssignableFrom(FavoriteViewModel::class.java) && ::application.isInitialized -> {
                FavoriteViewModel(application) as T
            }
            modelClass.isAssignableFrom(SettingsViewModel::class.java) && ::pref.isInitialized-> {
                SettingsViewModel(pref) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}