package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "location")
data class LocationData(

    @PrimaryKey(autoGenerate = true) var id: Int? = null,
    var latitude: Double,

    var longitude: Double,

    var timeGettingCoordinate: Long,

    var timeToServer: Long
)
