package com.nexal.app.di

import android.content.Context
import androidx.room.Room
import com.nexal.app.data.local.NexalDatabase
import com.nexal.app.data.local.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): NexalDatabase =
        Room.databaseBuilder(context, NexalDatabase::class.java, "nexal.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides fun provideUserProfileDao(db: NexalDatabase): UserProfileDao = db.userProfileDao()
    @Provides fun provideWorkoutPlanDao(db: NexalDatabase): WorkoutPlanDao = db.workoutPlanDao()
    @Provides fun provideWorkoutLogDao(db: NexalDatabase): WorkoutLogDao = db.workoutLogDao()
    @Provides fun provideCustomWorkoutLogDao(db: NexalDatabase): CustomWorkoutLogDao = db.customWorkoutLogDao()
    @Provides fun provideMealPlanDao(db: NexalDatabase): MealPlanDao = db.mealPlanDao()
    @Provides fun provideFoodLogDao(db: NexalDatabase): FoodLogDao = db.foodLogDao()
    @Provides fun provideWeightEntryDao(db: NexalDatabase): WeightEntryDao = db.weightEntryDao()
    @Provides fun provideWaterLogDao(db: NexalDatabase): WaterLogDao = db.waterLogDao()
    @Provides fun provideCardioLogDao(db: NexalDatabase): CardioLogDao = db.cardioLogDao()
}
