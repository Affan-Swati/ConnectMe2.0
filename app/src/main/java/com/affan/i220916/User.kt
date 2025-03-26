package com.affan.i220916

// Data model for user
data class User(
    val name: String = "",
    val username: String = "",
    val phone: String = "",
    val email: String = "",
    val userId: String = "",
    val profileImageBase64: String = "https://example.com/profile_icon.jpg", // Default profile image URL
    val bio: String = "",
    val followers: List<String> = emptyList(),
    val following: List<String> = emptyList()
)