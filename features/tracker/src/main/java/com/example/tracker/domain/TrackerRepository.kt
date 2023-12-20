package com.example.tracker.domain


import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import com.example.database.MyRoomDB
import com.google.firebase.auth.FirebaseAuth


class TrackerRepository
@Inject
constructor(private val myRoomDB: MyRoomDB){
    private val firebaseAuth = FirebaseAuth.getInstance()

    suspend fun deleteDataFromRoom() = withContext(Dispatchers.Default){
        firebaseAuth.signOut()
        Log.d("TrackerRepository", "Before deleting users: ${myRoomDB.getUserDao().getAllUsers()}")

        myRoomDB.getUserDao().deleteAllUsers()
        Log.d("TrackerRepository", "After deleting users: ${myRoomDB.getUserDao().getAllUsers()}")

        myRoomDB.getLocationDao().deleteAllUsersByCoordination()

    }
}