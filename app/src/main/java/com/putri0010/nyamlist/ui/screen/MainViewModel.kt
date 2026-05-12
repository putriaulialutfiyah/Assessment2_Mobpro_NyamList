package com.putri0010.nyamlist.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.putri0010.nyamlist.database.WishlistDao
import com.putri0010.nyamlist.model.Wishlist
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class MainViewModel(dao: WishlistDao) : ViewModel() {

    val data: StateFlow<List<Wishlist>> = dao.getWishlist().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = emptyList()
    )
}