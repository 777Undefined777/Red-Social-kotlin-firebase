package com.example.kotlinaprendiz.models

data class Comment(
    val commentId: String = "",
    val postId: String = "",
    val uid: String = "",
    val content: String = "",
    val timestamp: Long = 0L,
    var username: String = "" // Este campo debería estar presente y ser mutable
)
