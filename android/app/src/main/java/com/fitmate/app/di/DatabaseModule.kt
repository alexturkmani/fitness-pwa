package com.fitmate.app.di

import android.content.Context
import androidx.room.Room
import com.fitmate.app.data.local.FitMateDatabase
import com.fitmate.app.data.local.dao.*
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
    fun provideDatabase(@ApplicationContext context: Context): FitMateDatabase =
        Room.databaseBuilder(context, FitMateDatabase::class.java, "fitmate.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides fun provideUserProfileDao(db: FitMateDatabase): UserProfileDao = db.userProfileDao()
    @Provides fun provideWorkoutPlanDao(db: FitMateDatabase): WorkoutPlanDao = db.workoutPlanDao()
    @Provides fun provideWorkoutLogDao(db: FitMateDatabase): WorkoutLogDao = db.workoutLogDao()
    @Provides fun provideCustomWorkoutLogDao(db: FitMateDatabase): CustomWorkoutLogDao = db.customWorkoutLogDao()
    @Provides fun provideMealPlanDao(db: FitMateDatabase): MealPlanDao = db.mealPlanDao()
    @Provides fun provideFoodLogDao(db: FitMateDatabase): FoodLogDao = db.foodLogDao()
    @Provides fun provideWeightEntryDao(db: FitMateDatabase): WeightEntryDao = db.weightEntryDao()
    @Provides fun provideWaterLogDao(db: FitMateDatabase): WaterLogDao = db.waterLogDao()
    @Provides fun provideCardioLogDao(db: FitMateDatabase): CardioLogDao = db.cardioLogDao()
}
