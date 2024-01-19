   package com.androidcodr.movieapp_project.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.androidcodr.movieapp_project.R
import com.androidcodr.movieapp_project.ui.movie_details.Movie

   class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btn: Button = findViewById(R.id.btn)
        btn.setOnClickListener{
            val intent = Intent(this, Movie::class.java)
            intent.putExtra("id", 399579)
            this.startActivity(intent)
        }
    }
} 