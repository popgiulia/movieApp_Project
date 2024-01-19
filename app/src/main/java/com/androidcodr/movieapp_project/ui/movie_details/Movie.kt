package com.androidcodr.movieapp_project.ui.movie_details

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.MediaScannerConnection
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
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
import com.squareup.picasso.Picasso
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.ref.WeakReference


import java.text.NumberFormat
import java.text.SimpleDateFormat
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

        val apiService: MovieDBInterface = MovieDB_Client.getClient()
        movieRepository = MovieDetailsRepository(apiService)

        viewModel = getViewModel(movieId)

        viewModel.movieDetails.observe(this, Observer {
            bindUI(it)
        })


        progressbar = findViewById(R.id.progress_bar)
        txtError = findViewById(R.id.txt_error)

        viewModel.networkState.observe(this, Observer {
            progressbar.visibility = if (it == NetworkState.LOADING) View.VISIBLE else View.GONE
            txtError.visibility = if (it.status == Status.FAILED) View.VISIBLE else View.GONE
        })

    }

    fun bindUI(it: MovieDetails) {

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
            .dontTransform()
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

    fun salveazaPoster(view: View) {
        // Obține URL-ul posterului filmului din obiectul MovieDetails
        val posterUrl = POSTER_BASE_URL + viewModel.movieDetails.value?.posterPath

        // Obțineți referința către ImageView
        val ivMoviePoster: ImageView = findViewById(R.id.id_movie_poster)

        // Utilizați Picasso pentru a descărca imaginea și a o salva în stocare.
        Picasso.get().load(posterUrl).into(ivMoviePoster, object : com.squareup.picasso.Callback {
            override fun onSuccess() {
                // Executat atunci când imaginea este încărcată cu succes
                val drawable = ivMoviePoster.drawable
                val bitmap = (drawable as BitmapDrawable).bitmap

                // Generează un nume de fișier unic bazat pe data și ora curentă
                val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val fileName = "poster_$timeStamp.jpg"

                // Salvați bitmap-ul în stocarea externă sau internă
                val savedImagePathInternal = saveImageInternal(bitmap, fileName)
                val savedImagePathExternal = saveImageToExternalStorage(bitmap, fileName)

                if (savedImagePathInternal != null) {
                    // Imaginea a fost salvată cu succes intern.
                    Toast.makeText(this@Movie, "Poster salvat cu succes intern în $savedImagePathInternal", Toast.LENGTH_SHORT).show()
                } else if (savedImagePathExternal != null) {
                    // Imaginea a fost salvată cu succes extern.
                    Toast.makeText(this@Movie, "Poster salvat cu succes extern în $savedImagePathExternal", Toast.LENGTH_SHORT).show()
                } else {
                    // Nu s-a putut salva imaginea. Poate fi nevoie să verificați permisiunile de stocare.
                    Toast.makeText(this@Movie, "Eroare la salvarea posterului", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onError(e: Exception?) {
                // Manejați cazul în care încărcarea bitmap-ului a eșuat
                Toast.makeText(this@Movie, "Eroare la încărcarea posterului", Toast.LENGTH_SHORT).show()
            }
        })
    }


    // Funcția pentru a salva intern
    private fun saveImageInternal(bitmap: Bitmap?, fileName: String): String? {
        val contextWrapper = ContextWrapper(applicationContext)
        val directory = contextWrapper.getDir("DirectorImagine", Context.MODE_PRIVATE)
        val file = File(directory, "$fileName.jpg")
        var outputStream: FileOutputStream? = null
        try {
            outputStream = FileOutputStream(file)
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            return file.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }

    // Funcția pentru a salva extern
    private fun saveImageToExternalStorage(bitmap: Bitmap?, fileName: String): String? {
        // Verifică dacă stocarea externă este disponibilă pentru scriere
        if (isExternalStorageWritable()) {
            // Obține directorul de stocare externă public
            val directory =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            // Creează directorul dacă nu există deja
            if (!directory.exists()) {
                directory.mkdirs()
            }

            val file = File(directory, "$fileName.jpg")

            var outputStream: FileOutputStream? = null
            try {
                outputStream = FileOutputStream(file)
                bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                outputStream.flush()
                outputStream.close()

                // Așteaptă o scurtă întârziere înainte de a declanșa scanarea
                Thread.sleep(1000)

                // Actualizează galeria de imagini
                MediaScannerConnection.scanFile(
                    applicationContext,
                    arrayOf(file.absolutePath),
                    arrayOf("image/jpeg"),
                    null
                )

                return file.absolutePath
            } catch (e: IOException) {
                e.printStackTrace()
                return null
            }
        } else {
            // Stocarea externă nu este disponibilă pentru scriere
            return null
        }
    }

    private fun isExternalStorageWritable(): Boolean {
        val state = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED == state
    }
}
