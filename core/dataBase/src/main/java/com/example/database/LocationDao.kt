package com.example.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.data.LocationData
@Dao
interface LocationDao {

    @Insert
     fun insertLocation(location: LocationData): Long

     @Query("DELETE FROM location")
     fun deleteAllUsersByCoordination()

     @Query("SELECT * FROM location")
     fun getAllCoordinates(): List<LocationData>

     @Delete
     fun deleteLocationFromRoom(location: LocationData)
}