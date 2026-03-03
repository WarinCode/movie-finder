package com.example.moviefinder

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AssistantPhoto
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.firebasekotlin.screen.SignInScreen
import com.example.moviefinder.auth.AuthViewModel
import com.example.moviefinder.screen.HistoryScreen
import com.example.moviefinder.screen.HomeScreen
import com.example.moviefinder.screen.LikeScreen
import com.example.moviefinder.screen.SignUpScreen
import com.example.moviefinder.ui.theme.MovieFinderTheme
import com.example.moviefinder.util.dotenv

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MovieFinderTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    LayoutScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LayoutScreen(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    var selectedItem by remember { mutableStateOf(0) }
    val items = listOf("Home", "Like", "History")
    val iconsmenu = listOf(Icons.Default.Home, Icons.Default.AssistantPhoto, Icons.Default.History)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val authVM = viewModel<AuthViewModel>()
    val startDestination = if (authVM.isLoggedIn) "home" else "signin"

    Scaffold(
        topBar = {
            if (currentRoute !in listOf("signin", "signup")) TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1A1919),
                    titleContentColor = Color(0xFFFEFFFF)
                ),
                title = { Text("Movie Finder") },
                actions = {
                    IconButton(onClick = {
                        authVM.logout()
                        navController.navigate("signin")
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Sign out",
                            tint = Color.White
                        )
                    }
                }
            )
        },
        bottomBar = {
            if (currentRoute !in listOf("signin", "signup")) NavigationBar(
                containerColor = Color(0xFF1A1919),
                contentColor = Color(0xFFFEFFFF)
            ) {
                items.forEachIndexed { index, item ->
                    NavigationBarItem (
                        icon = { Icon(iconsmenu[index], contentDescription = item, modifier = Modifier.size(30.dp)) },
                        selected = selectedItem == index,
                        onClick = { selectedItem = index
                            when(index) {
                                0 -> navController.navigate("home")
                                1 -> navController.navigate("like")
                                2 -> navController.navigate("history")
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            unselectedIconColor = Color.White,
                            indicatorColor = Color(0xFF3D3D3D)
                        ),
                    )
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable (route = "signin") {
                SignInScreen(
                    onLoginSuccess = {
                        navController.navigate("home") {
                            popUpTo("signin") { inclusive = true }
                        }
                    },
                    onNavigateToRegister = {
                        navController.navigate("signup")
                    }
                )
            }
            composable (route = "signup"){
                SignUpScreen(
                    onRegisterSuccess = {
                        navController.navigate("home") {
                            popUpTo("signup") { inclusive = true }
                        }
                    },
                    onNavigateToLogin = {
                        navController.navigate("signin")
                    }
                )
            }
            composable(route = "home") { HomeScreen() }
            composable(route = "history") { HistoryScreen() }
            composable (route = "like"){ LikeScreen() }
        }
    }
}