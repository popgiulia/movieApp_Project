package com.androidcodr.movieapp_project.ui.movie_details

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.androidcodr.movieapp_project.R
import com.androidcodr.movieapp_project.data.api.MovieDB_Client
import com.androidcodr.movieapp_project.data.api.MovieDBInterface
import com.androidcodr.movieapp_project.data.api.POSTER_BASE_URL
import com.androidcodr.movieapp_project.data.repository.NetworkState
import com.androidcodr.movieapp_project.data.repository.Status
import com.androidcodr.movieapp_project.data.value_object.MovieDetails
import com.bumptech.glide.Glide


import java.text.NumberFormat
import java.util.*


class Movie : AppCompatActivity() {

    private lateinit var viewModel: MovieViewModel
    lateinit var movieRepository: MovieDetailsRepository

    private lateinit var progressbar: ProgressBar
    private lateinit var txtError: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie)

        val movieId: Int = intent.getIntExtra("id", 1)

        val apiService : MovieDBInterface = MovieDB_Client.getClient()
        movieRepository = MovieDetailsRepository(apiService)

        viewModel = getViewModel(movieId)

        viewModel.movieDetails.observe(this, Observer {
            bindUI(it)
        })

        // Find progress bar and text view by their IDs
        progressbar = findViewById(R.id.progress_bar)
        txtError = findViewById(R.id.txt_error)

        viewModel.networkState.observe(this, Observer {
            progressbar.visibility = if (it == NetworkState.LOADING) View.VISIBLE else View.GONE
            txtError.visibility = if (it.status == Status.FAILED) View.VISIBLE else View.GONE
        })

    }

    fun bindUI( it: MovieDetails){

        val movie_title: TextView = findViewById(R.id.movie_title)
        val movie_tagline: TextView = findViewById(R.id.movie_tagline)
        val movie_release_date: TextView = findViewById(R.id.movie_release_date)
        val movie_rating: TextView = findViewById(R.id.movie_rating)
        val movie_runtime: TextView = findViewById(R.id.movie_runtime)
        val movie_budget: TextView = findViewById(R.id.movie_budget)
        val movie_revenue: TextView = findViewById(R.id.movie_revenue)
        val movie_overview: TextView = findViewById(R.id.movie_overview)

        movie_title.text = it.title
        movie_tagline.text = it.tagline
        movie_release_date.text = it.releaseDate
        movie_rating.text = it.rating
        movie_runtime.text = it.runtime.toString() + " minutes"

        val formatCurrency = NumberFormat.getCurrencyInstance(Locale.US)
        movie_budget.text = formatCurrency.format(it.budget)
        movie_revenue.text = formatCurrency.format(it.revenue)

        movie_overview.text = it.overview

        val iv_movie_poster: ImageView = findViewById(R.id.id_movie_poster)

        val moviePosterURL = POSTER_BASE_URL + it.posterPath
        Glide.with(this)
            .load(moviePosterURL)
            .into(iv_movie_poster);
    }


    private fun getViewModel(movieId: Int): MovieViewModel {
        return ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return MovieViewModel(movieRepository, movieId) as T
            }
        })[MovieViewModel::class.java]
    }


}