package com.androidcodr.movieapp_project.data.api

import com.androidcodr.movieapp_project.data.value_object.MovieDetails
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path



interface MovieDBInterface {
    //@GET("movie_popular")
    //fun getPopularMovie(@Query)

    @GET("movie/{movie_id}")
    fun getMovieDetails(@Path("movie_id") id:Int): Single<MovieDetails>

}