package com.nickterhaar.tripsplit.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.nickterhaar.tripsplit.models.Expense

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expense")
    fun getAllExpenses(): LiveData<List<Expense>>

    @Query("SELECT * FROM expense WHERE uid = :expenseId")
    fun getExpense(expenseId: Int): LiveData<Expense>

    @Query("SELECT * FROM expense WHERE trip_id = :tripId")
    fun getExpenses(tripId: Int): LiveData<List<Expense>>

    @Insert
    fun insertExpense(vararg expense: Expense)

    @Update
    fun updateExpense(vararg expense: Expense)

    @Query("SELECT COUNT(*) FROM expense")
    fun getTotalExpenseCount(): Int

    @Delete
    fun deleteExpense(expense: Expense)
}