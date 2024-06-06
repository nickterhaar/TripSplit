package com.nickterhaar.tripsplit.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.nickterhaar.tripsplit.models.TripMembers

@Dao
interface TripMemberDao {
    @Query("SELECT user_id FROM tripmembers WHERE trip_id = :tripId")
    fun getTripMembers(tripId: Int): LiveData<List<Int>>

    @Query("SELECT * FROM tripmembers")
    fun getTripAllMembers(): LiveData<List<TripMembers>>

    @Query("INSERT INTO tripmembers(trip_id, user_id) VALUES(:tripId, :userId)")
    fun insertTripMember(tripId: Int, userId: Int)

    @Query("DELETE FROM tripmembers WHERE trip_id = :tripId AND user_id = :memberId")
    fun deleteTripMember(tripId: Int, memberId: Int)
}