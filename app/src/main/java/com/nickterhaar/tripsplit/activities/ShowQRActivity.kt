package com.nickterhaar.tripsplit.activities

import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.nickterhaar.tripsplit.R
import com.squareup.picasso.Picasso

class ShowQRActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_show_qr_activity)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (intent.hasExtra("tripName")) {
            val tripName = intent.getStringExtra("tripName")
            val qrTitle = findViewById<TextView>(R.id.qrTitle)
            qrTitle.setText("${tripName}")
            setTitle("${tripName} - QR")
        }

        if (intent.hasExtra("imageUrl")) {
            val imageUrl = intent.getStringExtra("imageUrl")
            val qrImage = findViewById<ImageView>(R.id.qrImage)
            Picasso.get().load(imageUrl).into(qrImage)
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }
}