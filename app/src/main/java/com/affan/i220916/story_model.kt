package com.affan.i220916

data class story_model(
    val type: Int,  // USER_STORY or OTHER_STORY
    val userId: String,
    val profileImageBase64: String
)