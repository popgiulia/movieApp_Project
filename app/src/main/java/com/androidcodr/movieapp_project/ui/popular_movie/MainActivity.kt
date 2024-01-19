package com.androidcodr.movieapp_project.ui.popular_movie

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androidcodr.movieapp_project.R
import com.androidcodr.movieapp_project.data.api.MovieDBInterface
import com.androidcodr.movieapp_project.data.api.MovieDB_Client
import com.androidcodr.movieapp_project.data.repository.NetworkState
import com.androidcodr.movieapp_project.ui.movie_details.Movie
import com.androidcodr.movieapp_project.data.value_object.Movie2
import com.androidcodr.movieapp_project.ui.movie_details.MovieViewModel
import com.androidcodr.movieapp_project.ui.popular_movie.MainActivityViewModel

class MainActivity : AppCompatActivity() {

    lateinit var mainViewModel: MainActivityViewModel

    lateinit var movieRepository: MoviePagedListRepository

    lateinit var rvMovieList: RecyclerView
    lateinit var progressBarPopular: ProgressBar
    lateinit var txtErrorPopular: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        /* val btn: Button = findViewById(R.id.btn)
        btn.setOnClickListener{
            val intent = Intent(this, Movie::class.java)
            intent.putExtra("id", 399579)
            this.startActivity(intent)
        }  */

        val apiService: MovieDBInterface = MovieDB_Client.getClient()
        movieRepository = MoviePagedListRepository(apiService)
        mainViewModel = retrieveViewModel()
        val movieAdapter = PopularMoviePagedListAdapter(this)
        val gridLayoutManager = GridLayoutManager(this, 3)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val viewType: Int = movieAdapter.getItemViewType(position)
                return if (viewType == movieAdapter.MOVIE_VIEW_TYPE) 1 else 3
            }
        }

        rvMovieList = findViewById(R.id.rv_movie_list)
        progressBarPopular = findViewById(R.id.progress_bar_popular) // Initialize progressBarPopular
        txtErrorPopular = findViewById(R.id.txt_error_popular) // Initialize txtErrorPopular

        rvMovieList.layoutManager = gridLayoutManager
        rvMovieList.setHasFixedSize(true)
        rvMovieList.adapter = movieAdapter

        mainViewModel.moviePagedList.observe(this, Observer {
            movieAdapter.submitList(it)
        })

        mainViewModel.networkState.observe(this, Observer {
            progressBarPopular.visibility = if (mainViewModel.listIsEmpty() && it == NetworkState.LOADING) View.VISIBLE else View.GONE
            txtErrorPopular.visibility = if (mainViewModel.listIsEmpty() && it == NetworkState.ERROR) View.VISIBLE else View.GONE
            if (!mainViewModel.listIsEmpty()) {
                movieAdapter.setNetworkState(it)
            }
        })
    }

    private fun retrieveViewModel(): MainActivityViewModel {
        return ViewModelProvider(
            this,
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return MainActivityViewModel(movieRepository) as T
                }
            }
        )[MainActivityViewModel::class.java]
    }

}
