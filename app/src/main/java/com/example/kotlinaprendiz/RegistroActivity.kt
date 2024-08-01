package com.example.kotlinaprendiz

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlinaprendiz.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class RegistroActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registrar_activity)

        auth = FirebaseAuth.getInstance()

        val etUsername: EditText = findViewById(R.id.editTextUsername)
        val etPassword: EditText = findViewById(R.id.editTextPassword)
        val etEmail: EditText = findViewById(R.id.editTextEmail)
        val etDateOfBirth: EditText = findViewById(R.id.editTextBirthDate)
        val btnRegister: Button = findViewById(R.id.buttonRegister)
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

        // Abrir DatePickerDialog al hacer clic en el EditText de fecha de nacimiento
        etDateOfBirth.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    // +1 porque enero es 0
                    val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                    etDateOfBirth.setText(selectedDate)
                }, year, month, day
            )
            datePickerDialog.show()
        }

        btnRegister.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val username = etUsername.text.toString().trim()
            val dateOfBirth = etDateOfBirth.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty() && username.isNotEmpty() && dateOfBirth.isNotEmpty()) {
                registerUser(email, password, username, dateOfBirth)
            } else {
                Toast.makeText(this, "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun registerUser(email: String, password: String, username: String, dateOfBirth: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.let {
                        saveUserDataToDatabase(it.uid, username, email, dateOfBirth, password)
                    }
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Autenticación fallida: ${task.exception?.message}",
                        Toast.LENGTH_SHORT).show()
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

    private fun saveUserDataToDatabase(uid: String, username: String, email: String, dateOfBirth: String, password: String) {
        val database = FirebaseDatabase.getInstance()
        val userRef = database.getReference("usuarios").child(uid)

        val user = User(uid, username, email, dateOfBirth, password)
        userRef.setValue(user).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "Se guardaron los datos de el usuario exitosamente.")
                Toast.makeText(this, "Registro exitoso.", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoginActivity::class.java) // Redirige a LoginActivity después del registro exitoso
                startActivity(intent)
                finish()
            } else {
                Log.w(TAG, "error al guardar los datos en la bd.", task.exception)
                Toast.makeText(this, "Error al guardar los datos: ${task.exception?.message}",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val TAG = "RegistroActivity"
    }
}
