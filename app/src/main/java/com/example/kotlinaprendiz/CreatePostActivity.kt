package com.example.kotlinaprendiz

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlinaprendiz.models.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class CreatePostActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)

        auth = FirebaseAuth.getInstance()

        val etContent: EditText = findViewById(R.id.editTextContent)
        val btnPost: Button = findViewById(R.id.buttonPost)

        btnPost.setOnClickListener {
            val content = etContent.text.toString().trim()

            if (content.isNotEmpty()) {
                createPost(content)
            } else {
                Toast.makeText(this, "Por favor, ingresa algún contenido.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createPost(content: String) {
        val database = FirebaseDatabase.getInstance()
        val postsRef = database.getReference("posts")
        val postId = postsRef.push().key ?: return

        val currentUser = auth.currentUser ?: return
        val post = Post(
            postId = postId,
            uid = currentUser.uid,
            username = currentUser.displayName ?: "Anónimo",
            content = content,
            imageUrl = null,
            timestamp = System.currentTimeMillis()
        )

        postsRef.child(postId).setValue(post).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Publicación creada.", Toast.LENGTH_SHORT).show()
                finish() // Regresa a la actividad anterior
            } else {
                Toast.makeText(this, "Error al crear la publicación: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
