package com.example.moviefinder

import com.example.moviefinder.screen.SearchScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import com.example.moviefinder.screen.SignInScreen
import com.example.moviefinder.auth.AuthViewModel
import com.example.moviefinder.font.poppinsFamily
import com.example.moviefinder.screen.HistoryScreen
import com.example.moviefinder.screen.HomeScreen
import com.example.moviefinder.screen.LikeScreen
import com.example.moviefinder.screen.MovieDetailScreen
import com.example.moviefinder.screen.SignUpScreen
import com.example.moviefinder.screen.UserProfileScreen
import com.example.moviefinder.ui.theme.MovieFinderTheme

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
    val items = listOf("home", "search", "like", "user-profile", "history")
    val iconsmenu = listOf(
        Icons.Default.Home,
        Icons.Default.Search,
        Icons.Default.Favorite,
        Icons.Default.AccountBox,
        Icons.Default.History
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val authVM = viewModel<AuthViewModel>()
    val user = authVM.currentUser
    val startDestination = if (authVM.isLoggedIn) "home" else "signin"
    val defaultAvatar = "https://i.pinimg.com/736x/9e/83/75/9e837528f01cf3f42119c5aeeed1b336.jpg"

    Scaffold(
        topBar = {
            if (currentRoute !in listOf("signin", "signup") && user != null) TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1A1919),
                    titleContentColor = Color(0xFFFEFFFF)
                ),
                title = { Text("Movie Finder", fontFamily = poppinsFamily) },
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = modifier.clickable { navController.navigate("user-profile") }
                    ) {
                        Text("${user.displayName ?: user.email}",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontFamily = poppinsFamily,
                        )
                        Spacer(modifier = modifier.width(12.dp))
                        AsyncImage(
                            model = user.photoUrl ?: defaultAvatar,
                            contentDescription = user.displayName ?: "Profile user",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(50.dp))
                                .border(0.8.dp, Color(0xC6FFFFFF), RoundedCornerShape(50.dp))
                        )
                        Spacer(modifier = modifier.width(12.dp))
                    }
                }
            )
        },
        bottomBar = {
            if (currentRoute !in listOf("signin", "signup")) NavigationBar(
                containerColor = Color(0xFF1A1919),
                contentColor = Color(0xFFFEFFFF)
            ) {
                Row(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(horizontal = 5.dp)
                ) {
                    items.forEachIndexed { index, item ->
                        NavigationBarItem (
                            icon = { Icon(
                                imageVector = iconsmenu[index],
                                contentDescription = item,
                                modifier = Modifier.size(30.dp)
                            )},
                            selected = selectedItem == index,
                            onClick = { selectedItem = index
                                when(index) {
                                    0 -> navController.navigate("home")
                                    1 -> navController.navigate("search")
                                    2 -> navController.navigate("like")
                                    3 -> navController.navigate("user-profile")
                                    4 -> navController.navigate("history")
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.White,
                                unselectedIconColor = Color.White,
                                indicatorColor = Color(0xFF3D3D3D)
                            ),
                            modifier = modifier.clip(RoundedCornerShape(60.dp))

                        )
                    }
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
            composable(route = "home") {
                HomeScreen(navController = navController)
            }
            composable(route = "search"){
                SearchScreen(navController = navController)
            }
            composable(
                route = "movie-detail/{id}",
                arguments = listOf(navArgument("id") { type = NavType.StringType })
            ){
                stackEntry -> val id = stackEntry.arguments?.getString("id") ?: ""
                MovieDetailScreen(
                    id = id,
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
            composable (route = "like"){ LikeScreen(navController = navController) }
            composable (route = "user-profile") {
                UserProfileScreen(navController = navController)
            }
            composable(route = "history") { HistoryScreen() }
        }
    }
}