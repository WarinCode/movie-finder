package com.example.moviefinder.model

data class Movie (
    val adult: Boolean = false,
    val backdrop_path: String? = null,
    val belongs_to_collection: BelongsToCollection? = null,
    val budget: Long = 0,
    val genres: List<Genre> = emptyList(),
    val homepage: String? = null,
    val id: Any? = null,
    val imdb_id: String = "",
    val origin_country: List<String> = emptyList(),
    val original_language: String = "",
    val original_title: String = "",
    val overview: String? = null,
    val popularity: Double = 0.0,
    val poster_path: String? = null,
    val production_companies: List<ProductionCompany> = emptyList(),
    val production_countries: List<ProductionCountry> = emptyList(),
    val release_date: String = "",
    val revenue: Long = 0,
    val runtime: Long = 0,
    val spoken_languages: List<SpokenLanguage> = emptyList(),
    val status: String? = null,
    val tagline: String = "",
    val title: String = "",
    val video: Boolean = false,
    val vote_average: Double = 0.0,
    val vote_count: Long = 0
)

data class BelongsToCollection (
    val id: Long = 0,
    val name: String = "",
    val poster_path: String? = null,
    val backdrop_path: String? = null
)

data class Genre (
    val id: Long = 0,
    val name: String? = null,
)

data class ProductionCompany (
    val id: Long = 0,
    val logo_path: String? = null,
    val name: String = "",
    val origin_country: String? = null,
)

data class ProductionCountry (
    val iso3166_1: String? = null,
    val name: String? = null,
)

data class SpokenLanguage (
    val english_name: String? = null,
    val iso639_1: String = "",
    val name: String = "",
)