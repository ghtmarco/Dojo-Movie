package com.example.dojomovie

data class Transaction(
    val id: Int,
    val userId: Int,
    val filmId: String,     // 🔧 FIXED: Changed from Int to String to match Film.id
    val quantity: Int,
    val filmTitle: String = "",
    val filmPrice: Int = 0
)