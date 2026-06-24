package com.putri0010.nyamlist.util

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.putri0010.nyamlist.database.WishlistDb
import com.putri0010.nyamlist.ui.screen.MainViewModel

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            val db = WishlistDb.getInstance(context)
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(db.dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}