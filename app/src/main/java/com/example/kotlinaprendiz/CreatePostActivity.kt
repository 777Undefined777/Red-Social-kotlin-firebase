package com.example.kotlinaprendiz

import android.os.Bundle
import android.util.Log
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
        val usersRef = database.getReference("usuarios")
        val postId = postsRef.push().key ?: return

        val currentUser = auth.currentUser ?: return
        val uid = currentUser.uid

        // Obtener el nombre de usuario desde la base de datos
        usersRef.child(uid).get().addOnSuccessListener { snapshot ->
            Log.d("CreatePostActivity", "Snapshot: ${snapshot.value}")
            if (snapshot.exists()) {
                val username = snapshot.child("username").value as? String
                Log.d("CreatePostActivity", "Nombre de usuario obtenido: $username")

                if (username != null) {
                    val post = Post(
                        postId = postId,
                        uid = uid,
                        username = username,
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
                } else {
                    Log.d("CreatePostActivity", "El campo 'username' no se encontró para el UID: $uid")
                    Toast.makeText(this, "No se encontró el nombre de usuario.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.d("CreatePostActivity", "No se encontraron datos para el UID: $uid")
                Toast.makeText(this, "No se encontró el nombre de usuario.", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { exception ->
            Log.e("CreatePostActivity", "Error al obtener el nombre de usuario", exception)
            Toast.makeText(this, "Error al obtener el nombre de usuario: ${exception.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
