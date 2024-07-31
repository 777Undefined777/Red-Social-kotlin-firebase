package com.example.kotlinaprendiz.models

data class Comment(
    val commentId: String,
    val postId: String,
    val uid: String,
    val username: String,
    val content: String,
    val timestamp: Long
)
