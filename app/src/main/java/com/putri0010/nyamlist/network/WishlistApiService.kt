package com.putri0010.nyamlist.network

import com.putri0010.nyamlist.model.OpStatus
import com.putri0010.nyamlist.model.Wishlist
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
private const val BASE_URL = "https://mini-project-3-nyamlist.vercel.app/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

enum class ApiStatus { LOADING, SUCCESS, FAILED }

interface WishlistApiService {
    @GET("api/wishlist")
    suspend fun getWishlist(@Header("Authorization") token: String): List<Wishlist>

    @GET("api/wishlist/{id}")
    suspend fun getWishlistById(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Wishlist

    @Multipart
    @POST("api/wishlist")
    suspend fun postWishlist(
        @Header("Authorization") token: String,
        @Part("kota") kota: RequestBody,
        @Part("makanan") makanan: RequestBody,
        @Part("resto") resto: RequestBody,
        @Part("status") status: RequestBody,
        @Part("tanggal") tanggal: RequestBody,
        @Part image: MultipartBody.Part
    ): OpStatus

    @DELETE("api/wishlist/{id}")
    suspend fun deleteWishlist(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): OpStatus

    @Multipart
    @PUT("api/wishlist/{id}")
    suspend fun putWishlist(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Part("kota") kota: RequestBody,
        @Part("makanan") makanan: RequestBody,
        @Part("resto") resto: RequestBody,
        @Part("status") status: RequestBody,
        @Part("tanggal") tanggal: RequestBody,
        @Part("is_deleted") isDeleted: RequestBody,
        @Part image: MultipartBody.Part? = null
    ): OpStatus

}

object WishlistApi {
    val service: WishlistApiService by lazy {
        retrofit.create(WishlistApiService::class.java)
    }

    fun getImageUrl(imageId: String): String {
        return "$BASE_URL$imageId.jpg"
    }
}