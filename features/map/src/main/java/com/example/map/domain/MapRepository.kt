package com.example.map.domain


import com.example.data.LocationData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Calendar

class MapRepository {
    private val firebaseFirestore = FirebaseFirestore.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()

   // private val _coordinatesFLow = MutableStateFlow<List<LocationData>>(emptyList())
   // val coordinatesFlow: Flow<List<LocationData>> = _coordinatesFLow.asStateFlow()

    suspend fun getCoordinatesForDay(selectedDate: Long): Flow<List<LocationData>> {
        val uid = firebaseAuth.currentUser?.uid
        return flow {
            uid.let {
                try {
                    val startOfDayTimestamp = startOfDayTimeStamp(selectedDate)
                    val endOfDayTimestamp = endOfDayTimeStamp(selectedDate)

                    val querySnapshot = firebaseFirestore.collection("location")
                        .whereEqualTo("userId", uid)
                        .whereGreaterThanOrEqualTo("timeToServer", startOfDayTimestamp)
                        .whereLessThanOrEqualTo("timeToServer", endOfDayTimestamp)
                        .get()
                        .await()

                    val coordinates = querySnapshot.documents.map { documentSnapshot ->
                        documentSnapshot.toObject(LocationData::class.java)!!
                    }
                    emit(coordinates)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        } //.flowOn(ioDislatcher.value)
    }
  /* @SuppressLint("SuspiciousIndentation")
   suspend fun getCoordinatesForDay(selectedDate: Long) {
    val uid = firebaseAuth.currentUser?.uid
           uid?.let {
               try {
                   val startOfDayTimestamp = startOfDayTimeStamp(selectedDate)
                   val endOfDayTimestamp = endOfDayTimeStamp(selectedDate)

                   val querySnapshot = firebaseFirestore.collection("location")
                       .whereEqualTo("userId", uid)
                       .whereGreaterThanOrEqualTo("timestamp", startOfDayTimestamp)
                       .whereLessThanOrEqualTo("timestamp", endOfDayTimestamp)
                       .get()
                       .await()

                   val coordinates = querySnapshot.documents.map { documentSnapshot ->
                       documentSnapshot.toObject(LocationData::class.java)!!
                   }
             _coordinatesFLow.value = coordinates
               } catch (e: Exception) {
                   e.printStackTrace()
               }
       }
   }*/
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