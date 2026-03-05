import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.moviefinder.firebase.MovieViewModel
import com.example.moviefinder.screen.MovieCard

@Composable
fun SearchScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val movieVM = viewModel<MovieViewModel>()
    val movies by movieVM.movies.collectAsState(initial = emptyList())
    var searchQuery by remember { mutableStateOf("") }

    val filteredMovies = remember(searchQuery, movies) {
        if (searchQuery.isBlank()) {
            emptyList()
        } else {
            movies.filter {
                it.title.contains(searchQuery, ignoreCase = true) &&
                        it.overview!!.contains(searchQuery, ignoreCase = true)
            }
        }
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search movies") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = { if (searchQuery.isNotEmpty())
                Icon(Icons.Default.Close, contentDescription = null, modifier = modifier.clickable { searchQuery = "" }) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Gray,
                cursorColor = Color.Black,
                focusedLabelColor = Color.Black
            ),
        )

        if (searchQuery.isNotBlank() && filteredMovies.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No movies found for \"$searchQuery\"", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp)
            ) {
                items(filteredMovies) { movie ->
                    MovieCard(
                        movie = movie,
                        onNavigateToMovieDetail = { navController.navigate("movie-detail/${movie.id}") },
                    )
                }
            }
        }
    }
}