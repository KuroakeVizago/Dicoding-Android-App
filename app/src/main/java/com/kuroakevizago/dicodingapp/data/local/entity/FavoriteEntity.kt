package com.kuroakevizago.dicodingapp.data.local.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kuroakevizago.dicodingapp.data.display.IDisplayableItem
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class FavoriteEntity (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    override val id: Int? = 0,

    @ColumnInfo(name = "name")
    override var name: String? = null,

    @ColumnInfo(name = "mediaCover")
    override var mediaCover: String? = null,

    @ColumnInfo(name = "isFavorite")
    var isFavorite: Boolean? = null
) : Parcelable, IDisplayableItem