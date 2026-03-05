package com.example.moviefinder.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WhereToVote
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.moviefinder.auth.AuthViewModel
import com.example.moviefinder.firebase.FavoriteViewModel
import com.example.moviefinder.firebase.MovieViewModel
import com.example.moviefinder.model.Favorite
import com.example.moviefinder.model.Movie

@Composable
fun MovieDetailScreen(
    id: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
){
    val movieVM = viewModel<MovieViewModel>()
    val movie by movieVM.getById(id).collectAsState(initial = null)

    if (movie == null) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.fillMaxSize().padding(top = 320.dp),
        ){
            CircularProgressIndicator(
                color = Color.Black,
                modifier = modifier.size(50.dp)
            )
        }
    } else {
        val movieData = movie as Movie
        val scrollState = rememberScrollState()
        val authVM = viewModel<AuthViewModel>()
        val userId = authVM.currentUser?.uid ?: ""

        val favoriteVM = viewModel<FavoriteViewModel>()
        val favorites by favoriteVM.getAllById(userId).collectAsState(initial = emptyList())
        var isLiked = favorites.any { fav -> fav.movieId == movieData.id.toString() }

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(scrollState)
        ) {
            AsyncImage(
                model = "https://image.tmdb.org/t/p/original/${movieData.poster_path}",
                contentDescription = movie!!.title,
                contentScale = ContentScale.Crop,
                modifier = modifier
                    .fillMaxWidth()
                    .height(500.dp)
            )

            Spacer(modifier.height(20.dp))
            Text(movieData.title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier.height(10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = modifier.fillMaxWidth(),
            ) {
                IconButton(
                    onClick = {},
                    colors = IconButtonDefaults
                        .iconButtonColors(contentColor = Color(0xFFFFC107)),
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        modifier = modifier.size(35.dp)
                    )
                }
                Text("Rating: ${movieData.vote_average}", fontSize = 16.sp)
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = modifier.fillMaxWidth(),
            ) {
                IconButton(
                    onClick = {},
                    colors = IconButtonDefaults
                        .iconButtonColors(contentColor = Color(0xFF4CAF50)),
                ) {
                    Icon(
                        imageVector = Icons.Default.WhereToVote,
                        contentDescription = "Vote",
                        modifier = modifier.size(31.dp)
                    )
                }
                Text("Vote: ${movieData.vote_count}", fontSize = 16.sp)
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = modifier.fillMaxWidth(),
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = "Release date",
                    modifier = modifier.size(31.dp)
                )
                Text("Release Date: ${movieData.release_date}", fontSize = 16.sp)
            }
            movieData.genres.let {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = modifier.fillMaxWidth()
                ) {
                    movieData.genres.forEach { genres ->
                        OutlinedButton(
                            onClick = {},
                            modifier = modifier.padding(5.dp)
                        ) {
                            Text("${genres.name}")
                        }
                    }
                }
            }
            Spacer(modifier.height(12.dp))
            Text("${movieData.overview}",
                modifier = modifier.fillMaxWidth(),
                fontSize = 17.sp,
                lineHeight = 25.sp,
            )
            Spacer(modifier.height(20.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = modifier.fillMaxWidth(),
            ){
                IconButton(
                    onClick = onBack,
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = "Back to home",
                        modifier = modifier.size(31.dp)
                    )
                }
                IconButton(
                    onClick = {
                        isLiked = !isLiked

                        if (isLiked) {
                            favoriteVM.insertFavorite(
                                Favorite(
                                    userId = userId,
                                    movieId = movieData.id.toString(),
                                    isLiked = isLiked
                                )
                            )
                        } else {
                            favoriteVM.deleteFavorite(
                                userId = userId,
                                movieId = movieData.id.toString()
                            )
                        }}
                ) {
                    Icon(
                        imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        tint = if (isLiked) Color.Red else Color.Black,
                        contentDescription = "Like",
                        modifier = modifier.size(35.dp)
                    )
                }
            }
        }
    }
}