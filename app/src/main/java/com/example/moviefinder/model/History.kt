package com.example.moviefinder.model

import com.google.firebase.Timestamp

data class History (
    val userId: String = "",
    val movieId: String = "",
    val viewedAt: Timestamp = Timestamp.now(),
)