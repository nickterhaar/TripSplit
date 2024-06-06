package com.nickterhaar.tripsplit.activities

import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.nickterhaar.tripsplit.AppDatabase
import com.nickterhaar.tripsplit.R
import com.nickterhaar.tripsplit.dao.TripDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditTripActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var trips: TripDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_trip)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        db = AppDatabase.getDatabase(this)

        val editTripTitle = findViewById<EditText>(R.id.editTripTitleInput)
        val editTripDate = findViewById<EditText>(R.id.editTripDateInput)
        val saveEditBtn = findViewById<Button>(R.id.editTripBtn)

        if (intent.hasExtra("tripId")) {
            val tripId = intent.getIntExtra("tripId", 0)
            trips = db.tripDao()
            trips.getTrip(tripId).observe(this) {trip ->
                setTitle("Edit ${trip.title}")
                editTripTitle.setText(trip.title)
                editTripDate.setText(trip.endDate)

                saveEditBtn.setOnClickListener {
                    trip.title = editTripTitle.text.toString()
                    trip.endDate = editTripDate.text.toString()
                    lifecycleScope.launch {
                        withContext(Dispatchers.IO) {
                            trips.updateTrip(trip)
                        }
                    }
                    finish()
                }
            }



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