package com.example.moviefinder.firebase

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviefinder.model.Movie
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.launch
import kotlin.String

class FirestoreMovieDataSource {

    private val collection = Firebase.firestore.collection("movieDetails")

    suspend fun insert(movie: Movie) {
        collection.add(movie).await()
    }

    suspend fun update(movie: Movie) {
        collection.document(movie.imdbId).update(
            mapOf(
                "adult" to movie.adult,
                "backdropPath" to movie.backdropPath,
                "belongsToCollection" to movie.belongsToCollection,
                "budget" to movie.budget,
                "genres" to movie.genres,
                "homepage" to movie.genres,
                "id" to movie.id,
                "imdbId" to movie.imdbId,
                "originCountry" to movie.originCountry,
                "originalLanguage" to movie.originalLanguage,
                "originalTitle" to movie.originalTitle,
                "overview" to movie.overview,
                "popularity" to movie.popularity,
                "posterPath" to movie.posterPath,
                "productionCompanies" to movie.productionCompanies,
                "productionCountries" to movie.productionCountries,
                "releaseDate" to movie.releaseDate,
                "revenue" to movie.revenue,
                "runtime" to movie.runtime,
                "spokenLanguages" to movie.spokenLanguages,
                "status" to movie.status,
                "tagline" to movie.tagline,
                "title" to movie.title,
                "video" to movie.video,
                "voteAverage" to movie.voteAverage,
                "voteCount" to movie.voteCount,
            )
        ).await()
    }

    suspend fun delete(movieId: String) {
        collection.document(movieId).delete().await()
    }

    fun getAll(): Flow<List<Movie>> = callbackFlow {
        val listener = collection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            val movies = snapshot?.documents?.mapNotNull { doc ->
                doc.toObject(Movie::class.java)?.copy(id = doc.id.toInt())
            } ?: emptyList()

            trySend(movies)
        }

        awaitClose {
            listener.remove()
        }
    }
}

class MovieRepository(
    private val dataSource: FirestoreMovieDataSource = FirestoreMovieDataSource()
) {
    val movies = dataSource.getAll()
    suspend fun insert(movie: Movie) = dataSource.insert(movie)
    suspend fun update(movie: Movie) = dataSource.update(movie)
    suspend fun delete(movieId: String) = dataSource.delete(movieId)
}

class MovieViewModel(
    private val repository: MovieRepository = MovieRepository()) : ViewModel() {
    val orders = repository.movies
    fun insertMovie(movie: Movie) {
        viewModelScope.launch {
            repository.insert(movie)}
    }
    fun updateMovie(movie: Movie) {
        viewModelScope.launch { repository.update(movie) }
    }
    fun deleteMovie(movieId: String) {
        viewModelScope.launch { repository.delete(movieId) }
    }
}