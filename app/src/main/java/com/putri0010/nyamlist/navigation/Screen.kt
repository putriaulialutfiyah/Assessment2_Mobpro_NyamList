package com.putri0010.nyamlist.navigation

const val KEY_ID_WISHLIST = "idWishlist"
sealed class Screen(val route: String) {
    data object Home: Screen("mainScreen")
    data object FormBaru: Screen("addEditScreen")
    data object FormUbah: Screen("addEditScreen/{${KEY_ID_WISHLIST}}") {
        fun withId(id: Long) = "addEditScreen/$id"
    }
    data object RecycleBin : Screen("recycle")
}