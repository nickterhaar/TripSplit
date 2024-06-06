package com.nickterhaar.tripsplit.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.nickterhaar.tripsplit.models.User

@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    fun getUsers(): LiveData<List<User>>

    @Query("SELECT * FROM user WHERE uid = :userId")
    fun getUser(userId: Int): LiveData<User>

    @Update
    fun updateUser(vararg user: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(vararg user: User)

    @Delete
    fun deleteUser(user: User)

    @Query("SELECT COUNT(*) FROM user")
    fun getTotalUserCount(): Int
}