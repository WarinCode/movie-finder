package com.example.moviefinder.model

data class Favorite (
    val userId: String = "",
    val movieId: String = "",
    @field:JvmField
    val isLiked: Boolean = false,
)