package com.example.learnahead_prototyp.DI

import com.example.learnahead_prototyp.Data.Repository.GoalRepository
import com.example.learnahead_prototyp.Data.Repository.IGoalsRepository
import com.google.firebase.firestore.FirebaseFirestore
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
    ): IGoalsRepository{
        return GoalRepository(database)
    }
}