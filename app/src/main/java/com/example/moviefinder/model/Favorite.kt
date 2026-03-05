package com.example.moviefinder.model

data class Favorite (
    val userId: String = "",
    val movieId: String = "",
    val liked: Boolean = false,
)