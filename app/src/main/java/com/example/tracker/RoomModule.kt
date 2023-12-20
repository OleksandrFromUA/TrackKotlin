package com.example.tracker


import android.content.Context
import androidx.room.Room
import com.example.database.LocationDao
import com.example.database.MyRoomDB
import com.example.database.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {


    @Provides
    @Singleton
fun getDB(@ApplicationContext context: Context): MyRoomDB {
    return Room.databaseBuilder(
        context,
        MyRoomDB::class.java, "location_database"
    ).build()
}


   @Provides
   @Singleton
   fun getUserDao(myRoomDB: MyRoomDB): UserDao {
       return myRoomDB.getUserDao()
   }

    @Provides
    @Singleton
    fun getLocationDao(myRoomDB: MyRoomDB): LocationDao {
        return myRoomDB.getLocationDao()
    }

}


