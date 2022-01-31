package com.example.nycrestaurants.models

import java.io.Serializable

data class NYCRestaurantModel(
    val id: Int,
    val title: String,
    val description: String,
    val date: String,
    val location: String,
    val latitude: Double,
    val longitude: Double
    ): Serializable