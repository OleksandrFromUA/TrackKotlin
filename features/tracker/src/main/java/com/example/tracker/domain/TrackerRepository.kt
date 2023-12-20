package com.example.tracker.domain


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
        myRoomDB.getUserDao().deleteAllUsers()
        myRoomDB.getLocationDao().deleteAllUsersByCoordination()
    }
}