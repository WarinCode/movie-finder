package com.example.moviefinder.screen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import coil3.compose.AsyncImage
import com.example.moviefinder.R
import com.example.moviefinder.firebase.MovieViewModel
import com.example.moviefinder.model.Movie

@Composable
fun HomeScreen(modifier: Modifier = Modifier){
    val movieVM = viewModel<MovieViewModel>()
    val movies by movieVM.movies.collectAsState(initial = emptyList())

    LazyColumn (
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(movies)  { movie ->
            MovieItem(movie)
        }
    }
}

@Composable
fun MovieItem(movie: Movie, modifier: Modifier = Modifier){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxWidth()
            .background(Color.Gray)
            .padding(vertical = 20.dp),
    ) {
        if (movie.poster_path != null) {
            AsyncImage(
                model = "https://image.tmdb.org/t/p/original/${movie.poster_path}",
                contentDescription = movie.title,
                modifier = modifier.width(100.dp).height(250.dp)
            )
        } else {
            Image(
                painter = painterResource(R.drawable.ic_launcher_background),
                contentDescription = movie.title,
                modifier = modifier.width(200.dp).height(250.dp)
            )
        }

        Column() {
            Text(movie.title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text("${movie.vote_average}")
            Text("${movie.release_date}")
        }
    }
}