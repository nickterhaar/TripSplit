package com.nickterhaar.tripsplit.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Expense(
    @ColumnInfo(name = "title") var title: String?,
    @ColumnInfo(name = "amount") var amount: Float?,
    @ColumnInfo(name = "note") var note: String?,
    @ColumnInfo(name = "url") var url: String?,
    @ColumnInfo(name = "trip_id") var tripId: Int?
){
    @PrimaryKey(autoGenerate = true) var uid: Int? = null
}