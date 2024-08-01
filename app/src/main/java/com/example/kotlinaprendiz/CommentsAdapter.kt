package com.example.kotlinaprendiz

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinaprendiz.models.Comment

class CommentsAdapter(private val comments: List<Comment>) : RecyclerView.Adapter<CommentsAdapter.CommentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(comments[position])
    }

    override fun getItemCount(): Int = comments.size

    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val usernameTextView: TextView = itemView.findViewById(R.id.textViewCommentUsername)
        private val contentTextView: TextView = itemView.findViewById(R.id.textViewCommentContent)
        private val timestampTextView: TextView = itemView.findViewById(R.id.textViewCommentTimestamp)

        fun bind(comment: Comment) {
            usernameTextView.text = comment.username
            contentTextView.text = comment.content
            timestampTextView.text = java.text.DateFormat.getDateTimeInstance().format(comment.timestamp)
        }
    }
}
