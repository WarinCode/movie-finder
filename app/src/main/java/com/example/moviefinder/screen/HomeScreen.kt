package com.example.moviefinder.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.moviefinder.R
import com.example.moviefinder.firebase.MovieViewModel
import com.example.moviefinder.model.Movie
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.navigation.NavController
import com.example.moviefinder.auth.AuthViewModel
import com.example.moviefinder.firebase.FavoriteViewModel
import com.example.moviefinder.firebase.HistoryViewModel
import com.example.moviefinder.model.History
import com.google.firebase.Timestamp
import com.example.moviefinder.font.poppinsFamily

@Composable
fun HomeScreen(
    navController: NavController,
    modifier: Modifier = Modifier
){
    val authVM = viewModel<AuthViewModel>()
    val userId = authVM.currentUser?.uid ?: ""
    val movieVM = viewModel<MovieViewModel>()
    val movies by movieVM.movies.collectAsState(initial = emptyList())

    LazyColumn (
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        if (movies.isEmpty()) {
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = modifier.fillMaxSize().padding(top = 320.dp),
                ){
                    CircularProgressIndicator(
                        color = Color.Black,
                        modifier = modifier.size(50.dp)
                    )
                }
            }
        }

        items(
            items = movies.sortedBy { movie -> movie.poster_path !is String },
            key = { movie -> movie.id.toString() }
        ) { movie ->
            MovieCard(
                movie = movie,
                userId = userId,
                onNavigateToMovieDetail = {
                    navController.navigate("movie-detail/${movie.id}")
                }
            )
        }
    }
}

@Composable
fun MovieCard(
    movie: Movie,
    userId: String,
    onNavigateToMovieDetail: () -> Unit,
    modifier: Modifier = Modifier
){
    val historyVM = viewModel<HistoryViewModel>()

    Column (
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFF3F4F4))
            .border(1.dp, Color(0x74282828))
            .clickable {
                historyVM.insertHistory(
                    History(
                        userId = userId,
                        movieId = movie.id.toString(),
                        viewedAt = Timestamp.now()
                    ))
                onNavigateToMovieDetail()
            }
    ) {
        if (movie.poster_path != null) {
            AsyncImage(
                model = "https://image.tmdb.org/t/p/original/${movie.poster_path}",
                contentDescription = movie.title,
                contentScale = ContentScale.Crop,
                modifier = modifier.fillMaxSize()
            )
        } else {
            Image(
                painter = painterResource(R.drawable.image_not_found),
                contentDescription = movie.title,
                contentScale = ContentScale.Crop,
                modifier = modifier.fillMaxSize(),
            )
        }

        Column(modifier = modifier.padding(vertical = 25.dp)){
            Text(movie.title,
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold,
                modifier = modifier.padding(start = 22.dp),
                fontFamily = poppinsFamily
            )
            Spacer(modifier.height(15.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = modifier
                    .fillMaxWidth()
                    .padding(start = 7.dp),
            ) {
                IconButton(
                    onClick = {},
                    colors = IconButtonDefaults
                        .iconButtonColors(contentColor = Color(0xFFFFC107))
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        modifier = modifier.size(32.dp)
                    )
                }
                Text("${movie.vote_average}",
                    fontSize = 19.sp,
                    fontFamily = poppinsFamily
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = modifier
                    .fillMaxWidth()
                    .padding(start = 7.dp),
            ) {
                IconButton(
                    onClick = {},
                    colors = IconButtonDefaults
                        .iconButtonColors(contentColor = Color(0xFF2A2A2A))
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = "Release Date",
                        modifier = modifier.size(32.dp),
                    )
                }
                Text(movie.release_date,
                    fontSize = 19.sp,
                    fontFamily = poppinsFamily,
                )
            }
        }
    }
    Spacer(modifier.height(40.dp))
}

@Composable
fun HorizontalMovieCard(
    movie: Movie,
    userId: String,
    onNavigateToMovieDetail: () -> Unit,
    modifier: Modifier = Modifier
){
    val historyVM = viewModel<HistoryViewModel>()
    val favoriteVM = viewModel<FavoriteViewModel>()

    Row (
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFF3F4F4))
            .border(1.dp, Color(0x74282828))
            .height(150.dp)
            .clickable {
                historyVM.insertHistory(
                    History(
                        userId = userId,
                        movieId = movie.id.toString(),
                        viewedAt = Timestamp.now()
                    ))
                onNavigateToMovieDetail()
            }
    ) {
        if (movie.poster_path != null) {
            AsyncImage(
                model = "https://image.tmdb.org/t/p/original/${movie.poster_path}",
                contentDescription = movie.title,
                contentScale = ContentScale.Fit,
                modifier = modifier
                    .height(200.dp)
                    .width(100.dp),
            )
        } else {
            Image(
                painter = painterResource(R.drawable.image_not_found),
                contentDescription = movie.title,
                contentScale = ContentScale.Fit,
                modifier = modifier
                    .height(200.dp)
                    .width(100.dp),
            )
        }

        Column (
            verticalArrangement = Arrangement.Top,
            modifier = modifier
                .padding(11.dp)
                .fillMaxSize()
        ){
            Text(movie.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,

                modifier = modifier,
                fontFamily = poppinsFamily
            )
            Spacer(modifier.height(15.dp))
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = modifier.fillMaxWidth()
            ) {
                Column() {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            modifier = modifier.size(26.dp),
                            tint = Color(0xFFFFC107)
                        )
                        Text("${movie.vote_average}",
                            fontSize = 16.sp,
                            fontFamily = poppinsFamily,
                            modifier = modifier.padding(start = 6.dp)
                        )
                    }
                    Spacer(modifier.height(5.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start,
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Release Date",
                            modifier = modifier.size(26.dp),
                        )
                        Text(movie.release_date,
                            fontSize = 16.sp,
                            fontFamily = poppinsFamily,
                            modifier = modifier.padding(start = 6.dp),
                        )
                    }
                }

                IconButton(onClick = {
                    favoriteVM.deleteFavorite(userId = userId, movieId = movie.id.toString())
                }) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Liked",
                        modifier.size(30.dp),
                        tint = Color.Red,
                    )
                }
            }
        }
    }
    Spacer(modifier.height(23.dp))
}