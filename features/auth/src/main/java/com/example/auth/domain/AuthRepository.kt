package com.example.auth.domain

import android.util.Log
import com.example.data.LocationData
import com.example.data.UserData
import com.example.database.MyRoomDB
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthRepository
@Inject
constructor(private val myRoomDB: MyRoomDB) {
    private val firebaseAuth = FirebaseAuth.getInstance()

    suspend fun saveToLocal(email: String) {
        withContext(Dispatchers.IO) {
            try {
                val user = UserData(uid = firebaseAuth.currentUser?.uid ?: "", email = email)
                val location = LocationData(
                    latitude = 0.0,
                    longitude = 0.0,
                    timeGettingCoordinate = 0,
                    timeToServer = 0
                )

                myRoomDB.getUserDao().insertUser(user)
                myRoomDB.getLocationDao().insertLocation(location)

            } catch (e: Exception) {
                Log.e("error", "произошла ошибка: ", e)
            }
        }
    }


    suspend fun saveUserToRoom(uid: String, email: String): CompletableDeferred<Unit> {
        return withContext(Dispatchers.IO) {
            val deferred = CompletableDeferred<Unit>()
            try {
                val user = UserData(uid = firebaseAuth.currentUser?.uid ?: "", email = email)
                val location = LocationData(
                    latitude = 0.0,
                    longitude = 0.0,
                    timeGettingCoordinate = 0,
                    timeToServer = 0
                )

                val userId = myRoomDB.getUserDao().insertUser(user)
                val locationU = myRoomDB.getLocationDao().insertLocation(location)

                if (userId != -1L && locationU != -1L) {
                    deferred.complete(Unit)
                } else {
                    deferred.completeExceptionally(Exception("Failed to insert user into Room database"))
                }

            } catch (e: Exception) {
                deferred.completeExceptionally(e)
            }
            deferred
        }
    }
}

