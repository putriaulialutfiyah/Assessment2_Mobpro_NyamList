package com.putri0010.nyamlist.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.putri0010.nyamlist.database.WishlistDao
import kotlinx.coroutines.launch

class RecycleBinViewModel(
    private val dao: WishlistDao
) : ViewModel() {

    val data = dao.getDeletedWishlist()

    fun restore(id: Long) {
        viewModelScope.launch {
            dao.restoreWishlist(id)
        }
    }

    fun deletePermanent(id: Long) {
        viewModelScope.launch {
            dao.deletePermanent(id)
        }
    }
}