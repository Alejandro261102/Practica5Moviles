package com.example.practica5moviles.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface ShowDao {
    // Guarda o actualiza la lista de series
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertShows(shows: List<TvShow>)

    // Actualiza un show individual (para marcar favorito)
    @Update
    suspend fun updateShow(show: TvShow)

    // Búsqueda local (Offline)
    @Query("SELECT * FROM shows_table WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    suspend fun searchShowsLocal(query: String): List<TvShow>

    // Obtener solo favoritos
    @Query("SELECT * FROM shows_table WHERE isFavorite = 1")
    suspend fun getFavorites(): List<TvShow>
}