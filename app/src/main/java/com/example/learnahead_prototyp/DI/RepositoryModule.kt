package com.example.learnahead_prototyp.DI

import android.content.SharedPreferences
import com.example.learnahead_prototyp.Data.Repository.AuthRepository
import com.example.learnahead_prototyp.Data.Repository.GoalRepository
import com.example.learnahead_prototyp.Data.Repository.IAuthRepository
import com.example.learnahead_prototyp.Data.Repository.IGoalRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RepositoryModule {

    @Provides
    @Singleton
    fun provideGoalRepository(
        database: FirebaseFirestore
    ): IGoalRepository{
        return GoalRepository(database)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        database: FirebaseFirestore,
        auth: FirebaseAuth,
        appPreferences: SharedPreferences,
        gson: Gson
    ): IAuthRepository {
        return AuthRepository(auth,database, appPreferences, gson)
    }
}