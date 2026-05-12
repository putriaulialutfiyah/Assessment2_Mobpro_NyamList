package com.putri0010.nyamlist.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.putri0010.nyamlist.model.Wishlist
import kotlin.jvm.java

@Database(entities = [Wishlist::class], version = 2, exportSchema = false)
abstract class WishlistDb : RoomDatabase() {

    abstract val dao: WishlistDao

    companion object {

        @Volatile
        private var INSTANCE: WishlistDb? = null

        fun getInstance(context: Context): WishlistDb {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        WishlistDb::class.java,
                        "wishlist.db"
                    )
                    .fallbackToDestructiveMigration(false)
                    .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}