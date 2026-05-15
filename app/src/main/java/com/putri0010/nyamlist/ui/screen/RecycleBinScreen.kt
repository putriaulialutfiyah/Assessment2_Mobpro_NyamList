package com.putri0010.nyamlist.ui.screen

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.outlined.Restore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.putri0010.nyamlist.ui.theme.Cream
import com.putri0010.nyamlist.ui.theme.NyamListTheme
import com.putri0010.nyamlist.ui.theme.Orange
import com.putri0010.nyamlist.util.ViewModelFactory

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun RecycleBinScreenPreview() {

    NyamListTheme {
        RecycleBinScreen(rememberNavController())
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecycleBinScreen(
    navController: NavHostController
) {

    val context = LocalContext.current
    val factory = ViewModelFactory(context)
    val viewModel: RecycleBinViewModel =
        viewModel(factory = factory)
    val data by viewModel.data.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {
                        Icon(
                            imageVector =
                                Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = Cream
                        )
                    }
                },
                title = {
                    Text(
                        "Recycle Bin",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Orange,
                    titleContentColor = Cream
                )
            )
        }

    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            items(data) { wishlist ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),

                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {

                    Box(
                        modifier = Modifier.fillMaxWidth().background(Cream)
                    )  {

                        Column(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = "Kota",
                                    tint = Orange
                                )

                                Text(
                                    text = wishlist.kota,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(start = 8.dp),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    color = Orange
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Fastfood,
                                    contentDescription = "Makanan",
                                    tint = Orange
                                )

                                Text(
                                    text = wishlist.makanan,
                                    modifier = Modifier.padding(start = 8.dp),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    color = Orange
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.RestaurantMenu,
                                    contentDescription = "Resto",
                                    tint = Orange
                                )

                                Text(
                                    text = wishlist.resto,
                                    modifier = Modifier.padding(start = 8.dp),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    color = Orange
                                )
                            }
                            Text(
                                text = wishlist.status,
                                color = Orange
                            )
                            Text(
                                text = wishlist.tanggal,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = Orange
                            )
                        }

                        IconButton(
                            onClick = {
                                viewModel.restore(wishlist.id)
                            },
                            modifier = Modifier.align(Alignment.TopEnd)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Restore,
                                contentDescription = "Restore",
                                tint = Orange
                            )
                        }
                    }
                }
            }
        }
    }
}

