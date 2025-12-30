package com.example.practica5moviles

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.practica5moviles.data.AppDatabase
import com.example.practica5moviles.data.TvShow
import com.example.practica5moviles.databinding.ActivityMainBinding
import com.example.practica5moviles.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ShowAdapter
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar DB
        db = AppDatabase.getDatabase(this)

        setupUI()

        // Cargar nombre de usuario
        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val username = prefs.getString("username", "Usuario")
        binding.tvWelcomeUser.text = "Usuario: $username"
    }

    private fun setupUI() {
        adapter = ShowAdapter { show -> toggleFavorite(show) }
        binding.rvShows.layoutManager = LinearLayoutManager(this)
        binding.rvShows.adapter = adapter

        // Buscar
        binding.btnSearch.setOnClickListener {
            val query = binding.etSearch.text.toString()
            if (query.isNotBlank()) {
                searchShows(query)
            } else {
                Toast.makeText(this, "Escribe algo para buscar", Toast.LENGTH_SHORT).show()
            }
        }

        // Ver Favoritos
        binding.btnShowFavorites.setOnClickListener {
            loadFavorites()
        }

        // Recomendaciones
        binding.btnRecommend.setOnClickListener {
            generateRecommendations()
        }

        // Logout
        binding.btnLogout.setOnClickListener {
            val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            prefs.edit().clear().apply()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    // --- LÓGICA DE SINCRONIZACIÓN Y OFFLINE (Ejercicio 2) ---
    private fun searchShows(query: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // 1. Intentar buscar en Internet (API TVMaze)
                val response = RetrofitClient.api.searchShows(query)

                if (response.isSuccessful && response.body() != null) {
                    val apiShows = response.body()!!.map { it.show }

                    // 2. Sincronización: Guardar en DB Local sin borrar favoritos
                    apiShows.forEach { newShow ->
                        // Verificar si ya existe en local para conservar su estado de favorito
                        val existingShows = db.showDao().searchShowsLocal(newShow.name)
                        val existing = existingShows.find { it.id == newShow.id }

                        if (existing != null) {
                            newShow.isFavorite = existing.isFavorite // Mantenemos el favorito
                        }
                        newShow.lastSearchTimestamp = System.currentTimeMillis()
                    }

                    // Guardar lista actualizada en Room
                    db.showDao().insertShows(apiShows)
                }
            } catch (e: Exception) {
                // Si falla internet, no pasa nada, seguimos al paso 3 (mostrar local)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Sin internet: Mostrando datos locales", Toast.LENGTH_SHORT).show()
                }
            }

            // 3. Fuente de la Verdad: Mostrar SIEMPRE desde la Base de Datos Local
            val localResults = db.showDao().searchShowsLocal(query)

            withContext(Dispatchers.Main) {
                adapter.submitList(localResults)
                if (localResults.isEmpty()) {
                    Toast.makeText(this@MainActivity, "No se encontraron resultados locales ni remotos.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // --- LÓGICA DE FAVORITOS Y RECOMENDACIONES (Ejercicio 3) ---

    private fun toggleFavorite(show: TvShow) {
        lifecycleScope.launch(Dispatchers.IO) {
            // Invertir estado
            show.isFavorite = !show.isFavorite
            db.showDao().updateShow(show)

            withContext(Dispatchers.Main) {
                adapter.notifyDataSetChanged() // Refrescar visualmente
                val msg = if (show.isFavorite) "Agregado a Favoritos" else "Eliminado de Favoritos"
                Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadFavorites() {
        lifecycleScope.launch(Dispatchers.IO) {
            val favorites = db.showDao().getFavorites()
            withContext(Dispatchers.Main) {
                adapter.submitList(favorites)
                if (favorites.isEmpty()) {
                    Toast.makeText(this@MainActivity, "No tienes favoritos aún.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun generateRecommendations() {
        // Lógica simple: Tomar un favorito al azar y buscar cosas similares en la DB local
        lifecycleScope.launch(Dispatchers.IO) {
            val favorites = db.showDao().getFavorites()

            if (favorites.isEmpty()) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Agrega favoritos primero para recibir recomendaciones.", Toast.LENGTH_LONG).show()
                }
                return@launch
            }

            // Algoritmo simple de recomendación:
            // Toma el primer favorito, extrae la primera palabra y busca coincidencias en la DB
            val randomFav = favorites.random()
            val keyword = randomFav.name.split(" ").firstOrNull() ?: ""

            // Buscar en DB local cosas que coincidan con la palabra clave pero que NO sean el mismo show
            val recommendations = db.showDao().searchShowsLocal(keyword)
                .filter { it.id != randomFav.id && !it.isFavorite }

            withContext(Dispatchers.Main) {
                if (recommendations.isNotEmpty()) {
                    adapter.submitList(recommendations)
                    Toast.makeText(this@MainActivity, "Porque te gustó '${randomFav.name}'...", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this@MainActivity, "No tenemos recomendaciones nuevas basadas en '${randomFav.name}' intenta buscar más series.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}