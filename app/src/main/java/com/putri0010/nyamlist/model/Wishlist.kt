package com.putri0010.nyamlist.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wish")
data class Wishlist (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val kota: String,
    val makanan: String,
    val resto: String,
    val status: String,
    val tanggal: String,

    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean = false
)