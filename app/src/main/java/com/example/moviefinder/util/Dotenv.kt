package com.example.moviefinder.util

import io.github.cdimascio.dotenv.dotenv
val dotenv = dotenv {
    directory = "/assets"
    filename = "env"
}