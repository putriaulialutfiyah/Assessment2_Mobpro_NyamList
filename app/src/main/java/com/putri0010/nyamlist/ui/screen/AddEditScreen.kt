package com.putri0010.nyamlist.ui.screen

import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.putri0010.nyamlist.R
import com.putri0010.nyamlist.model.User
import com.putri0010.nyamlist.model.toImageModel
import com.putri0010.nyamlist.network.UserDataStore
import com.putri0010.nyamlist.ui.theme.Cream
import com.putri0010.nyamlist.ui.theme.NyamListTheme
import com.putri0010.nyamlist.ui.theme.Orange
import com.putri0010.nyamlist.util.ViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditScreen(navController: NavHostController, id: Long? = null) {
    val context = LocalContext.current
    val factory = ViewModelFactory(context)
    val viewModel: AddEditViewModel = viewModel(factory = factory)

    val dataStore = remember { UserDataStore(context) }
    val userFlow = remember { dataStore.userFlow }
    val user by userFlow.collectAsState(initial = User())


    var kota by remember { mutableStateOf("") }
    var makanan by remember { mutableStateOf("") }
    var resto by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var existingImageUrl by remember { mutableStateOf<String?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
        uri?.let {
            bitmap = it.toBitmap(context)
        }
    }

    LaunchedEffect(user.idToken) {
        if (id == null || user.idToken.isEmpty()) return@LaunchedEffect
        val data = viewModel.getWishlist(user.idToken, id) ?: return@LaunchedEffect
        kota = data.kota
        makanan = data.makanan
        resto = data.resto
        status = data.status
        existingImageUrl = data.imageUrl
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.kembali),
                            tint = Cream
                        )
                    }
                },
                title = {
                    Text(
                        text = stringResource(id = if (id == null) R.string.tambah_wishlist else R.string.edit_wishlist),
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Orange,
                    titleContentColor = Cream,
                ),
                actions = {
                    IconButton(onClick = {
                        if (kota == "" || makanan == "" || resto == "") {
                            Toast.makeText(context, R.string.invalid, Toast.LENGTH_LONG).show()
                            return@IconButton
                        }
                        if (user.idToken.isEmpty()) {
                            Toast.makeText(context, "Silakan login terlebih dahulu", Toast.LENGTH_LONG).show()
                            return@IconButton
                        }
                        if (id == null) {
                            if (bitmap == null) {
                                Toast.makeText(context, "Pilih gambar terlebih dahulu", Toast.LENGTH_LONG).show()
                                return@IconButton
                            }
                            viewModel.insert(user.idToken, kota, makanan, resto, status, bitmap) {
                                navController.popBackStack()
                            }
                        } else {
                            viewModel.update(user.idToken, id, kota, makanan, resto, status, false, bitmap) {
                                navController.popBackStack()
                            }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.Check,
                            contentDescription = stringResource(R.string.simpan),
                            tint = Cream
                        )
                    }
                    if (id != null) {
                        DeleteAction {
                            showDialog = true
                        }
                    }
                }
            )
        }
    ) { padding ->
        FormWishlist(
            city = kota,
            onCityChange = { kota = it },
            food = makanan,
            onFoodChange = { makanan = it },
            restaurant = resto,
            onRestaurantChange = { resto = it },
            status = status,
            onStatusChange = { status = it },
            selectedImageUri = imageUri,
            existingImageUrl = existingImageUrl,
            onSelectImageClick = { launcher.launch("image/*") },
            modifier = Modifier.padding(padding)
        )
        if (id != null && showDialog && user.idToken.isNotEmpty()) {
            DisplayAlertDialog(
                onDismissRequest = { showDialog = false }) {
                showDialog = false
                viewModel.delete(user.idToken, id) {
                    navController.popBackStack()
                }
            }
        }
    }
}

@Composable
fun DeleteAction(delete: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    IconButton(onClick = { expanded = true }) {
        Icon(
            imageVector = Icons.Filled.MoreVert,
            contentDescription = stringResource(R.string.lainnya),
            tint = Cream
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = {
                    Text(text = stringResource(id = R.string.hapus))
                },
                onClick = {
                    expanded = false
                    delete()
                }
            )
        }
    }
}

@Composable
fun FormWishlist(
    city: String, onCityChange: (String) -> Unit,
    food: String, onFoodChange: (String) -> Unit,
    restaurant: String, onRestaurantChange: (String) -> Unit,
    status: String, onStatusChange: (String) -> Unit,
    selectedImageUri: Uri?,
    existingImageUrl: String?,
    onSelectImageClick: () -> Unit,
    modifier: Modifier
) {
    val radioOptions = listOf("Sudah dikunjungi", "Belum dikunjungi")
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Image picker card placed at the very top of the form
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clickable { onSelectImageClick() },
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Cream.copy(alpha = 0.5f)),
            border = BorderStroke(1.dp, Orange.copy(alpha = 0.5f))
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                if (selectedImageUri != null) {
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = "Selected Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else if (!existingImageUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = existingImageUrl.toImageModel(),
                        contentDescription = "Existing Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Image",
                            tint = Orange,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Pilih Gambar dari Galeri", color = Orange, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        OutlinedTextField(
            value = city,
            onValueChange = { onCityChange(it) },
            label = {
                Text(
                    text = stringResource(R.string.kota),
                    color = Orange
                )
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Orange,
                unfocusedBorderColor = Orange.copy(alpha = 0.5f)
            )
        )
        OutlinedTextField(
            value = food,
            onValueChange = { onFoodChange(it) },
            label = {
                Text(
                    text = stringResource(R.string.makanan),
                    color = Orange
                )
            },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences
            ),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Orange,
                unfocusedBorderColor = Orange.copy(alpha = 0.5f)
            )
        )
        OutlinedTextField(
            value = restaurant,
            onValueChange = { onRestaurantChange(it) },
            label = {
                Text(
                    text = stringResource(R.string.resto),
                    color = Orange
                )
            },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences
            ),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Orange,
                unfocusedBorderColor = Orange.copy(alpha = 0.5f)
            )
        )
        OutlinedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(
                1.dp,
                Orange
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                radioOptions.forEach { text ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (text == status),
                                onClick = { onStatusChange(text) },
                                role = Role.RadioButton
                            )
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (text == status),
                            onClick = null,
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Orange,
                                unselectedColor = Orange.copy(alpha = 0.5f)
                            )
                        )
                        Text(
                            text = text,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 16.dp),
                            color = Orange
                        )
                    }
                }
            }
        }
    }
}

// Utility extension function to convert Uri to Bitmap
fun Uri.toBitmap(context: Context): Bitmap? {
    return try {
        if (Build.VERSION.SDK_INT < 28) {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.getBitmap(context.contentResolver, this)
        } else {
            val source = ImageDecoder.createSource(context.contentResolver, this)
            ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                decoder.isMutableRequired = true
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun AddEditScreenPreview() {
    NyamListTheme {
        AddEditScreen(rememberNavController())
    }
}