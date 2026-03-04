package com.example.moviefinder.screen

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WhereToVote
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.moviefinder.firebase.MovieViewModel
import com.example.moviefinder.model.Movie
import kotlinx.coroutines.flow.Flow

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
        val scrollState = rememberScrollState()

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(scrollState)
        ) {
            AsyncImage(
                model = "https://image.tmdb.org/t/p/original/${movie!!.poster_path}",
                contentDescription = movie!!.title,
                contentScale = ContentScale.Crop,
                modifier = modifier
                    .fillMaxWidth()
                    .height(500.dp)
            )

            Spacer(modifier.height(20.dp))
            Text("${movie!!.title}",
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
                Text("Rating: ${movie!!.vote_average}", fontSize = 16.sp)
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
                Text("Vote: ${movie!!.vote_count}", fontSize = 16.sp)
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
                        modifier = modifier.size(31.dp)
                    )
                }
                Text("Relase Date: ${movie!!.release_date}", fontSize = 16.sp)
            }
            movie!!.genres.let {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = modifier.fillMaxWidth()
                ) {
                    movie!!.genres.forEach { genres ->
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
            Text("${movie!!.overview}",
                modifier = modifier.fillMaxWidth(),
                fontSize = 17.sp,
                lineHeight = 25.sp,
            )
            Spacer(modifier.height(20.dp))
        }
    }
}