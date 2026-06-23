package com.putri0010.nyamlist.ui.screen

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.putri0010.nyamlist.R
import com.putri0010.nyamlist.model.Wishlist
import com.putri0010.nyamlist.navigation.Screen
import com.putri0010.nyamlist.ui.theme.Cream
import com.putri0010.nyamlist.ui.theme.NyamListTheme
import com.putri0010.nyamlist.ui.theme.Orange
import com.putri0010.nyamlist.util.SettingsDataStore
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

    val dataStore = SettingsDataStore(LocalContext.current)
    val showList by dataStore.layoutFlow.collectAsState(true)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.app_name),
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Orange,
                    titleContentColor = Cream
                ),
                actions = {
                    IconButton(
                        onClick = {
                            CoroutineScope(Dispatchers.IO).launch {
                                dataStore.saveLayout(!showList)
                            }
                        }
                    ) {
                        Icon(
                            painter = painterResource(
                                if (showList)
                                    R.drawable.outline_grid_view_24
                                else
                                    R.drawable.outline_lists_24
                            ),
                            contentDescription = stringResource(
                                if (showList)
                                    R.string.grid
                                else
                                    R.string.list
                            ),
                            tint = Cream
                        )
                    }
                    IconButton(
                        onClick = {
                            navController.navigate(
                                Screen.RecycleBin.route
                            )
                        }
                    ) {

                        Icon(
                            painter = painterResource(
                                R.drawable.outline_delete_24
                            ),
                            contentDescription = "Recycle Bin",
                            tint = Cream
                        )
                    }
                }
            )
        },

        floatingActionButton = {

            FloatingActionButton(
                onClick = {
                    navController.navigate(Screen.FormBaru.route)
                },
                containerColor = Orange
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.tambah_wishlist),
                    tint = Cream
                )
            }
        }

    ) { innerPadding ->

        ScreenContent(
            showList = showList,
            modifier = Modifier.padding(innerPadding),
            navController = navController
        )
    }
}

@Composable
fun ScreenContent(
    showList: Boolean,
    modifier: Modifier = Modifier,
    navController: NavHostController
) {

    val context = LocalContext.current
    val factory = ViewModelFactory(context)
    val viewModel: MainViewModel = viewModel(factory = factory)
    val data by viewModel.data.collectAsState()

    if (data.isEmpty()) {

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.list_kosong),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = Orange
            )
        }

    } else {
        if (showList) {
            LazyColumn(
                modifier = modifier.fillMaxSize(),

                contentPadding = PaddingValues(bottom = 84.dp)
            ) {
                items(data) {
                    ListItem(
                        wishlist = it
                    ) {
                        navController.navigate(
                            Screen.FormUbah.withId(it.id)
                        )
                    }
                    HorizontalDivider()
                }
            }
        } else {
            LazyVerticalStaggeredGrid(
                modifier = modifier.fillMaxSize(),
                columns = StaggeredGridCells.Fixed(2),
                verticalItemSpacing = 8.dp,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(
                    8.dp,
                    8.dp,
                    8.dp,
                    84.dp
                )
            ) {
                items(data) {
                    GridItem(
                        wishlist = it
                    ) {
                        navController.navigate(
                            Screen.FormUbah.withId(it.id)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ListItem(
    wishlist: Wishlist,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },

        colors = CardDefaults.cardColors(
            containerColor = Cream
        ),

        border = BorderStroke(
            1.dp,
            DividerDefaults.color
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
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
    }
}

@Composable
fun GridItem(
    wishlist: Wishlist,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },

        colors = CardDefaults.cardColors(
            containerColor = Cream
        ),

        border = BorderStroke(
            1.dp,
            DividerDefaults.color
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
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
    }
}