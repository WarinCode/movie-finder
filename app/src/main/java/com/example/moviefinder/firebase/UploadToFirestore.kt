package com.example.moviefinder.firebase

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.firebase.firestore.FirebaseFirestore
import com.example.moviefinder.model.Movie

fun uploadJsonToFirestore(context: Context) {
    val db = FirebaseFirestore.getInstance()

    try {
        val jsonString = context.assets.open("movie_details.json")
            .bufferedReader()
            .use { it.readText() }

        val listType = object : TypeToken<List<Movie>>() {}.type
        val movies: List<Movie> = Gson().fromJson(jsonString, listType)

        if (movies.isEmpty()) {
            return
        }

        val chunks = movies.chunked(500)
        chunks.forEachIndexed { index, movieChunk ->
            val batch = db.batch()

            movieChunk.forEach { movie ->
                val docRef = db.collection("movies").document()
                batch.set(docRef, movie)
            }

            batch.commit()
        }

    } catch (e: Exception) {
        e.printStackTrace()
    }
}