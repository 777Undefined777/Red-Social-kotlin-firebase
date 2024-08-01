package com.example.kotlinaprendiz

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kotlinaprendiz.models.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class PostsAdapter(private val posts: List<Post>) : RecyclerView.Adapter<PostsAdapter.PostViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.bind(post)
    }

    override fun getItemCount(): Int = posts.size

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val usernameTextView: TextView = itemView.findViewById(R.id.textViewUsername)
        private val contentTextView: TextView = itemView.findViewById(R.id.textViewContent)
        private val timestampTextView: TextView = itemView.findViewById(R.id.textViewTimestamp)
        private val imageViewPost: ImageView = itemView.findViewById(R.id.imageViewPost)
        private val likeImageView: ImageView = itemView.findViewById(R.id.imageViewLike)
        private val commentImageView: ImageView = itemView.findViewById(R.id.imageViewComment)
        private val likeCountTextView: TextView =
            itemView.findViewById(R.id.textViewLikeCount) // Nueva vista para el contador de "me gusta"

        fun bind(post: Post) {
            usernameTextView.text = post.username
            contentTextView.text = post.content
            timestampTextView.text = java.text.DateFormat.getDateTimeInstance().format(post.timestamp)
            likeCountTextView.text = post.likes.toString()

            if (post.imageUrl != null) {
                imageViewPost.visibility = View.VISIBLE
                Glide.with(itemView.context).load(post.imageUrl).into(imageViewPost)
            } else {
                imageViewPost.visibility = View.GONE
            }

            likeImageView.setOnClickListener {
                handleLikeClick(post)
            }

            commentImageView.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, CommentsActivity::class.java)
                intent.putExtra("POST_ID", post.postId)
                context.startActivity(intent)
            }
        }


        private fun handleLikeClick(post: Post) {
            val currentUser = FirebaseAuth.getInstance().currentUser ?: return
            val uid = currentUser.uid

            val postRef = FirebaseDatabase.getInstance().getReference("posts").child(post.postId)

            if (post.likedBy.contains(uid)) {
                post.likedBy.remove(uid)
                post.likes -= 1
            } else {
                post.likedBy.add(uid)
                post.likes += 1
            }

            postRef.setValue(post).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    likeCountTextView.text = post.likes.toString()
                } else {
                    Toast.makeText(
                        itemView.context,
                        "Error al actualizar 'me gusta'",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}