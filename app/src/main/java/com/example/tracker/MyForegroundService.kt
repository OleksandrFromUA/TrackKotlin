package com.example.tracker

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.example.data.LocationData
import com.example.database.MyRoomDB
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class MyForegroundService
@Inject
constructor
    (private val myRoomDB: MyRoomDB) : Service() {
    private var job: Job? = null
    private val CHANNEL_ID = "my_channel"
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        setupLocationUpdates()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForegroundService()
        return START_STICKY
    }

    private suspend fun saveLocationData(location: Location) {
        try {
            val userId = FirebaseAuth.getInstance().currentUser?.uid

           if(userId!=null){
                val timeCoordinate = System.currentTimeMillis()
                val locationUser =
                    LocationData(null, location.latitude, location.longitude, timeCoordinate, 0)

                val newCoordinateInRoom = myRoomDB.getLocationDao().insertLocation(locationUser)

                if (newCoordinateInRoom != -1L) {

                    val timeToServer = System.currentTimeMillis()
                    val locationData = hashMapOf(
                        "userId" to userId,
                        "latitude" to location.latitude,
                        "longitude" to location.longitude,
                        "timeCoordinate" to timeCoordinate,
                        "timeToServer" to timeToServer
                    )

                    FirebaseFirestore.getInstance()
                        .collection("location")
                        .document(userId)
                        .set(locationData, SetOptions.merge())
                        .addOnSuccessListener {
                            Log.i("log", "Data sent to cloud")
                            job = CoroutineScope(Dispatchers.IO).launch {
                                deleteDataFromRoom(locationUser)}
                        }
                        .addOnFailureListener { e ->
                            Log.i("log", "Data not sent to cloud: $e")
                        }
                } else {
                    Log.i("log", "Database is empty")
                }

            } else {
                Log.i("log", "Current user is missing")
            }
        } catch (e: Exception) {
            Log.e("log", "Error saving location data", e)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "My Location Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager: NotificationManager =
                getSystemService(NotificationManager::class.java)!!
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun startForegroundService() {
        val notification: Notification = createNotification()
        startForeground(1, notification)
    }

    private fun stopForeground() {
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
    }

    private fun createNotification(): Notification {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.my_foreground_service))
            .setContentText(getString(R.string.tracking_location))
            .setSmallIcon(R.drawable.location_searching)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        return builder.build()
    }

    private fun setupLocationUpdates() {
        /*val locationRequest = LocationRequest.Builder()
            .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
            .setInterval(10 * 60 * 1000)
            .setSmallestDisplacement(60.0f)
            .build()*/

        /* val locationRequest = LocationRequest.create().apply {
             priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
             interval = 10 * 60 * 1000
             smallestDisplacement = 60.0f }*/


        /* val builder = LocationRequest.Builder(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
             .setInterval(10 * 60 * 1000)
             .setMinUpdateDistanceMeters(60.0f)*/

        val locationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
            .setInterval(10 * 60 * 1000)
            .setSmallestDisplacement(60.0f)

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                  //  Log.e("tag", "Latitude: ${location.latitude} Longitude: ${location.longitude}")
                    job = CoroutineScope(Dispatchers.IO).launch {
                        saveLocationData(location)
                    }
                }
            }
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationProviderClient.requestLocationUpdates(
            // builder.build(),
            locationRequest,
            locationCallback,
            null
        )
    }

    private suspend fun deleteDataFromRoom(locationData: LocationData) {
        try {
            withContext(Dispatchers.IO) {
                myRoomDB.getLocationDao().deleteLocationFromRoom(locationData)
            }
            Log.i("tag", "Operations performed successfully")
        } catch (e: Exception) {
            Log.e("log", "Failed to delete data from Room", e)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopForeground()
        job?.cancel()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}