package com.example.moviefinder.model

import com.google.firebase.firestore.PropertyName
import com.google.gson.annotations.SerializedName

data class Movie(
    val adult: Boolean,
    val backdropPath: String?,
    val belongsToCollection: BelongToCollection?,
    val budget: Int,
    val genres: List<Genres>,
    val homepage: String,
    val id: Int,
    val imdbId: String,
    val originCountry: List<String>,
    val originalLanguage: String,
    val originalTitle: String,
    val overview: String,
    val popularity: Double,
    val posterPath: String?,
    val productionCompanies: List<ProductionCompanies>,
    val productionCountries: List<ProductionCountries>,
    val releaseDate: String,
    val revenue: Long,
    val runtime: Int,
    val spokenLanguages: List<SpokenLanguages>,
    val status: String,
    val tagline: String,
    val title: String,
    val video: Boolean,
    val voteAverage: Double,
    val voteCount: Int,
)

data class BelongToCollection (
    val id: Int,
    val name: String,
    val poster_path: String,
    val backdrop_path: String,
)

data class Genres (
    val id: Int,
    val name: String
)

data class ProductionCompanies (
    val id: Int,
    val logo_path: String?,
    val name: String,
    val original_country: String,
)

data class ProductionCountries (
    val iso_3166_1: String,
    val name: String,
)

data class SpokenLanguages (
    val englist_name: String,
    val iso_639_1: String,
    val name: String,
)