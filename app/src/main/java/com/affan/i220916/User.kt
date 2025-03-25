package com.affan.i220916

// Data model for user
data class User(
    val name: String = "",
    val username: String = "",
    val phone: String = "",
    val email: String = "",
    val userId: String = "",
    val profileImageUrl: String = "" // Default profile image URL
)