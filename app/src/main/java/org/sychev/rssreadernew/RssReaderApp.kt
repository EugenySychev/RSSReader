package org.sychev.rssreadernew

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.launch
import org.sychev.rssreadernew.navigation.Destination
import org.sychev.rssreadernew.navigation.drawerDestinations
import org.sychev.rssreadernew.screens.news.NewsDetailScreen
import org.sychev.rssreadernew.screens.news.NewsScreen
import org.sychev.rssreadernew.screens.news.NewsScreenAction
import org.sychev.rssreadernew.screens.news.NewsScreenViewModel
import org.sychev.rssreadernew.screens.settings.SettingsScreen
import org.sychev.rssreadernew.screens.sources.SourcesScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RssReaderApp() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val isDetailRoute = currentRoute == Destination.NewsDetail.route

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                drawerDestinations.forEach { destination ->
                    NavigationDrawerItem(
                        label = { Text(destination.label) },
                        selected = currentRoute == destination.route,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
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
                        val title = if (isDetailRoute) {
                            Destination.NewsDetail.label
                        } else {
                            drawerDestinations.firstOrNull { it.route == currentRoute }?.label.orEmpty()
                        }
                        Text(title)
                    },
                    navigationIcon = {
                        if (isDetailRoute) {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                            }
                        } else {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
                            }
                        }
                    },
                )
            },
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Destination.News.route,
                modifier = Modifier.padding(innerPadding),
            ) {
                composable(Destination.News.route) {
                    NewsScreen(
                        onAction = { action ->
                            if (action is NewsScreenAction.OpenItem) {
                                navController.navigate(Destination.NewsDetail.createRoute(action.item.link))
                            }
                        },
                    )
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
