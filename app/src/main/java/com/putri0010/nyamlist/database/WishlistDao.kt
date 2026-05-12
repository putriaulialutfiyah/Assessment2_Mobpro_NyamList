package com.putri0010.nyamlist.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.putri0010.nyamlist.model.Wishlist
import kotlinx.coroutines.flow.Flow

@Dao
interface WishlistDao {

    @Insert
    suspend fun insert(wishlist: Wishlist)

    @Update
    suspend fun update(wishlist: Wishlist)

    @Query("""SELECT * FROM wish WHERE is_deleted = 0 ORDER BY tanggal DESC""")
    fun getWishlist(): Flow<List<Wishlist>>

    @Query("""SELECT * FROM wish WHERE id = :id""")
    suspend fun getWishlistById(id: Long): Wishlist?

    @Query("""UPDATE wish SET is_deleted = 1 WHERE id = :id""")
    suspend fun softDelete(id: Long)

    @Query("""SELECT * FROM wish WHERE is_deleted = 1 ORDER BY tanggal DESC""")
    fun getDeletedWishlist(): Flow<List<Wishlist>>

    @Query("""UPDATE wish SET is_deleted = 0 WHERE id = :id""")
    suspend fun restoreWishlist(id: Long)

    @Query("""DELETE FROM wish WHERE id = :id""")
    suspend fun deletePermanent(id: Long)
}