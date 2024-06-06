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
import com.nickterhaar.tripsplit.dao.ExpenseDao
import com.nickterhaar.tripsplit.models.Expense
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddExpenseActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var expenses: ExpenseDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_expense)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setTitle("Add new Trip Expense")
        db = AppDatabase.getDatabase(this)

        val newExpenseTitle = findViewById<EditText>(R.id.addExpenseTitleInput)
        val newExpenseAmount = findViewById<EditText>(R.id.addExpenseAmountInput)
        val newExpenseNote = findViewById<EditText>(R.id.addExpenseNoteInput)

        if (intent.hasExtra("tripId")) {
            val tripId = intent.getIntExtra("tripId", 0)

            val addExpenseBtn = findViewById<Button>(R.id.addExpenseBtn)
            addExpenseBtn.setOnClickListener {
                val expenseTitle = newExpenseTitle.text.toString()
                val expenseAmount = newExpenseAmount.text.toString().toFloat()
                val expenseNote = newExpenseNote.text.toString()
                val expenseUrl = ""

                expenses = db.expenseDao()
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        val newExpense = Expense(expenseTitle, expenseAmount, expenseNote, expenseUrl,
                            tripId)
                        expenses.insertExpense(newExpense)
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