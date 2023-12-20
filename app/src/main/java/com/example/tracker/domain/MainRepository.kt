package com.example.tracker.domain

import android.annotation.SuppressLint
import com.example.data.UserData
import com.example.database.MyRoomDB
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MainRepository
@Inject
constructor(private val myRoomDB: MyRoomDB) {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

      @SuppressLint("SuspiciousIndentation")
    fun getUserById(): Flow<List<UserData>>? {
    val uid: String? = firebaseAuth.currentUser?.uid
        return uid?.let {
            myRoomDB.getUserDao().getUserById(it) }
    }


        /* @SuppressLint("SuspiciousIndentation")
    fun getUserById(scope: CoroutineScope): Flow<List<UserData>>? {
        val uid: String? = firebaseAuth.currentUser?.uid
        val userFlow = uid?.let {
            myRoomDB.getUserDao().getUserById(it)
        }

        userFlow?.onEach { users ->
            Log.d("MainRepository", "Users from database: $users")
        }?.launchIn(scope = scope)

        return userFlow
    }*/

}