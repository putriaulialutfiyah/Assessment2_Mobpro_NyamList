package com.putri0010.nyamlist.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.putri0010.nyamlist.database.WishlistDao
import com.putri0010.nyamlist.model.Wishlist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddEditViewModel(private val dao: WishlistDao) : ViewModel() {

    private val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)

    fun insert(kota: String, makanan: String, resto: String, status: String) {
        val wish = Wishlist(
            tanggal = formatter.format(Date()),
            kota = kota,
            makanan = makanan,
            resto = resto,
            status = status
        )

        viewModelScope.launch(Dispatchers.IO) {
            dao.insert(wish)
        }
    }
    suspend fun getWishlist(id: Long) : Wishlist? {
        return dao.getWishlistById(id)
    }

    fun update(id: Long, kota: String, makanan: String, resto: String, status: String) {
        val wish = Wishlist(
            id = id,
            tanggal = formatter.format(Date()),
            kota = kota,
            makanan = makanan,
            resto = resto,
            status = status
        )
        viewModelScope.launch(Dispatchers.IO) {
            dao.update(wish)
        }
    }

    fun delete(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.softDelete(id)
        }
    }
}
