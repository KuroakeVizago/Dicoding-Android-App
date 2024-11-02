package com.kuroakevizago.dicodingapp.data.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.kuroakevizago.dicodingapp.data.local.entity.FavoriteEntity
import com.kuroakevizago.dicodingapp.data.local.database.FavoriteRoomDatabase
import com.kuroakevizago.dicodingapp.data.local.database.IFavoriteDao
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class FavoriteRepository private constructor(application: Application) {

    private val mFavoritesDao: IFavoriteDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val db = FavoriteRoomDatabase.getDatabase(application)
        mFavoritesDao = db.favoriteDao()
    }

    fun get(id: String): LiveData<FavoriteEntity?> = mFavoritesDao.get(id)

    fun getAllFavorites(): LiveData<List<FavoriteEntity>> = mFavoritesDao.getAllFavorites()

    fun insert(favorite: FavoriteEntity) {
        executorService.execute { mFavoritesDao.insert(favorite) }
    }

    fun delete(favorite: FavoriteEntity) {
        executorService.execute { mFavoritesDao.delete(favorite) }
    }


    fun isFavoriteExists(id: Int): LiveData<Boolean> {
        return mFavoritesDao.isFavoriteExists(id)
    }

    companion object {
        @Volatile
        private var INSTANCE: FavoriteRepository? = null

        fun getInstance(application: Application): FavoriteRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = FavoriteRepository(application)
                INSTANCE = instance
                instance
            }
        }
    }
}
