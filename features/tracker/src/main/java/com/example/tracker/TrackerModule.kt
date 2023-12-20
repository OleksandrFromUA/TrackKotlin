package com.example.tracker

import com.example.database.MyRoomDB
import com.example.tracker.domain.TrackerRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped


@Module
@InstallIn(ViewModelComponent::class)
object TrackModule {

    @Provides
    @ViewModelScoped
    fun provideTrackRepository(myRoomDB: MyRoomDB): TrackerRepository {
        return TrackerRepository(myRoomDB)
    }
}