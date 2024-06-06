package com.nickterhaar.tripsplit.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TripMembers(
    @ColumnInfo(name = "trip_id") var tripId: Int?,
    @ColumnInfo(name = "user_id") var userId: Int?
){
    @PrimaryKey(autoGenerate = true) var uid: Int? = null
}