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

    private val collection = Firebase.firestore.collection("movies")

    suspend fun insert(movie: Movie) {
        collection.add(movie).await()
    }

    suspend fun delete(movieId: String) {
        collection.document(movieId).delete().await()
    }

    fun getAll(): Flow<List<Movie>> = callbackFlow {
        val listener = collection.limit(300).addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            val movies = snapshot?.documents?.mapNotNull { doc ->
                doc.toObject(Movie::class.java)?.copy(id = doc.id)
            } ?: emptyList()

            trySend(movies)
        }

        awaitClose {
            listener.remove()
        }
    }

    fun getById(id: String): Flow<Movie?> = callbackFlow {
        val listener = collection.document(id).addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val movie = snapshot.toObject(Movie::class.java)?.copy(id = snapshot.id)
                trySend(movie)
            } else {
                trySend(null)
            }
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

    fun getById(id: String) = dataSource.getById(id)

    suspend fun insert(movie: Movie) = dataSource.insert(movie)
    suspend fun delete(movieId: String) = dataSource.delete(movieId)
}

class MovieViewModel(
    private val repository: MovieRepository = MovieRepository()) : ViewModel() {
    val movies = repository.movies

    fun getById(id: String): Flow<Movie?> {
        return repository.getById(id)
    }

    fun insertMovie(movie: Movie) {
        viewModelScope.launch {
            repository.insert(movie)}
    }

    fun deleteMovie(movieId: String) {
        viewModelScope.launch { repository.delete(movieId) }
    }
}