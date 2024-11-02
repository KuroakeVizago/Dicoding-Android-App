package com.kuroakevizago.dicodingapp.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.kuroakevizago.dicodingapp.preferences.SettingsPreferences
import kotlinx.coroutines.launch

class SettingsViewModel(private val pref: SettingsPreferences) : ViewModel() {
    fun getThemeSettings(): LiveData<Boolean> {
        return pref.getThemeSetting().asLiveData()
    }

    fun saveThemeSetting(isDarkModeActive: Boolean) {
        viewModelScope.launch {
            pref.saveThemeSetting(isDarkModeActive)
        }
    }

    fun saveNotificationSetting(isEnabled: Boolean) {
        viewModelScope.launch {
            pref.saveNotificationSetting(isEnabled)
        }
    }

    fun getNotificationSetting() : LiveData<Boolean> {
        return pref.getNotificationSetting().asLiveData()
    }
}