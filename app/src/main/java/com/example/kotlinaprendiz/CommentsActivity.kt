package com.example.kotlinaprendiz

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinaprendiz.models.Comment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class CommentsActivity : AppCompatActivity() {

    private lateinit var commentsRef: DatabaseReference
    private lateinit var postId: String
    private lateinit var commentsList: MutableList<Comment>
    private lateinit var commentsAdapter: CommentsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)

        postId = intent.getStringExtra("POST_ID") ?: return
        commentsRef = FirebaseDatabase.getInstance().getReference("comments").child(postId)

        commentsList = mutableListOf()
        commentsAdapter = CommentsAdapter(commentsList)

        val recyclerViewComments: RecyclerView = findViewById(R.id.recyclerViewComments)
        recyclerViewComments.layoutManager = LinearLayoutManager(this)
        recyclerViewComments.adapter = commentsAdapter

        val newCommentButton: Button = findViewById(R.id.buttonAddComment)
        val commentEditText: EditText = findViewById(R.id.editTextComment)

        newCommentButton.setOnClickListener {
            val content = commentEditText.text.toString()
            if (content.isNotEmpty()) {
                addComment(content)
            }
        }

        fetchComments()
    }

    private fun fetchComments() {
        commentsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                commentsList.clear()
                for (commentSnapshot in snapshot.children) {
                    val comment = commentSnapshot.getValue(Comment::class.java)
                    if (comment != null) {
                        fetchUsername(comment.uid) { username ->
                            comment.username = username
                            Log.d("CommentsActivity", "Comentario recibido: Usuario: ${comment.username}, Contenido: ${comment.content}")
                            commentsList.add(comment)
                            commentsAdapter.notifyDataSetChanged()
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@CommentsActivity, "Error al cargar comentarios", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun fetchUsername(uid: String, callback: (String) -> Unit) {
        val usersRef = FirebaseDatabase.getInstance().getReference("usuarios")
        usersRef.child(uid).child("username").get().addOnSuccessListener { snapshot ->
            val username = snapshot.value as? String ?: "Desconocido"
            callback(username)
        }.addOnFailureListener {
            callback("Error")
        }
    }





    private fun addComment(content: String) {
        val commentId = commentsRef.push().key ?: return
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return
        val uid = currentUser.uid
        val timestamp = System.currentTimeMillis()

        val comment = Comment(commentId, postId, uid, content, timestamp)
        commentsRef.child(commentId).setValue(comment).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Comentario agregado", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Error al agregar comentario: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
