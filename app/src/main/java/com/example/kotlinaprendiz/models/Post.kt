package com.example.kotlinaprendiz.models

data class Post(
    val postId: String = "",
    val uid: String = "",
    val username: String = "",
    val content: String = "",
    val imageUrl: String? = null,
    val timestamp: Long = 0L,
    var likes: Int = 0,
    var commentsCount: Int = 0, // Contador de comentarios
    val likedBy: MutableList<String> = mutableListOf()
)
