package com.example.practica5moviles.network

import com.example.practica5moviles.data.TvMazeSearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface TvMazeApi {
    @GET("search/shows")
    suspend fun searchShows(@Query("q") query: String): Response<List<TvMazeSearchResponse>>
}