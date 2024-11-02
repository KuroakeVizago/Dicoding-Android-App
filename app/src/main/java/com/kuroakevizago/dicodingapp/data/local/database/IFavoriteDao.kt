package com.kuroakevizago.dicodingapp.data.local.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.kuroakevizago.dicodingapp.data.local.entity.FavoriteEntity

@Dao
interface IFavoriteDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(favorite: FavoriteEntity)

    @Update
    fun update(favorite: FavoriteEntity)

    @Delete
    fun delete(favorite: FavoriteEntity)

    @Query("SELECT * FROM FavoriteEntity WHERE id = :id")
    fun get(id : String) : LiveData<FavoriteEntity?>

    @Query("SELECT * from FavoriteEntity ORDER BY id ASC")
    fun getAllFavorites(): LiveData<List<FavoriteEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM FavoriteEntity WHERE id = :id LIMIT 1)")
    fun isFavoriteExists(id: Int): LiveData<Boolean>
}