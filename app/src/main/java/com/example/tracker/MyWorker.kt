package com.example.tracker


import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.lifecycle.lifecycleScope
import androidx.work.Constraints
import androidx.work.ListenableWorker
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.database.MyRoomDB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import com.example.data.LocationData
import com.example.data.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@HiltWorker
class MyWorker @AssistedInject constructor(
   @Assisted context: Context,
   @Assisted workerParams: WorkerParameters,
   private val myRoomDB: MyRoomDB): Worker(context, workerParams) {

    override fun doWork(): Result {
        TODO("Not yet implemented")
    }
   /*override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
     //  return@withContext try {
       try {
            val coordinatesFromRoom = getCoordinatesFromRoom()
            val usersFromRoom = getUsersFromRoom()

            if (isInternetAvailable()) {
                val successSend = sendCoordinatesToFirebase(usersFromRoom, coordinatesFromRoom)
                if (successSend) {
                    deleteCoordinatesFromRoom(coordinatesFromRoom)
                    Log.e("log", "deleteCoordinatesFromRoom")
                    return@withContext Result.success()
                   // Result.success()
                } else {
                    Log.e("log", "Failed to send data to the cloud")
                    return@withContext Result.retry()
                   // Result.retry()
                }
            } else {
                Log.e("log", "No internet")
               return@withContext Result.retry()
               // Result.retry()
            }
        } catch (e: Exception) {
            e.printStackTrace()
           return@withContext Result.failure()
           //Result.failure()
        }
    }*/

    companion object {
        fun startMyWorker(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            val syncWorkRequest = PeriodicWorkRequest.Builder(
                MyWorker::class.java,
                15,
                TimeUnit.MINUTES
            )
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueue(syncWorkRequest)
        }
    }

    private suspend fun getCoordinatesFromRoom(): List<LocationData> = withContext(Dispatchers.IO) {
        val locationDao = myRoomDB.getLocationDao()
        return@withContext locationDao.getAllCoordinates()
    }

    private suspend fun getUsersFromRoom(): List<UserData> = withContext(Dispatchers.IO) {
        val userDao = myRoomDB.getUserDao()
        return@withContext userDao.getUsersFromRoom()
    }


    private suspend fun isInternetAvailable(): Boolean {
        val connectivityManager =
            applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager

        if (connectivityManager != null) {
            val network = connectivityManager.activeNetwork
            val capabilities = connectivityManager.getNetworkCapabilities(network)

            return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        }

        return false
    }

    private suspend fun sendCoordinatesToFirebase(users: List<UserData>, coordinates: List<LocationData>): Boolean {
        val db = FirebaseFirestore.getInstance()
        val firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser

        currentUser?.let { user ->
            val uidUser = user.uid
            val userReference: CollectionReference = db.collection("users")
            val documentReference: DocumentReference = userReference.document(uidUser)
            val timeCoordinate = System.currentTimeMillis()

            for (user in users) {
                val userData = hashMapOf(
                    "userId" to user.id,
                    "email" to user.email
                )
                documentReference.set(userData, SetOptions.merge())
            }

            val locationReference: CollectionReference = db.collection("location")
            for (locationData in coordinates) {
                val newLocationData = hashMapOf(
                    "userId" to uidUser,
                    "latitude" to locationData.latitude,
                    "longitude" to locationData.longitude,
                    "timeCoordinate" to locationData.timeGettingCoordinate,
                    "timeToServer" to timeCoordinate)
                locationReference.add(newLocationData)
            }
            return true
        } ?: return false
    }

    private suspend fun deleteCoordinatesFromRoom(coordinates: List<LocationData>) {
        try {
            withContext(Dispatchers.IO) {
                myRoomDB.getLocationDao().deleteAllUsersByCoordination()
                Log.e("log", "Coordinates successfully removed from Room database")
            }
        } catch (e: Exception) {
            Log.e("log", "Coordinates successfully removed from Room database: ", e)
        }
    }


}


