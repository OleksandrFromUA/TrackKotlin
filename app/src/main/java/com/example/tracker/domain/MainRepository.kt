package com.example.tracker.domain

import android.annotation.SuppressLint
import com.example.data.UserData
import com.example.database.MyRoomDB
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

//@AndroidEntryPoint
class MainRepository
@Inject
constructor(private val myRoomDB: MyRoomDB){

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    @SuppressLint("SuspiciousIndentation")
    fun getUserById(): Flow<List<UserData>>? {
    val uid: String? = firebaseAuth.currentUser?.uid
        return uid?.let {
            myRoomDB.getUserDao().getUserById(it) }
    }


  }
