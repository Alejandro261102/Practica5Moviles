package com.example.practica5moviles.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "shows_table")
data class TvShow(
    @PrimaryKey
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("language") val language: String?,
    @SerializedName("summary") val summary: String?,

    // Campos locales (no vienen de la API, los gestionamos nosotros)
    var isFavorite: Boolean = false,
    var lastSearchTimestamp: Long = 0
)

// Clase auxiliar para leer la respuesta anidada de la API de TVMaze
data class TvMazeSearchResponse(
    @SerializedName("score") val score: Double,
    @SerializedName("show") val show: TvShow
)