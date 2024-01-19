package com.androidcodr.movieapp_project.data.repository

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.androidcodr.movieapp_project.data.api.MovieDBInterface
import com.androidcodr.movieapp_project.data.value_object.Movie2
import io.reactivex.disposables.CompositeDisposable

class MovieDataSourceFactory(private val apiService : MovieDBInterface, private val compositeDisposable: CompositeDisposable)
    : DataSource.Factory<Int, Movie2>() {

    val moviesLiveDataSource =  MutableLiveData<MovieDataSource>()

    override fun create(): DataSource<Int, Movie2> {
        val movieDataSource = MovieDataSource(apiService,compositeDisposable)
        moviesLiveDataSource.postValue(movieDataSource)
        return movieDataSource
    }
}