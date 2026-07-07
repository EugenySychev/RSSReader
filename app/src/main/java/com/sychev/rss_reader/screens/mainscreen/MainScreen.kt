package com.sychev.rss_reader

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import com.sychev.rss_reader.events.AppEvent
import com.sychev.rss_reader.events.AppProvider
import com.sychev.rss_reader.events.rememberAppEventEmitter
import com.sychev.rss_reader.navigation.Destination
import com.sychev.rss_reader.navigation.drawerDestinations
import com.sychev.rss_reader.screens.mainscreen.MainScreenActions
import com.sychev.rss_reader.screens.mainscreen.MainScreenViewModel
import com.sychev.rss_reader.screens.news.NewsDetailScreen
import com.sychev.rss_reader.screens.news.NewsScreen
import com.sychev.rss_reader.screens.news.NewsScreenViewModel
import com.sychev.rss_reader.screens.news.NewsSortOrder
import com.sychev.rss_reader.screens.settings.SettingsScreen
import com.sychev.rss_reader.screens.sources.SourcesScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MainScreen(
    viewModel: MainScreenViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val isDetailRoute = currentRoute == Destination.NewsDetail.route

    LaunchedEffect(navController) {
        viewModel.setNavController(navController)
    }

    AppProvider {
        val emitAppEvent = rememberAppEventEmitter()
        var isSearchActive by remember { mutableStateOf(false) }
        var searchQuery by remember { mutableStateOf("") }

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    NavigationDrawerItem(
                        label = { Text(Destination.News.label) },
                        selected = currentRoute == Destination.News.route,
                        shape = RectangleShape,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(Destination.News.route)
                        }
                    )
                    uiState.sourceList.forEach { sourceItem ->
                        NavigationDrawerItem(
                            label = {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(
                                        dimensionResource(R.dimen.news_item_padding),
                                    ),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    if (sourceItem.imageUrl != null) {
                                        AsyncImage(
                                            model = sourceItem.imageUrl,
                                            contentDescription = sourceItem.name,
                                            modifier = Modifier
                                                .height(dimensionResource(R.dimen.source_item_image_height))
                                                .width(dimensionResource(R.dimen.source_item_image_width))
                                                .background(MaterialTheme.colorScheme.surfaceVariant),
                                            contentScale = ContentScale.Fit,
                                        )
                                    }
                                    Text(sourceItem.name)
                                }
                            },
                            selected = currentRoute == Destination.NewsSource.route && backStackEntry?.arguments?.getString("link") == sourceItem.url,
                            shape = RectangleShape,
                            onClick = {
                                scope.launch { drawerState.close() }
                                viewModel.onAction(MainScreenActions.OnNavigateFromMenu(Destination.News.route, sourceItem.url))
                            }
                        )
                    }
                    drawerDestinations.forEach { destination ->
                        NavigationDrawerItem(
                            label = { Text(destination.label) },
                            selected = currentRoute == destination.route,
                            shape = RectangleShape,
                            onClick = {
                                scope.launch { drawerState.close() }
                                viewModel.onAction(
                                    MainScreenActions.OnNavigateFromMenu(
                                        destination.route,
                                        ""
                                    ),
                                )
                            },
                        )
                    }
                }
            },
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        title = {
                            if (isSearchActive) {
                                TextField(
                                    value = searchQuery,
                                    onValueChange = {
                                        searchQuery = it
                                        emitAppEvent(AppEvent.SearchNews(it))
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    placeholder = { Text("Search") },
                                    singleLine = true,
                                    trailingIcon = {
                                        IconButton(
                                            onClick = {
                                                searchQuery = ""
                                                isSearchActive = false
                                                emitAppEvent(AppEvent.SearchNews(""))
                                            }
                                        ) {
                                            Icon(imageVector = Icons.Default.Close, contentDescription = "Clear")
                                        }
                                    },
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent,
                                    ),
                                )
                            } else {
                                val title = when {
                                    isDetailRoute -> Destination.NewsDetail.label
                                    currentRoute == Destination.News.route || currentRoute?.startsWith("news/") == true -> Destination.News.label
                                    else -> drawerDestinations.firstOrNull { it.route == currentRoute }?.label.orEmpty()
                                }
                                Text(title)
                            }
                        },
                        navigationIcon = {
                            when {
                                isSearchActive -> {
                                    IconButton(
                                        onClick = {
                                            isSearchActive = false
                                            searchQuery = ""
                                        },
                                    ) {
                                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Close search")
                                    }
                                }
                                isDetailRoute -> {
                                    IconButton(onClick = { navController.popBackStack() }) {
                                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                                    }
                                }
                                else -> {
                                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                        Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
                                    }
                                }
                            }
                        },

                        actions = {
                            if (isDetailRoute || isSearchActive) return@TopAppBar
                            var sortMenuExpanded by remember { mutableStateOf(false) }
                            IconButton(onClick = { sortMenuExpanded = true }) {
                                Icon(painter = painterResource(R.drawable.baseline_sort_24), contentDescription = "Sort")
                            }
                            DropdownMenu(
                                expanded = sortMenuExpanded,
                                onDismissRequest = { sortMenuExpanded = false },
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Last to Old") },
                                    onClick = {
                                        sortMenuExpanded = false
                                        emitAppEvent(AppEvent.SortNews(NewsSortOrder.NEWEST_FIRST))
                                    },
                                )
                                DropdownMenuItem(
                                    text = { Text("Old to Last") },
                                    onClick = {
                                        sortMenuExpanded = false
                                        emitAppEvent(AppEvent.SortNews(NewsSortOrder.OLDEST_FIRST))
                                    },
                                )
                            }
                            IconButton(onClick = { isSearchActive = true }) {
                                Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                            }
                            IconButton(onClick = { emitAppEvent(AppEvent.RefreshNews) }) {
                                Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh")
                            }
                        }
                    )
                },
            ) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = Destination.News.route,
                    modifier = Modifier.padding(innerPadding),
                ) {
                    composable(
                        route = Destination.News.route,
                    ) {
                        NewsScreen(link = "", navController = navController)
                    }
                    composable(
                        route = Destination.NewsSource.route,
                        arguments = listOf(navArgument("link") { type = NavType.StringType }),
                    ) { entry ->
                        val link = entry.arguments?.getString("link").orEmpty()
                        NewsScreen(link = link, navController = navController)
                    }
                    composable(Destination.Sources.route) { SourcesScreen() }
                    composable(Destination.Settings.route) { SettingsScreen() }
                    composable(
                        route = Destination.NewsDetail.route,
                        arguments = listOf(navArgument("link") { type = NavType.StringType }),
                    ) { entry ->
                        val link = entry.arguments?.getString("link").orEmpty()
                        val newsBackStackEntry = remember(entry) {
                            navController.getBackStackEntry(Destination.News.route)
                        }
                        NewsDetailScreen(
                            link = link,
                            newsViewModel = hiltViewModel<NewsScreenViewModel>(newsBackStackEntry),
                        )
                    }
                }
            }
        }
    }
}
