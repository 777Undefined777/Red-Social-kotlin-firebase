package com.example.kotlinaprendiz

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinaprendiz.models.Comment
import java.text.SimpleDateFormat
import java.util.Locale

class CommentsAdapter(private val commentsList: List<Comment>) : RecyclerView.Adapter<CommentsAdapter.CommentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = commentsList[position]
        holder.usernameTextView.text = comment.username
        holder.contentTextView.text = comment.content

        // Convert timestamp to date string
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        val formattedDate = dateFormat.format(comment.timestamp)
        holder.timestampTextView.text = formattedDate

        // Set user image (Placeholder for now, or load from database)
        holder.userImageView.setImageResource(R.drawable.ic_user)
    }

    override fun getItemCount(): Int = commentsList.size

    inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val usernameTextView: TextView = itemView.findViewById(R.id.textViewCommentUsername)
        val contentTextView: TextView = itemView.findViewById(R.id.textViewCommentContent)
        val timestampTextView: TextView = itemView.findViewById(R.id.textViewCommentTimestamp)
        val userImageView: ImageView = itemView.findViewById(R.id.imageViewUser)
    }
}
