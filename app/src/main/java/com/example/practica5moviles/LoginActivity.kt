package com.example.practica5moviles

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.practica5moviles.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Verificar sesión persistente (Ejercicio 1)
        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        if (prefs.getBoolean("is_logged_in", false)) {
            goToMain()
        }

        binding.btnLogin.setOnClickListener {
            val user = binding.etUsername.text.toString()
            val pass = binding.etPassword.text.toString()

            // Validación simple (Aquí iría tu llamada a tu API propia del Ejercicio 1)
            if (user.isNotEmpty() && pass.isNotEmpty()) {

                // Guardar sesión
                val editor = prefs.edit()
                editor.putBoolean("is_logged_in", true)
                editor.putString("username", user)
                editor.apply()

                Toast.makeText(this, "Bienvenido $user", Toast.LENGTH_SHORT).show()
                goToMain()
            } else {
                Toast.makeText(this, "Por favor ingresa usuario y contraseña", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun goToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish() // Cierra el login para que no se pueda volver atrás
    }
}