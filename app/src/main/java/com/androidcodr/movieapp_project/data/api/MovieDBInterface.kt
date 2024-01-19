package com.androidcodr.movieapp_project.data.api

import com.androidcodr.movieapp_project.data.value_object.MovieDetails
import com.androidcodr.movieapp_project.data.value_object.MovieResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface MovieDBInterface {
    @GET("movie/popular")
    fun getPopularMovie( @Query("page") page: Int): Single<MovieResponse>

    @GET("movie/{movie_id}")
    fun getMovieDetails(@Path("movie_id") id:Int): Single<MovieDetails>

}