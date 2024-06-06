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
import com.nickterhaar.tripsplit.dao.TripMemberDao
import com.nickterhaar.tripsplit.models.Trip
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddTripActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var trips: TripDao
    private lateinit var tripMembers: TripMemberDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_trip)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setTitle("Add New Trip")

        db = AppDatabase.getDatabase(this)

        val newTripTitle = findViewById<EditText>(R.id.addTripTitleInput)
        val newTripDate = findViewById<EditText>(R.id.addTripDateInput)
        val newTripNotes = findViewById<EditText>(R.id.addTripNoteInput)

        if (intent.hasExtra("currentUserId")) {
            val adminId = intent.getIntExtra("currentUserId", 0)

            val addTripBtn = findViewById<Button>(R.id.addTripBtn)
            addTripBtn.setOnClickListener {
                val tripTitle = newTripTitle.text.toString()
                val tripDate = newTripDate.text.toString()
                val tripAdminId = adminId
                val tripNotes = newTripNotes.text.toString()

                trips = db.tripDao()
                tripMembers = db.tripMemberDao()

                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        var newTripId = trips.insertTrip(Trip(tripTitle, tripDate, tripAdminId,
                            tripNotes)).toString().replace("[","").replace("]", "").toInt()

                        tripMembers.insertTripMember(newTripId, tripAdminId)
                    }
                }
                finish()
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