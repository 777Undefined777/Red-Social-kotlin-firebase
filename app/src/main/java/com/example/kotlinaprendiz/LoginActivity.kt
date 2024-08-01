package com.example.kotlinaprendiz

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        auth = FirebaseAuth.getInstance()

        val etEmailLogin: EditText = findViewById(R.id.editTextEmailLogin)
        val etPasswordLogin: EditText = findViewById(R.id.editTextPasswordLogin)
        val btnLogin: Button = findViewById(R.id.buttonLogin)
        val btnSignup: TextView = findViewById(R.id.btnSignup)

        val videoView: VideoView = findViewById(R.id.videoView1)
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


        btnSignup.setOnClickListener {
            val intent = Intent(this, RegistroActivity::class.java)
            startActivity(intent)
        }

        btnLogin.setOnClickListener {
            val email = etEmailLogin.text.toString().trim()
            val password = etPasswordLogin.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                Toast.makeText(this, "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show()
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

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Login exitoso.", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Login fallido: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
