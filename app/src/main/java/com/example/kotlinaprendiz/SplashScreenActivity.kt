package com.example.kotlinaprendiz



import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.welcome_splash)

        // Configura un retraso para pasar a la siguiente actividad
        val splashTimeOut: Long = 3000 // Tiempo en milisegundos (3 segundos)

        Handler().postDelayed({
            // Después del retraso, inicia la actividad principal
            startActivity(Intent(this, LoginActivity::class.java))
            // Finaliza la actividad splash
            finish()
        }, splashTimeOut)
    }
}
