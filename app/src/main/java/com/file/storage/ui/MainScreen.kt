package com.file.storage.ui

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.file.storage.core.model.FileType
import com.file.storage.feature.files.FileFormScreen
import com.file.storage.feature.files.FileListScreen
import com.file.storage.feature.files.SettingsDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()

    var showSettingsDialog by remember { mutableStateOf(false) }

    val backStackEntry by navController.currentBackStackEntryAsState()

    val currentRoute = backStackEntry?.destination?.route

    val canNavigateBack by remember(currentRoute) {
        derivedStateOf {
            currentRoute in listOf("add_file/{type}", "edit_file/{fileId}")
        }
    }

    val currentScreenTitle by remember(currentRoute) {
        derivedStateOf {
            when (currentRoute) {
                Screen.Claims.route -> "Claims"
                Screen.Documents.route -> "Documents"
                "add_file/{type}" -> "Add File"
                "edit_file/{fileId}" -> "Edit File"
                else -> "File Storage"
            }
        }
    }

    val showFab by remember(currentRoute) {
        derivedStateOf {
            currentRoute in listOf(Screen.Claims.route, Screen.Documents.route)
        }
    }

    val showBottomBar by remember(currentRoute) {
        derivedStateOf {
            currentRoute in listOf(Screen.Claims.route, Screen.Documents.route)
        }
    }

    val items = listOf(
        Screen.Claims,
        Screen.Documents
    )

    Scaffold(
        topBar = {
            AppTopBar(
                title = currentScreenTitle,
                canNavigateBack =  canNavigateBack,
                onNavigateUp = { navController.popBackStack()  },
                onActionUp = { showSettingsDialog = true },
            )
        },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination
                    items.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = null) },
                            label = { Text(screen.resourceId) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if(showFab){
                FloatingActionButton(
                    onClick = {
                        val currentRoute = navController.currentBackStackEntry?.destination?.route
                        val type =
                            if (currentRoute == Screen.Documents.route) "DOCUMENT" else "CLAIM"
                        navController.navigate("add_file/$type")
                    }
                ) {
                    Icon(Icons.Filled.Add, "Add")
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Claims.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Claims.route) {
                FileListScreen(
                    type = FileType.CLAIM,
                    onItemClick = { id -> navController.navigate("edit_file/$id") }
                )
            }
            composable(Screen.Documents.route) {
                FileListScreen(
                    type = FileType.DOCUMENT,
                    onItemClick = { id -> navController.navigate("edit_file/$id") }
                )
            }
            composable(
                route = "add_file/{type}",
                arguments = listOf(navArgument("type") { type = NavType.StringType })
            ) { backStackEntry ->
                val typeStr = backStackEntry.arguments?.getString("type") ?: "CLAIM"
                val type = FileType.valueOf(typeStr)
                FileFormScreen(
                    initialType = type,
                    fileId = null,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(
                route = "edit_file/{fileId}",
                arguments = listOf(navArgument("fileId") { type = NavType.StringType })
            ) { backStackEntry ->
                val fileId = backStackEntry.arguments?.getString("fileId")
                FileFormScreen(
                    initialType = FileType.CLAIM,
                    fileId = fileId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
    if (showSettingsDialog) {
        SettingsDialog(
            onDismissRequest = { showSettingsDialog = false }
        )
    }
}

sealed class Screen(
    val route: String,
    val resourceId: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    object Claims : Screen("claims", "Claims", Icons.Filled.Receipt)
    object Documents : Screen("documents", "Documents", Icons.Filled.Description)
}
