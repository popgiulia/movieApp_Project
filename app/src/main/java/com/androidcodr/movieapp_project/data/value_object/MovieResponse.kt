package com.androidcodr.movieapp_project.data.value_object

import com.androidcodr.movieapp_project.data.value_object.Movie2
import com.google.gson.annotations.SerializedName

data class MovieResponse(
    val page: Int,
    @SerializedName("results")
    val movieList: List<Movie2>,
    @SerializedName("total_pages")
    val totalPages: Int,
    @SerializedName("total_results")
    val totalResults: Int
)