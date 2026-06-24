package com.putri0010.nyamlist.ui.screen

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.putri0010.nyamlist.database.WishlistDao
import com.putri0010.nyamlist.model.Wishlist
import com.putri0010.nyamlist.network.WishlistApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddEditViewModel(private val dao: WishlistDao) : ViewModel() {

    private val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)

    suspend fun getWishlist(token: String, id: Long): Wishlist? {
        return withContext(Dispatchers.IO) {
            try {
                WishlistApi.service.getWishlistById("Bearer $token", id.toString())
            } catch (e: Exception) {
                Log.e("AddEditViewModel", "Error fetching wishlist: ${e.message}")
                null
            }
        }
    }

    fun insert(
        token: String,
        kota: String,
        makanan: String,
        resto: String,
        status: String,
        bitmap: Bitmap?,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val imagePart = bitmap?.toMultipartBody()
                if (imagePart == null) {
                    throw Exception("Gambar harus dipilih")
                }
                val result = WishlistApi.service.postWishlist(
                    token = "Bearer $token",
                    kota = kota.toRequestBody("text/plain".toMediaTypeOrNull()),
                    makanan = makanan.toRequestBody("text/plain".toMediaTypeOrNull()),
                    resto = resto.toRequestBody("text/plain".toMediaTypeOrNull()),
                    status = status.toRequestBody("text/plain".toMediaTypeOrNull()),
                    tanggal = formatter.format(Date()).toRequestBody("text/plain".toMediaTypeOrNull()),
                    image = imagePart
                )
                if (result.status == "success") {
                    withContext(Dispatchers.Main) {
                        onSuccess()
                    }
                } else {
                    throw Exception(result.message)
                }
            } catch (e: Exception) {
                Log.e("AddEditViewModel", "Error saving wishlist: ${e.message}")
            }
        }
    }

    fun update(
        token: String,
        id: Long,
        kota: String,
        makanan: String,
        resto: String,
        status: String,
        isDeleted: Boolean,
        bitmap: Bitmap?,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val imagePart = bitmap?.toMultipartBody()
                val result = WishlistApi.service.putWishlist(
                    token = "Bearer $token",
                    id = id.toString(),
                    kota = kota.toRequestBody("text/plain".toMediaTypeOrNull()),
                    makanan = makanan.toRequestBody("text/plain".toMediaTypeOrNull()),
                    resto = resto.toRequestBody("text/plain".toMediaTypeOrNull()),
                    status = status.toRequestBody("text/plain".toMediaTypeOrNull()),
                    tanggal = formatter.format(Date()).toRequestBody("text/plain".toMediaTypeOrNull()),
                    isDeleted = isDeleted.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
                    image = imagePart
                )
                if (result.status == "success") {
                    withContext(Dispatchers.Main) {
                        onSuccess()
                    }
                } else {
                    throw Exception(result.message)
                }
            } catch (e: Exception) {
                Log.e("AddEditViewModel", "Error updating wishlist: ${e.message}")
            }
        }
    }

    fun delete(token: String, id: Long, onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = WishlistApi.service.deleteWishlist("Bearer $token", id.toString())
                if (result.status == "success") {
                    withContext(Dispatchers.Main) {
                        onSuccess()
                    }
                } else {
                    throw Exception(result.message)
                }
            } catch (e: Exception) {
                Log.e("AddEditViewModel", "Error deleting wishlist: ${e.message}")
            }
        }
    }

    private fun Bitmap.toMultipartBody(): MultipartBody.Part {
        val stream = ByteArrayOutputStream()
        compress(Bitmap.CompressFormat.JPEG, 80, stream)
        val byteArray = stream.toByteArray()
        val requestBody = byteArray.toRequestBody(
            "image/jpg".toMediaTypeOrNull(), 0, byteArray.size)
        return MultipartBody.Part.createFormData(
            "image", "image.jpg", requestBody)
    }
}
