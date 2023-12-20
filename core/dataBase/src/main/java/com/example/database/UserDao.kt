package com.example.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.data.UserData
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Query("SELECT * FROM users WHERE uid = :uid")
    fun getUserById(uid: String): Flow<List<UserData>>

    @Insert
    fun insertUser(user: UserData): Long

     @Query("SELECT * FROM users")
    fun getAllUsers(): List<UserData>

    @Query("DELETE FROM users")
    fun deleteAllUsers()

    @Query("SELECT * FROM users")
    fun getUsersFromRoom(): List<UserData>
}