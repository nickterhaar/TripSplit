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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditExpenseActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var expenses: ExpenseDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_expense)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        db = AppDatabase.getDatabase(this)

        val editExpenseTitle = findViewById<EditText>(R.id.editExpenseTitleInput)
        val editExpenseAmount = findViewById<EditText>(R.id.editExpenseAmountInput)
        val editExpenseNote = findViewById<EditText>(R.id.editExpenseNoteInput)
        val saveEditBtn = findViewById<Button>(R.id.editExpenseBtn)

        if (intent.hasExtra("expenseId")) {
            val expenseId = intent.getIntExtra("expenseId", 0)
            expenses = db.expenseDao()
            expenses.getExpense(expenseId).observe(this) { expense ->
                setTitle("Edit ${expense.title}")
                editExpenseTitle.setText(expense.title)
                editExpenseAmount.setText(expense.amount.toString())
                editExpenseNote.setText(expense.note)

                saveEditBtn.setOnClickListener {
                    expense.title = editExpenseTitle.text.toString()
                    expense.amount = editExpenseAmount.text.toString().toFloat()
                    expense.note = editExpenseNote.text.toString()
                    lifecycleScope.launch {
                        withContext(Dispatchers.IO) {
                            expenses.updateExpense(expense)
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