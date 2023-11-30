package com.example.database

import androidx.room.RoomDatabase

abstract class MyRoomDB: RoomDatabase() {

    companion object{
    private var instance: MyRoomDB? = null

    }
}