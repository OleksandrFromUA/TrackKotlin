package com.example.map.domain



import android.util.Log
import com.example.data.LocationData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.Calendar

class MapRepository() {
    private val firebaseFirestore = FirebaseFirestore.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()

    private val _coordinatesFLow = MutableStateFlow<List<LocationData>>(emptyList())

    suspend fun getCoordinatesForDay(selectedDate: Long): Flow<List<LocationData>> {
        val uid = firebaseAuth.currentUser?.uid
        return flow {
            uid.let {
           // if(uid!=null){
                try {
                    val startOfDayTimestamp = startOfDayTimeStamp(selectedDate)
                    val endOfDayTimestamp = endOfDayTimeStamp(selectedDate)

                    val querySnapshot = firebaseFirestore.collection("location")
                        .whereEqualTo("userId", uid)
                        .whereGreaterThanOrEqualTo("timeCoordinate", startOfDayTimestamp)
                        .whereLessThanOrEqualTo("timeToServer", endOfDayTimestamp)
                        .get()
                        .await()

                    val coordinates = querySnapshot.documents.map { documentSnapshot ->
                        documentSnapshot.toObject(LocationData::class.java)!!
                    }
                    _coordinatesFLow.emit(coordinates)
                    // emit(coordinates)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        } //.flowOn(ioDispatcher.value)
    }



private fun startOfDayTimeStamp(selectedDate: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = selectedDate
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun endOfDayTimeStamp(selectedDate: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = selectedDate
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar.timeInMillis
    }

    suspend fun logOutFromFirebase() {
        firebaseAuth.signOut()
    }
}