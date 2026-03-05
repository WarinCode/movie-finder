package com.example.moviefinder.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.moviefinder.auth.AuthViewModel
import com.example.moviefinder.firebase.FavoriteViewModel
import com.example.moviefinder.firebase.MovieViewModel
import com.example.moviefinder.model.Favorite

@Composable
fun LikeScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    val authVM  = viewModel<AuthViewModel>()
    val userId = authVM.currentUser?.uid ?: "";
    val favoriteVM  = viewModel<FavoriteViewModel>()
    val favorites by favoriteVM.getAllById(userId).collectAsState(initial = emptyList())
    val movieVM = viewModel<MovieViewModel>()

    if (favorites.isEmpty()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .fillMaxSize()
                .padding(top = 320.dp),
        ){
            Text("No favorites yet",
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = modifier.fillMaxWidth(),
            )
        }
    } else {
        Column (
            modifier = modifier
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text("My favorite movies",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = modifier
                    .fillMaxWidth()
                    .padding(top = 15.dp, bottom = 20.dp)
            )

            favorites.forEach { favorite ->
                val movie by movieVM.getById(favorite.movieId).collectAsState(initial = null)

                movie?.let { movieData ->
                    MovieCard(
                        movie = movieData,
                        userId = userId,
                        onNavigateToMovieDetail = {
                            navController.navigate("movie-detail/${movieData.id}")
                        }
                    )
                }
            }
        }
    }
}