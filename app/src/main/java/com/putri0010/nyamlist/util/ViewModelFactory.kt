package com.putri0010.nyamlist.util

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.putri0010.nyamlist.database.WishlistDb
import com.putri0010.nyamlist.ui.screen.MainViewModel
import com.putri0010.nyamlist.ui.screen.AddEditViewModel
import com.putri0010.nyamlist.ui.screen.RecycleBinViewModel

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val db = WishlistDb.getInstance(context)
        @Suppress("UNCHECKED_CAST")
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(db.dao) as T
            }
            modelClass.isAssignableFrom(AddEditViewModel::class.java) -> {
                AddEditViewModel(db.dao) as T
            }
            modelClass.isAssignableFrom(RecycleBinViewModel::class.java) -> {
                RecycleBinViewModel(db.dao) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}