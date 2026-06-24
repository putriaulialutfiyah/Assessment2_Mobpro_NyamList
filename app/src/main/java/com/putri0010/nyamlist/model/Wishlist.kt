package com.putri0010.nyamlist.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

@Entity(tableName = "wish")
data class Wishlist (
    @PrimaryKey(autoGenerate = true)
    @Json(name = "id")
    val id: Long = 0L,

    @Json(name = "kota")
    val kota: String,

    @Json(name = "makanan")
    val makanan: String,

    @Json(name = "resto")
    val resto: String,

    @Json(name = "status")
    val status: String,

    @Json(name = "tanggal")
    val tanggal: String,

    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean = false,

    @ColumnInfo(name = "image_url")
    @Json(name = "image_url")
    val imageUrl: String? = null,

    @ColumnInfo(name = "local_image_uri")
    val localImageUri: String? = null,

    @ColumnInfo(name = "is_synced")
    val isSynced: Boolean = true
)

data class OpStatus(
    @Json(name = "status") var status: String,
    @Json(name = "message") var message: String?
)