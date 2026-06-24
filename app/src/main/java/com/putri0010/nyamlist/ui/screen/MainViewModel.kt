package com.putri0010.nyamlist.ui.screen

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.putri0010.nyamlist.database.WishlistDao
import com.putri0010.nyamlist.model.Wishlist
import com.putri0010.nyamlist.network.ApiStatus
import com.putri0010.nyamlist.network.WishlistApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream

class MainViewModel(private val dao: WishlistDao): ViewModel() {

    var data = mutableStateOf(emptyList<Wishlist>())

    var status = MutableStateFlow(ApiStatus.LOADING)

    var errorMessage = mutableStateOf<String?>(null)

    fun retrieveData(token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            status.value = ApiStatus.LOADING
            try {
                data.value = WishlistApi.service.getWishlist("Bearer $token")
                status.value = ApiStatus.SUCCESS
            } catch (e: Exception) {
                Log.d("MainViewModel", "Failure: ${e.message}")
                status.value = ApiStatus.FAILED
            }
        }
    }

    fun saveData(userId: String, kota: String, makanan: String, resto: String, statusKunjungan: String, tanggal: String, bitmap: Bitmap) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = WishlistApi.service.postWishlist(
                    token = "Bearer $userId",
                    kota = kota.toRequestBody("text/plain".toMediaTypeOrNull()),
                    makanan = makanan.toRequestBody("text/plain".toMediaTypeOrNull()),
                    resto = resto.toRequestBody("text/plain".toMediaTypeOrNull()),
                    status = statusKunjungan.toRequestBody("text/plain".toMediaTypeOrNull()),
                    tanggal = tanggal.toRequestBody("text/plain".toMediaTypeOrNull()),
                    image = bitmap.toMultipartBody()
                )
                if (result.status == "success") {
                    retrieveData(userId)
                } else {
                    throw Exception(result.message)
                }
            } catch (e: Exception) {
                Log.d("MainViewModel", "Failure: ${e.message}")
                errorMessage.value = "Error: ${e.message}"
            }
        }
    }

    fun deleteData(userId: String, wishlistId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = WishlistApi.service.deleteWishlist("Bearer $userId", wishlistId)
                if (result.status == "success") {
                    retrieveData(userId)
                } else {
                    throw Exception(result.message)
                }
            } catch (e: Exception) {
                Log.d("MainViewModel", "Failure: ${e.message}")
                errorMessage.value = "Error: ${e.message}"
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

    fun clearMessage() { errorMessage.value = null }
}