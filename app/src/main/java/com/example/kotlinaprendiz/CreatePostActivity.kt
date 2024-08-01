package com.example.kotlinaprendiz

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlinaprendiz.models.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

import java.util.*

class CreatePostActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private var selectedImageUri: Uri? = null
    private lateinit var imageView: ImageView
    private val PICK_IMAGE_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)

        auth = FirebaseAuth.getInstance()

        // Initialize imageView
        imageView = findViewById(R.id.imageView)

        val etContent: EditText = findViewById(R.id.editTextContent)
        val btnPost: Button = findViewById(R.id.buttonPost)
        val btnSelectImage: ImageView = findViewById(R.id.imageView)

        val videoView: VideoView = findViewById(R.id.videoView)
        val videoUri = Uri.parse("android.resource://" + packageName + "/" + R.raw.hcvideo)
        videoView.setVideoURI(videoUri)
        videoView.start()
        videoView.setOnPreparedListener { mediaPlayer ->
            mediaPlayer.isLooping = true
            mediaPlayer.setVolume(0f, 0f)
            mediaPlayer.setOnVideoSizeChangedListener { _, width, height ->
                adjustAspectRatio(videoView, width, height)
            }
        }

        btnSelectImage.setOnClickListener {
            openImagePicker()
        }

        btnPost.setOnClickListener {
            val content = etContent.text.toString().trim()

            if (content.isNotEmpty()) {
                if (selectedImageUri != null) {
                    uploadImageAndCreatePost(content)
                } else {
                    createPostWithoutImage(content)
                }
            } else {
                Toast.makeText(this, "Por favor, ingresa contenido.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun adjustAspectRatio(videoView: VideoView, videoWidth: Int, videoHeight: Int) {
        val layoutParams = videoView.layoutParams
        val displayMetrics = resources.displayMetrics

        val deviceWidth = displayMetrics.widthPixels
        val deviceHeight = displayMetrics.heightPixels

        val deviceAspectRatio = deviceWidth.toFloat() / deviceHeight.toFloat()
        val videoAspectRatio = videoWidth.toFloat() / videoHeight.toFloat()

        if (videoAspectRatio > deviceAspectRatio) {
            layoutParams.width = deviceWidth
            layoutParams.height = (deviceWidth / videoAspectRatio).toInt()
        } else {
            layoutParams.width = (deviceHeight * videoAspectRatio).toInt()
            layoutParams.height = deviceHeight
        }

        videoView.layoutParams = layoutParams
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            selectedImageUri = data.data
            imageView.setImageURI(selectedImageUri)
        }
    }

    private fun uploadImageAndCreatePost(content: String) {
        val storageRef = FirebaseStorage.getInstance().reference.child("images/${UUID.randomUUID()}.jpg")
        selectedImageUri?.let { uri ->
            storageRef.putFile(uri).addOnSuccessListener { taskSnapshot ->
                storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    createPost(content, downloadUri.toString())
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(this, "Error al subir la imagen: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createPostWithoutImage(content: String) {
        createPost(content, null)
    }

    private fun createPost(content: String, imageUrl: String?) {
        val database = FirebaseDatabase.getInstance()
        val postsRef = database.getReference("posts")
        val usersRef = database.getReference("usuarios")
        val postId = postsRef.push().key ?: return

        val currentUser = auth.currentUser ?: return
        val uid = currentUser.uid

        usersRef.child(uid).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val username = snapshot.child("username").value as? String

                if (username != null) {
                    val post = Post(
                        postId = postId,
                        uid = uid,
                        username = username,
                        content = content,
                        imageUrl = imageUrl,
                        timestamp = System.currentTimeMillis()
                    )

                    postsRef.child(postId).setValue(post).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Publicación creada.", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(this, "Error al crear la publicación: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "No se encontró el nombre de usuario.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "No se encontró el nombre de usuario.", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "Error al obtener el nombre de usuario: ${exception.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
