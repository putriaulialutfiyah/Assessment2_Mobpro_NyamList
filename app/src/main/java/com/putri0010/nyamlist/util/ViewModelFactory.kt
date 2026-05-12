package com.putri0010.nyamlist.util

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.putri0010.nyamlist.database.WishlistDb
import com.putri0010.nyamlist.ui.screen.AddEditViewModel
import com.putri0010.nyamlist.ui.screen.MainViewModel
import com.putri0010.nyamlist.ui.screen.RecycleBinViewModel

class ViewModelFactory (
    private val context: Context
) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val dao = WishlistDb.getInstance(context).dao

        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(dao) as T
        }
        else if (modelClass.isAssignableFrom(AddEditViewModel::class.java)){
            return AddEditViewModel(dao) as T
        }
        else if (modelClass.isAssignableFrom(RecycleBinViewModel::class.java)) {
            return RecycleBinViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}