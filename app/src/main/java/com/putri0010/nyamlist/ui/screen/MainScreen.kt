package com.putri0010.nyamlist.ui.screen

import android.content.ContentResolver
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.ClearCredentialException
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.canhub.cropper.CropImageView
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.putri0010.nyamlist.BuildConfig
import com.putri0010.nyamlist.R
import com.putri0010.nyamlist.model.User
import com.putri0010.nyamlist.model.Wishlist
import com.putri0010.nyamlist.model.toImageModel
import com.putri0010.nyamlist.navigation.Screen
import com.putri0010.nyamlist.network.ApiStatus
import com.putri0010.nyamlist.network.UserDataStore
import com.putri0010.nyamlist.ui.theme.Cream
import com.putri0010.nyamlist.ui.theme.NyamListTheme
import com.putri0010.nyamlist.ui.theme.Orange
import com.putri0010.nyamlist.util.ViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun MainScreenPreview() {
    NyamListTheme {
        MainScreen(rememberNavController())
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavHostController) {
    val context = LocalContext.current
    val dataStore = remember { UserDataStore(context) }
    val userFlow = remember { dataStore.userFlow }
    val layoutFlow = remember { dataStore.layoutFlow }

    val user by userFlow.collectAsState(initial = User())
    val showList by layoutFlow.collectAsState(initial = true)

    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.app_name), fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Orange, titleContentColor = Cream),
                actions = {
                    IconButton(onClick = {
                        if (user.email.isEmpty()) {
                            CoroutineScope(Dispatchers.IO).launch { signIn(context, dataStore) }
                        }
                        else {
                            showDialog = true
                        }
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.account_circle),
                            contentDescription = stringResource(R.string.profil),
                            tint = Cream
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        ScreenContent(showList = showList, modifier = Modifier.padding(innerPadding), navController = navController)
    }

    if (showDialog) {
        ProfilDialog(
            user = user,
            onDismissRequest = { showDialog = false },
            onConfirmation = {
                CoroutineScope(Dispatchers.IO).launch {
                    signOut(context, dataStore)
                }
                showDialog = false
            }
        )
    }
}

@Composable
fun ScreenContent(showList: Boolean, modifier: Modifier = Modifier, navController: NavHostController) {

    val context = LocalContext.current
    val factory = ViewModelFactory(context)
    val viewModel: MainViewModel = viewModel(factory = factory)

    val data by viewModel.data
    val status by viewModel.status.collectAsState()

    val dataStore = remember { UserDataStore(context) }
    val userFlow = remember { dataStore.userFlow }
    val user by userFlow.collectAsState(initial = User())

    LaunchedEffect(user.email, user.idToken) {
        if (user.email.isNotEmpty()) {
            if (user.idToken.isEmpty()) {

                if (user.email == "mockuser@example.com" || user.email.startsWith("mock")) {
                    dataStore.saveData(user.copy(idToken = "mock_token_123"))
                } else {

                    dataStore.saveData(User())
                }
            } else {
                viewModel.retrieveData(user.idToken)
            }
        }
    }

    if (user.email.isEmpty()) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Selamat datang di NyamList!",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Orange,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Silakan klik ikon profil di kanan atas untuk masuk menggunakan akun Google Anda.",
                style = MaterialTheme.typography.bodyMedium,
                color = Orange.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
        }
    } else {
        when (status) {
            ApiStatus.LOADING -> {
                Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Orange)
                }
            }
            ApiStatus.FAILED -> {
                Column(modifier = modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = viewModel.errorMessage.value ?: "Terjadi kesalahan koneksi", color = Orange)
                    Button(onClick = { viewModel.retrieveData(user.idToken) }, modifier = Modifier.padding(top = 16.dp), colors = ButtonDefaults.buttonColors(containerColor = Orange)) {
                        Text("Coba Lagi", color = Cream)
                    }
                }
            }
            ApiStatus.SUCCESS -> {
                if (showList) {
                    LazyColumn(modifier = modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 84.dp)) {
                        item { AddListItem { navController.navigate(Screen.FormBaru.route) } }
                        items(data) { wishlist ->
                            ListItem(wishlist = wishlist) { navController.navigate(Screen.FormUbah.withId(wishlist.id)) }
                        }
                    }
                } else {
                    LazyVerticalStaggeredGrid(
                        modifier = modifier.fillMaxSize(),
                        columns = StaggeredGridCells.Fixed(2),
                        verticalItemSpacing = 8.dp,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(8.dp, 8.dp, 8.dp, 84.dp)
                    ) {
                        item { AddGridItem { navController.navigate(Screen.FormBaru.route) } }
                        items(data) { wishlist ->
                            GridItem(wishlist = wishlist) { navController.navigate(Screen.FormUbah.withId(wishlist.id)) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddListItem(onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp).clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Cream.copy(alpha = 0.7f)),
        border = BorderStroke(1.dp, Orange.copy(alpha = 0.5f))
    ) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
            Icon(Icons.Default.Add, contentDescription = "Tambah", tint = Orange, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text("Tambah Wishlist Baru", color = Orange, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun AddGridItem(onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().height(200.dp).clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Cream.copy(alpha = 0.7f)),
        border = BorderStroke(1.dp, Orange.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Add, contentDescription = "Tambah", tint = Orange, modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text("Tambah Wishlist", color = Orange, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ListItem(wishlist: Wishlist, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(8.dp).clickable { onClick() }, colors = CardDefaults.cardColors(containerColor = Cream), border = BorderStroke(1.dp, DividerDefaults.color)) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(wishlist.localImageUri ?: wishlist.imageUrl.toImageModel())
                    .crossfade(true).build(),
                contentDescription = "Gambar",
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.loading_img),
                error = painterResource(id = R.drawable.broken_image),
                modifier = Modifier.size(100.dp).padding(end = 12.dp)
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.LocationOn, contentDescription = "Kota", tint = Orange)
                    Text(text = wishlist.kota, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 8.dp), maxLines = 1, overflow = TextOverflow.Ellipsis, color = Orange)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Fastfood, contentDescription = "Makanan", tint = Orange)
                    Text(text = wishlist.makanan, modifier = Modifier.padding(start = 8.dp), maxLines = 1, overflow = TextOverflow.Ellipsis, color = Orange)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.RestaurantMenu, contentDescription = "Resto", tint = Orange)
                    Text(text = wishlist.resto, modifier = Modifier.padding(start = 8.dp), maxLines = 1, overflow = TextOverflow.Ellipsis, color = Orange)
                }
                Text(text = wishlist.status, color = Orange)
                Text(text = wishlist.tanggal, maxLines = 1, overflow = TextOverflow.Ellipsis, color = Orange)
            }
        }
    }
}

private suspend fun signIn(context: Context, dataStore: UserDataStore) {
    val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId(BuildConfig.API_KEY)
        .build()

    val request: GetCredentialRequest = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

    try {
        val credentialManager = CredentialManager.create(context)
        val result = credentialManager.getCredential(context, request)
        handleSignIn(result, dataStore)
    } catch (e: GetCredentialException) {
        Log.e("SIGN-IN", "Error: ${e.errorMessage}. Falling back to mock login.")
        dataStore.saveData(User("Mock User", "mockuser@example.com", "", "mock_token_123"))
        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
            android.widget.Toast.makeText(context, "Google Sign-In tidak tersedia. Masuk sebagai Mock User.", android.widget.Toast.LENGTH_LONG).show()
        }
    }
}

private suspend fun handleSignIn(
    result: GetCredentialResponse,
    dataStore: UserDataStore
) {
    val credential = result.credential
    if (credential is CustomCredential &&
        credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL){
        try {
            val googleId = GoogleIdTokenCredential.createFrom(credential.data)
            val nama = googleId.displayName ?: ""
            val email = googleId.id
            val photoUrl = googleId.profilePictureUri.toString()
            dataStore.saveData(User(nama, email, photoUrl, googleId.idToken))
        } catch (e: GoogleIdTokenParsingException) {
            Log.e("SIGN-IN", "Error: ${e.message}")
        }
    }
    else {
        Log.e("SIGN-IN", "Error: unrecognized custom credential type.")
    }
}

private suspend fun signOut(context: Context, dataStore: UserDataStore){
    try {
        val credentialManager = CredentialManager.create(context)
        credentialManager.clearCredentialState(
            ClearCredentialStateRequest()
        )
        dataStore.saveData(User())
    }catch (e: ClearCredentialException){
        Log.e("SIGN-IN", "Error: ${e.errorMessage}")
    }
}

@Composable
fun GridItem(wishlist: Wishlist, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable { onClick() }, colors = CardDefaults.cardColors(containerColor = Cream), border = BorderStroke(1.dp, DividerDefaults.color)) {
        Column {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(wishlist.localImageUri ?: wishlist.imageUrl.toImageModel())
                    .crossfade(true).build(),
                contentDescription = "Gambar",
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.loading_img),
                error = painterResource(id = R.drawable.broken_image),
                modifier = Modifier.fillMaxWidth().height(140.dp)
            )
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.LocationOn, contentDescription = "Kota", tint = Orange, modifier = Modifier.size(18.dp))
                    Text(text = wishlist.kota, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 8.dp), maxLines = 1, overflow = TextOverflow.Ellipsis, color = Orange)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Fastfood, contentDescription = "Makanan", tint = Orange, modifier = Modifier.size(18.dp))
                    Text(text = wishlist.makanan, modifier = Modifier.padding(start = 8.dp), maxLines = 1, overflow = TextOverflow.Ellipsis, color = Orange)
                }
            }
        }
    }
}

private fun getCroppedImage(
    resolver: ContentResolver,
    result: CropImageView.CropResult
): Bitmap? {
    if (!result.isSuccessful) {
        Log.e("IMAGE", "Error: ${result.error}")
        return null
    }

    val uri = result.uriContent ?: return null

    return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
        MediaStore.Images.Media.getBitmap(resolver, uri)
    } else {
        val source = ImageDecoder.createSource(resolver, uri)
        ImageDecoder.decodeBitmap(source)
    }
}