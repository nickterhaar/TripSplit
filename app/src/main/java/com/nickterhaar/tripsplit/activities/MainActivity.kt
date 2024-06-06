package com.nickterhaar.tripsplit.activities

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.nickterhaar.tripsplit.AppDatabase
import com.nickterhaar.tripsplit.R
import com.nickterhaar.tripsplit.adapters.TripListAdapter
import com.nickterhaar.tripsplit.dao.ExpenseDao
import com.nickterhaar.tripsplit.dao.TripDao
import com.nickterhaar.tripsplit.dao.UserDao
import com.nickterhaar.tripsplit.models.Expense
import com.nickterhaar.tripsplit.models.Trip
import com.nickterhaar.tripsplit.custom_views.ExpandableHeightGridView
import com.nickterhaar.tripsplit.dao.TripMemberDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray


class MainActivity : AppCompatActivity() {



    private val expensesList = mutableListOf<Expense>()

    private lateinit var allTripsGrid: ExpandableHeightGridView
    private lateinit var db: AppDatabase
    private lateinit var users: UserDao
    private lateinit var trips: TripDao
    private lateinit var expenses: ExpenseDao
    private lateinit var tripmembers: TripMemberDao


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db = AppDatabase.getDatabase(this)
        users = db.userDao()
        trips = db.tripDao()
        expenses = db.expenseDao()
        tripmembers = db.tripMemberDao()

        fun getAllTrips() {
            allTripsGrid = findViewById(R.id.gridLayout)
            allTripsGrid.isExpanded = true
            trips.getTrips().observe(this) { gridTrips ->
                allTripsGrid.adapter = TripListAdapter(this, gridTrips)

                allTripsGrid.setOnItemClickListener { parent, view, position, id ->
                    val intent = Intent(this, ViewTripActivity::class.java)
                    intent.putExtra("tripId", id.toInt())
                    startActivity(intent)
                }
            }
        }

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                if (db.checkUserCount()) {
                    db.insertUsers()
                }
                if (db.checkTripCount()) {
                    val queue = Volley.newRequestQueue(this@MainActivity)
                    val url = "https://75d4f.text99.com/api/trips"
                    val stringRequest = StringRequest(Request.Method.GET, url,
                        { response ->
                            val jsonResponse = JSONArray(response)
                            for (i in 0 until jsonResponse.length()) {
                                val trip = jsonResponse.getJSONObject(i)
                                val tripId = trip.getString("id")
                                val tripTitle = trip.getString("title")
                                val tripDate = trip.getString("end_date")
                                val tripAdmin = trip.getString("admin_id")
                                val tripShareUrl = trip.getString("share_url")

                                val newTrip = Trip(tripTitle, tripDate, tripAdmin.toInt(),
                                    tripShareUrl)
                                lifecycleScope.launch {
                                    withContext(Dispatchers.IO) {
                                        trips.insertTrip(newTrip)
                                    }
                                }
                            }
                        },
                        { println("That didn't work!") })
                    queue.add(stringRequest)
                }
                if (db.checkExpenseCount()) {
                    val queue = Volley.newRequestQueue(this@MainActivity)
                    val url = "https://75d4f.text99.com/api/expenses"
                    val stringRequest = StringRequest(Request.Method.GET, url,
                        { response ->
                            val jsonResponse = JSONArray(response)
                            for (i in 0 until jsonResponse.length()) {
                                val expense = jsonResponse.getJSONObject(i)
                                val expenseTitle = expense.getString("title")
                                val expenseAmount = expense.getString("amount")
                                val expenseNote = expense.getString("note")
                                val expenseUrl = expense.getString("url")
                                val expenseTripId = expense.getString("trip_id")

                                val newExpense = Expense(expenseTitle,
                                    expenseAmount.toFloat(),
                                    expenseNote, expenseUrl, expenseTripId.toInt())
                                lifecycleScope.launch {
                                    withContext(Dispatchers.IO) {
                                        expenses.insertExpense(newExpense)
                                    }
                                }
                                expensesList.add(newExpense)
                            }
                        },
                        { println("That didn't work!") })
                    queue.add(stringRequest)
                }
            }
            getAllTrips()
        }

        val addTripBtn = findViewById<Button>(R.id.addTripBtn)
        addTripBtn.setOnClickListener {
            val intent = Intent(this, AddTripActivity::class.java)
            intent.putExtra("currentUserId", 1)
            startActivity(intent)
        }
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
//        TODO: write on instance save
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
//        TODO: write on instance restore
    }
}