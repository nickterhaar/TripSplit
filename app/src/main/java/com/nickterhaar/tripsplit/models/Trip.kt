package com.nickterhaar.tripsplit.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Trip(
    @ColumnInfo(name = "title") var title: String?,
    @ColumnInfo(name = "end_date") var endDate: String?,
    @ColumnInfo(name = "admin_id") var adminId: Int,
    @ColumnInfo(name = "share_url") var shareUrl: String?
){
    @PrimaryKey(autoGenerate = true) var uid: Int? = null
}