package com.nickterhaar.tripsplit

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.nickterhaar.tripsplit.dao.ExpenseDao
import com.nickterhaar.tripsplit.dao.TripDao
import com.nickterhaar.tripsplit.dao.TripMemberDao
import com.nickterhaar.tripsplit.dao.UserDao
import com.nickterhaar.tripsplit.models.Expense
import com.nickterhaar.tripsplit.models.Trip
import com.nickterhaar.tripsplit.models.TripMembers
import com.nickterhaar.tripsplit.models.User

@Database(entities = [User::class, Trip::class, Expense::class, TripMembers::class], version = 40)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun tripDao(): TripDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun tripMemberDao():TripMemberDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tripsplit_db").fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }

    fun checkUserCount(): Boolean {
        return (userDao().getTotalUserCount() == 0)
    }
    fun checkTripCount(): Boolean {
        return (tripDao().getTotalTripCount() == 0)
    }
    fun checkExpenseCount(): Boolean {
        return (expenseDao().getTotalExpenseCount() == 0)
    }

    fun insertUsers() {
        userDao().insertUser(User("Nick", "ter Haar", "nick@tripsplit.dev"))
        userDao().insertUser(User("Frank", "van Viegen", "frank@tripsplit.dev"))
        userDao().insertUser(User("Teachers", "Saxion", "teachers@tripsplit.dev"))
        userDao().insertUser(User("Steve", "Jobs", "steve.jobs@tripsplit.dev"))
        userDao().insertUser(User("Dwight", "Schrute", "dwight@tripsplit.dev"))
        tripMemberDao().insertTripMember(1, 1)
        tripMemberDao().insertTripMember(1, 2)
        tripMemberDao().insertTripMember(2, 1)
        tripMemberDao().insertTripMember(2, 2)
        tripMemberDao().insertTripMember(2, 3)
    }

}