package com.androidcodr.movieapp_project.ui.popular_movie

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.androidcodr.movieapp_project.R
import com.androidcodr.movieapp_project.data.api.POSTER_BASE_URL
import com.androidcodr.movieapp_project.data.repository.NetworkState
import com.androidcodr.movieapp_project.data.value_object.Movie2
import com.bumptech.glide.Glide


class PopularMoviePagedListAdapter(public val context: Context) : PagedListAdapter<Movie2, RecyclerView.ViewHolder>(MovieDiffCallback()) {
    val MOVIE_VIEW_TYPE = 1
    val NETWORK_VIEW_TYPE = 2

    private var networkState: NetworkState? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view: View
        if (viewType == MOVIE_VIEW_TYPE) {
            view = layoutInflater.inflate(R.layout.movie_list_item, parent, false)
            return MovieItemViewHolder(view)
        } else {
            view = layoutInflater.inflate(R.layout.network_state_item, parent, false)
            return NetworkStateItemViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == MOVIE_VIEW_TYPE) {
            (holder as MovieItemViewHolder).bind(getItem(position),context)
        }
        else {
            (holder as NetworkStateItemViewHolder).bind(networkState)
        }
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + if (hasExtraRow()) 1 else 0
    }

    private fun hasExtraRow(): Boolean {
        return networkState != null && networkState != NetworkState.LOADED
    }

    override fun getItemViewType(position: Int): Int {
        return if (hasExtraRow() && position == itemCount - 1) {
            NETWORK_VIEW_TYPE
        } else {
            MOVIE_VIEW_TYPE
        }
    }

    class MovieDiffCallback : DiffUtil.ItemCallback<Movie2>() {
        override fun areItemsTheSame(oldItem: Movie2, newItem: Movie2): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Movie2, newItem: Movie2): Boolean {
            return oldItem == newItem
        }

    }

    class MovieItemViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        private val movieTitle = itemView.findViewById<TextView>(R.id.cv_movie_title)
        private val movieReleaseDate = itemView.findViewById<TextView>(R.id.cv_movie_release_date)
        private val moviePoster = itemView.findViewById<ImageView>(R.id.cv_iv_movie_poster)

        fun bind(movie: Movie2?,context: Context) {
            movieTitle.text = movie?.title
            movieReleaseDate.text = movie?.releaseDate

            val moviePosterURL = POSTER_BASE_URL + movie?.posterPath
            Glide.with(context)
                .load(moviePosterURL)
                .into(moviePoster)

            itemView.setOnClickListener{
                val intent = Intent(context, Movie2::class.java)
                intent.putExtra("id", movie?.id)
                context.startActivity(intent)
            }
        }
    }

    class NetworkStateItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val progressBar = itemView.findViewById<ProgressBar>(R.id.progress_bar)
        private val errorMsg = itemView.findViewById<TextView>(R.id.error_msg)

        fun bind(networkState: NetworkState?) {
            if (networkState != null && networkState == NetworkState.LOADING) {
                progressBar.visibility = View.VISIBLE
            } else {
                progressBar.visibility = View.GONE
            }

            if (networkState != null && networkState == NetworkState.ERROR) {
                errorMsg.visibility = View.VISIBLE
                errorMsg.text = networkState.msg
            } else {
                errorMsg.visibility = View.GONE
            }
        }
    }

    fun setNetworkState(newNetworkState: NetworkState) {
        val previousState = this.networkState
        val hadExtraRow = hasExtraRow()
        this.networkState = newNetworkState
        val hasExtraRow = hasExtraRow()
        if (hadExtraRow != hasExtraRow) {
            if (hadExtraRow) {
                notifyItemRemoved(super.getItemCount())
            } else {
                notifyItemInserted(super.getItemCount())
            }
        } else if (hasExtraRow && previousState != newNetworkState) {
            notifyItemChanged(itemCount - 1)
        }
    }

}