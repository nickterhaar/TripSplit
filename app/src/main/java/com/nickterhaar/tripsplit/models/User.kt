package com.nickterhaar.tripsplit.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @ColumnInfo(name = "first_name") var firstName: String?,
    @ColumnInfo(name = "last_name") var lastName: String?,
    @ColumnInfo(name = "email") var email: String?
){
    @PrimaryKey(autoGenerate = true) var uid: Int? = null
}