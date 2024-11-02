package com.kuroakevizago.dicodingapp.ui.favorite

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kuroakevizago.dicodingapp.data.local.entity.FavoriteEntity
import com.kuroakevizago.dicodingapp.enumeration.ViewState
import com.kuroakevizago.dicodingapp.data.repository.FavoriteRepository

class FavoriteViewModel(application: Application) : ViewModel() {
    private val favoriteRepository : FavoriteRepository = FavoriteRepository.getInstance(application)

    // LiveData to track loading state
    private val _fetchStateAllFavorites = MutableLiveData<ViewState>()
    val fetchStateAllFavorites: LiveData<ViewState> get() = _fetchStateAllFavorites

    fun insert(favorite : FavoriteEntity) {
        favoriteRepository.insert(favorite)
    }

    fun delete(favorite : FavoriteEntity) {
        favoriteRepository.delete(favorite)
    }
    fun isFavoriteExists(id: Int): LiveData<Boolean> {
        return favoriteRepository.isFavoriteExists(id)
    }

    fun getAllFavorites() : LiveData<List<FavoriteEntity>> {
        _fetchStateAllFavorites.value = ViewState.Loading
        val liveData = favoriteRepository.getAllFavorites()

        liveData.observeForever {
            _fetchStateAllFavorites.value = ViewState.Loaded
        }

        return liveData
    }
}