package com.nickterhaar.tripsplit.activities

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isInvisible
import androidx.lifecycle.lifecycleScope
import com.nickterhaar.tripsplit.AppDatabase
import com.nickterhaar.tripsplit.R
import com.nickterhaar.tripsplit.dao.ExpenseDao
import com.nickterhaar.tripsplit.dao.TripDao
import com.nickterhaar.tripsplit.dao.TripMemberDao
import com.nickterhaar.tripsplit.dao.UserDao
import com.nickterhaar.tripsplit.models.Expense
import com.nickterhaar.tripsplit.models.Trip
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ViewTripActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var users: UserDao
    private lateinit var trips: TripDao
    private lateinit var expenses: ExpenseDao
    private lateinit var tripMembers: TripMemberDao

    private lateinit var currentTrip: Trip
    private lateinit var currentExpenses: List<Expense>
    private lateinit var currentMembers: List<Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_view_trip)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db = AppDatabase.getDatabase(this)

        trips = db.tripDao()

        val shareTripBtn = findViewById<Button>(R.id.shareTripBtn)
        val paymentQRBtn = findViewById<Button>(R.id.paymentQRBtn)


        val organizerList = findViewById<LinearLayout>(R.id.tripOrganizer)
        val totalCosts = findViewById<LinearLayout>(R.id.tripCosts)
        val membersList = findViewById<LinearLayout>(R.id.tripViewMembersList)

        var totalTripCost = 0.0
        var tripMemberCount = 0

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (intent.hasExtra("tripId")) {
            val intentTripId = intent.getIntExtra("tripId", 0)
            trips.getTrip(intentTripId).observe(this) { trip ->
                currentTrip = trip
                setTitle("${trip.title}")

                val tripDateText = "Trip Date: ${currentTrip.endDate}"
                val tripDate = findViewById<TextView>(R.id.tripViewDate)
                tripDate.setText(tripDateText)

                users = db.userDao()

                users.getUser(trip.adminId).observe(this) { user ->
                    organizerList.removeAllViews()
                    val view = LayoutInflater.from(this).inflate(R.layout.trip_member, null)
                    val organizerName = "${user.firstName} ${user.lastName}"
                    view.findViewById<TextView>(R.id.memberName).setText(organizerName)

                    organizerList.addView(view)
                }

                tripMembers = db.tripMemberDao()
                tripMembers.getTripMembers(currentTrip.uid!!).observe(this) { memberIds ->
                    membersList.removeAllViews()
                    currentMembers = memberIds
                    tripMemberCount = memberIds.size
                    memberIds.forEach { memberId ->
                        if (memberId != currentTrip.adminId) {
                            users.getUser(memberId).observe(this) { user ->
                                val view = LayoutInflater.from(this).inflate(R.layout
                                    .trip_member, null)
                                val memberName = "${user.firstName} ${user.lastName}"
                                view.findViewById<TextView>(R.id.memberName).setText(memberName)
                                membersList.addView(view)
                            }
                        }
                    }
                }

                expenses = db.expenseDao()
                expenses.getExpenses(trip.uid!!).observe(this) { expenses ->
                    currentExpenses = expenses
                    val expensesList = findViewById<LinearLayout>(R.id.tripViewExpensesList)
                    expensesList.removeAllViews()
                    totalTripCost = 0.0
                    expenses.forEach { expense ->
                        totalTripCost += expense.amount!!
                        val view = LayoutInflater.from(this).inflate(R.layout.trip_expense, null)
                        view.findViewById<TextView>(R.id.expenseTitle).setText(expense.title)
                        view.findViewById<TextView>(R.id.expenseAmount).setText("€ ${expense
                            .amount.toString().replace(".", ",").replace(",0", "")}")

                        view.setOnClickListener {
                            val intent = Intent(this, EditExpenseActivity::class.java)
                            intent.putExtra("expenseId", expense.uid!!.toInt())
                            startActivity(intent)
                        }
                        expensesList.addView(view)
                    }

                    if (totalTripCost != 0.0) {
                        paymentQRBtn.isInvisible = false
                        val memberCost = (totalTripCost / tripMemberCount).toString()
                            .replace(".", ",")
                        paymentQRBtn.setOnClickListener {
                            val imageUrl = "https://epc-qr" +
                                    ".eu?iban=NL12BANK0123456789&euro=${memberCost}," +
                                    "&bname=J.%20Doe&logo=none&cut=lr&colour=orange&pixel=16"
                            val intent = Intent(this, ShowQRActivity::class.java)
                            intent.putExtra("imageUrl", imageUrl)
                            intent.putExtra("tripName", currentTrip.title)
                            startActivity(intent)
                        }
                    }
                    totalCosts.removeAllViews()
                    val view = LayoutInflater.from(this).inflate(R.layout.trip_costs, null)
                    val totalCostString = "€ ${totalTripCost.toString().replace(".", ",")}"
                    val memberCostString = "€ ${(totalTripCost / tripMemberCount).toString().replace(".", ",")} per member"
                    view.findViewById<TextView>(R.id.totalCost).setText(totalCostString.replace
                        (",0", ""))
                    view.findViewById<TextView>(R.id.memberCost).setText(memberCostString.replace
                        (",0", ""))
                    totalCosts.addView(view)
                }

            }
        }

        val addExpenseBtn = findViewById<Button>(R.id.addExpenseBtn)
        addExpenseBtn.setOnClickListener {
            val intent = Intent(this, AddExpenseActivity::class.java)
            intent.putExtra("tripId", currentTrip.uid)
            startActivity(intent)
        }

        shareTripBtn.setOnClickListener {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "Share '${currentTrip.title}'")
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.custom_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            R.id.editTrip -> {
                val intent = Intent(this, EditTripActivity::class.java)
                intent.putExtra("tripId", currentTrip.uid)
                startActivity(intent)
            }
            R.id.deleteTrip -> {
                val builder = AlertDialog.Builder(this)
                builder.setMessage("Are you sure you want to delete ${currentTrip.title}")
                builder.setPositiveButton("Delete") {dialog, id ->

                    lifecycleScope.launch {
                        withContext(Dispatchers.IO) {
                            trips.deleteTrip(currentTrip)
                            currentExpenses.forEach { expense ->
                                expenses.deleteExpense(expense)
                            }
                            currentMembers.forEach { memberId ->
                                tripMembers.deleteTripMember(currentTrip.uid!!, memberId)
                            }
                        }
                    }
                }
                builder.setNegativeButton("Cancel") {dialog, id ->
                    dialog.cancel()
                }
                builder.show()
            }
        }

        return super.onOptionsItemSelected(item)
    }
}