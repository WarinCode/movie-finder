package com.example.moviefinder.screen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.graphics.fromColorLong
import androidx.compose.ui.layout.ContentScale
import androidx.navigation.NavController

@Composable
fun HomeScreen(
    navController: NavController,
    modifier: Modifier = Modifier
){
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
            key = { movie -> movie.imdb_id }
        ) { movie ->
            MovieCard(
                movie = movie,
                onClick = {
                    navController.navigate("movie-detail/${movie.id}")
                }
            )
        }
    }
}

@Composable
fun MovieCard(
    movie: Movie,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
){
    Column (
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFE8E8E8))
            .clickable { onClick() }
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
                modifier = modifier.padding(start = 18.dp)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = modifier.fillMaxWidth(),
            ) {
                IconButton(
                    onClick = {},
                    colors = IconButtonDefaults
                        .iconButtonColors(contentColor = Color(0xFFFFC107))
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                    )
                }
                Text("${movie.vote_average}")
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = modifier.fillMaxWidth(),
            ) {
                IconButton(
                    onClick = {},
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = "Date",
                    )
                }
                Text("${movie.release_date}")
            }
        }
    }
    Spacer(modifier.height(40.dp))
}