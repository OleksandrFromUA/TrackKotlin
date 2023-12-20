package com.example.auth

import com.example.auth.domain.AuthRepository
import com.example.database.MyRoomDB
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped


@Module
@InstallIn(ViewModelComponent::class)
object AuthModule {

    @Provides
    @ViewModelScoped
    fun provideAuthRepository(myRoomDB: MyRoomDB): AuthRepository {
        return AuthRepository(myRoomDB)
    }
}