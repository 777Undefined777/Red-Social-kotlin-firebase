package com.example.kotlinaprendiz

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinaprendiz.models.Post
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var postsRef: DatabaseReference
    private lateinit var postsList: MutableList<Post>
    private lateinit var postsAdapter: PostsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        postsRef = database.getReference("posts")
        postsList = mutableListOf()
        postsAdapter = PostsAdapter(postsList)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewPosts)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = postsAdapter

        fetchPosts()

        val EditUser: ImageView = findViewById(R.id.ProfileImg)
        EditUser.setOnClickListener {
            startActivity(Intent(this, UserEditActivity::class.java))
        }




        // Botón para crear una nueva publicación
        val fabCreatePost: FloatingActionButton = findViewById(R.id.fabCreatePost)
        fabCreatePost.setOnClickListener {
            startActivity(Intent(this, CreatePostActivity::class.java))
        }
    }

    private fun fetchPosts() {
        postsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                postsList.clear()
                for (postSnapshot in snapshot.children) {
                    val post = postSnapshot.getValue(Post::class.java)
                    post?.let { postsList.add(it) }
                }
                postsAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar error
            }
        })
    }
}
