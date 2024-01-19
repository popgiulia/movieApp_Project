package com.androidcodr.movieapp_project.ui.movie_details

import androidx.lifecycle.LiveData
import com.androidcodr.movieapp_project.data.api.MovieDBInterface
import com.androidcodr.movieapp_project.data.repository.MovieDetailsNetworkDataSource
import com.androidcodr.movieapp_project.data.repository.NetworkState
import com.androidcodr.movieapp_project.data.value_object.MovieDetails
import io.reactivex.disposables.CompositeDisposable

class MovieDetailsRepository(private val apiService: MovieDBInterface) {

    private lateinit var movieDetailsNetworkDataSource: MovieDetailsNetworkDataSource

    fun fetchMovieDetails(compositeDisposable: CompositeDisposable, movieId: Int): LiveData<MovieDetails> {
        if (!::movieDetailsNetworkDataSource.isInitialized) {
            movieDetailsNetworkDataSource = MovieDetailsNetworkDataSource(apiService, compositeDisposable)
        }

        movieDetailsNetworkDataSource.fetchMovieDetails(movieId)

        return movieDetailsNetworkDataSource.downloadedMovieResponse
    }

    fun getMovieDetailsNetworkState(): LiveData<NetworkState> {
        if (!::movieDetailsNetworkDataSource.isInitialized) {
            // Handle the situation where network data source is not initialized
            // This might be an error or just a case where it's not meant to be used
        }

        return movieDetailsNetworkDataSource.networkState
    }
}
