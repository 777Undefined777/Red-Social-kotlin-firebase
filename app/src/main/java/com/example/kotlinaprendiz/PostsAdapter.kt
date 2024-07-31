package com.example.kotlinaprendiz

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

import com.example.kotlinaprendiz.models.Post

class PostsAdapter(private val posts: List<Post>) : RecyclerView.Adapter<PostsAdapter.PostViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.bind(post)
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val usernameTextView: TextView = itemView.findViewById(R.id.textViewUsername)
        private val contentTextView: TextView = itemView.findViewById(R.id.textViewContent)
        private val timestampTextView: TextView = itemView.findViewById(R.id.textViewTimestamp)
        private val imageViewPost: ImageView = itemView.findViewById(R.id.imageViewPost)

        fun bind(post: Post) {
            usernameTextView.text = post.username
            contentTextView.text = post.content
            timestampTextView.text = java.text.DateFormat.getDateTimeInstance().format(post.timestamp)
            if (post.imageUrl != null) {
                Glide.with(itemView.context).load(post.imageUrl).into(imageViewPost)
                imageViewPost.visibility = View.VISIBLE
            } else {
                imageViewPost.visibility = View.GONE
            }
        }
    }
}
