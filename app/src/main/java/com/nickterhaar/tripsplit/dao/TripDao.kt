package com.nickterhaar.tripsplit.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.nickterhaar.tripsplit.models.Trip

@Dao
interface TripDao {
    @Query("SELECT * FROM trip")
    fun getTrips(): LiveData<List<Trip>>

    @Query("SELECT * FROM trip WHERE uid = (:tripId)")
    fun getTrip(tripId: Int): LiveData<Trip>

    @Update
    fun updateTrip(vararg trip: Trip)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTrip(vararg trip: Trip): List<Long>

    @Delete
    fun deleteTrip(trip: Trip)

    @Query("SELECT COUNT(*) FROM trip")
    fun getTotalTripCount(): Int
}