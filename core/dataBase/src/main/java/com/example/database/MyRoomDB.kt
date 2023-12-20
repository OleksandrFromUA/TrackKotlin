package com.example.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.data.LocationData
import com.example.data.UserData

@Database(
    version = 2,
    entities = [
        UserData::class, LocationData::class
    ]
)

abstract class MyRoomDB : RoomDatabase() {
    abstract fun getUserDao(): UserDao
    abstract fun getLocationDao(): LocationDao



    companion object {
        private var instance: MyRoomDB? = null


       /* @Synchronized
        fun getInstance(): MyRoomDB {
            if (instance == null) {
                instance = MyAppApplication.getAppContext()?.let {
                    Room.databaseBuilder(
                        it,
                        MyRoomDB::class.java,
                        "location_database"
                    ).build()
                }
            }
            return instance!!
        }*/
    }

}