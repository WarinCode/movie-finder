package com.example.moviefinder.firebase

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviefinder.model.Favorite
import com.example.moviefinder.model.History
import com.example.moviefinder.model.Movie
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.launch

class FirestoreMovieDataSource {
    private val collection = Firebase.firestore.collection("movies")

    fun getAll(): Flow<List<Movie>> = callbackFlow {
        val listener = collection.limit(4000).addSnapshotListener { snapshot, error ->
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
        val listener = collection
            .document(id)
            .addSnapshotListener { snapshot, error ->

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
}

class MovieViewModel(
    private val repository: MovieRepository = MovieRepository()) : ViewModel() {
    val movies = repository.movies

    fun getById(id: String): Flow<Movie?> {
        return repository.getById(id)
    }
}

class FirestoreFavoriteDataSource {

    private val collection = Firebase.firestore.collection("favorites")

    suspend fun insert(favorite: Favorite) {
        val docId = "${favorite.userId}_${favorite.movieId}"
        collection.document(docId).set(favorite).await()
    }

    suspend fun delete(userId: String, movieId: String) {
        val docId = "${userId}_${movieId}"
        collection.document(docId).delete().await()
    }

    fun getAllById(userId: String): Flow<List<Favorite>> = callbackFlow {
        val listener = collection
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->

            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            val favorites = snapshot?.documents?.mapNotNull { doc ->
                doc.toObject(Favorite::class.java)
            } ?: emptyList()

            trySend(favorites)
        }

        awaitClose {
            listener.remove()
        }
    }
}

class FavoriteRepository(
    private val dataSource: FirestoreFavoriteDataSource = FirestoreFavoriteDataSource()
) {
    fun getAllById(userId: String) = dataSource.getAllById(userId)
    suspend fun insert(favorite: Favorite) = dataSource.insert(favorite)
    suspend fun delete(userId: String, movieId: String) = dataSource.delete(userId, movieId)
}

class FavoriteViewModel(
    private val repository: FavoriteRepository = FavoriteRepository()
) : ViewModel() {
    fun getAllById(userId: String): Flow<List<Favorite>> {
        return repository.getAllById(userId)
    }

    fun insertFavorite(favorite: Favorite) {
        viewModelScope.launch {
            repository.insert(favorite)
        }
    }

    fun deleteFavorite(userId: String, movieId: String) {
        viewModelScope.launch {
            repository.delete(userId, movieId)
        }
    }
}

class FirestoreHistoryDataSource {

    private val collection = Firebase.firestore.collection("histories")

    fun getAllById(userId: String): Flow<List<History>> = callbackFlow {
        val listener = collection
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val histories = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(History::class.java)
                } ?: emptyList()

                trySend(histories)
            }

        awaitClose {
            listener.remove()
        }
    }

    suspend fun insert(history: History) {
        val docId = "${history.userId}_${history.movieId}_${history.viewedAt.toString()}"
        collection.document(docId).set(history).await()
    }

    suspend fun delete(history: History) {
        val docId = "${history.userId}_${history.movieId}_${history.viewedAt.toString()}"
        collection.document(docId).delete().await()
    }

    suspend fun deleteAll(userId: String) {
        val snapshot = collection
            .whereEqualTo("userId", userId)
            .get()
            .await()

        for (doc in snapshot.documents) {
            doc.reference.delete().await()
        }
    }
}

class HistoryRepository(
    private val dataSource: FirestoreHistoryDataSource = FirestoreHistoryDataSource()
) {
    fun getAllById(userId: String) = dataSource.getAllById(userId)
    suspend fun insert(history: History) = dataSource.insert(history)
    suspend fun delete(history: History) = dataSource.delete(history)
    suspend fun deleteAll(userId: String) = dataSource.deleteAll(userId)
}

class HistoryViewModel(
    private val repository: HistoryRepository = HistoryRepository()
) : ViewModel() {
    fun getAllById(userId: String): Flow<List<History>> {
        return repository.getAllById(userId)
    }

    fun insertHistory(history: History) {
        viewModelScope.launch {
            repository.insert(history)
        }
    }

    fun deleteHistory(history: History) {
        viewModelScope.launch {
            repository.delete(history)
        }
    }

    fun deleteAllHistories(userId: String) {
        viewModelScope.launch {
            repository.deleteAll(userId)
        }
    }
}