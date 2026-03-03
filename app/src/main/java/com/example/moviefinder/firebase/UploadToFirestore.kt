package com.example.moviefinder.firebase

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.firebase.firestore.FirebaseFirestore
import com.example.moviefinder.model.Movie

fun uploadJsonToFirestore(context: Context) {
    try {
        val db = FirebaseFirestore.getInstance()
        val gson = Gson()
        val jsonString = context.assets.open("movie_details.json")
            .bufferedReader()
            .use { it.readText() }

        val listType = object : TypeToken<List<Movie>>() {}.type
        val movies: List<Movie> = gson.fromJson(jsonString, listType)

        Log.d("MOVIE_APP", "จำนวนหนังทั้งหมด: ${movies.size}")

        movies.forEach { movie ->
            db.collection("movies")
                .document(movie.id.toString())
                .set(movie)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
