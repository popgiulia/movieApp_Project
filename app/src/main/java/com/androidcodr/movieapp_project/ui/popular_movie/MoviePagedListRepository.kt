package com.androidcodr.movieapp_project.ui.popular_movie

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.androidcodr.movieapp_project.data.api.MovieDBInterface
import com.androidcodr.movieapp_project.data.api.POST_PER_PAGE
import com.androidcodr.movieapp_project.data.repository.MovieDataSource
import com.androidcodr.movieapp_project.data.repository.MovieDataSourceFactory
import com.androidcodr.movieapp_project.data.repository.NetworkState
import com.androidcodr.movieapp_project.data.value_object.Movie2
import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.Executors

class MoviePagedListRepository (private val apiService : MovieDBInterface) {

    lateinit var moviePagedList: LiveData<PagedList<Movie2>>
    lateinit var moviesDataSourceFactory: MovieDataSourceFactory

    fun fetchLiveMoviePagedList (compositeDisposable: CompositeDisposable) : LiveData<PagedList<Movie2>> {
        moviesDataSourceFactory =
            MovieDataSourceFactory(apiService, compositeDisposable)

        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPageSize(POST_PER_PAGE)
            .build()

        moviePagedList = LivePagedListBuilder(moviesDataSourceFactory, config).build()

        return moviePagedList

    }

    fun getNetworkState(): LiveData<NetworkState> {
        return moviesDataSourceFactory.moviesLiveDataSource.switchMap { it.networkState }
    }

  //  fun getNetworkState(): LiveData<NetworkState> {
    //    return moviesDataSource.networkState
    //}
  private val TAG = "MoviePagedListRepository"
    private val compositeDisposable = CompositeDisposable()

    fun getMoviePagedList(networkState: MutableLiveData<NetworkState>): LiveData<PagedList<Movie2>> {
        Log.d(TAG, "getMoviePagedList: called")

        val movieDataSourceFactory = MovieDataSourceFactory(apiService, compositeDisposable)


        val config = PagedList.Config.Builder()
            .build()

        val executor = Executors.newFixedThreadPool(5)
        val livePagedList = LivePagedListBuilder(movieDataSourceFactory, config)
            .setFetchExecutor(executor)
            .build()

        return livePagedList
    }

}