package com.fitcore.di

import com.fitcore.data.local.dao.FoodDao
import com.fitcore.data.local.dao.FoodLogDao
import com.fitcore.data.local.dao.MealDao
import com.fitcore.data.local.dao.WaterLogDao
import com.fitcore.data.local.dao.WeightDao
import com.fitcore.data.local.dao.WorkoutDao
import com.fitcore.data.repository.FoodRepositoryImpl
import com.fitcore.data.repository.MealRepositoryImpl
import com.fitcore.data.repository.ProgressRepositoryImpl
import com.fitcore.data.repository.WorkoutRepositoryImpl
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideMealRepository(
        mealDao: MealDao,
        foodDao: FoodDao,
        foodLogDao: FoodLogDao,
        waterLogDao: WaterLogDao
    ): MealRepositoryImpl {
        return MealRepositoryImpl(mealDao, foodDao, foodLogDao, waterLogDao)
    }

    @Provides
    @Singleton
    fun provideFoodRepository(
        foodDao: FoodDao
    ): FoodRepositoryImpl {
        return FoodRepositoryImpl(foodDao)
    }

    @Provides
    @Singleton
    fun provideWorkoutRepository(
        workoutDao: WorkoutDao,
        gson: Gson
    ): WorkoutRepositoryImpl {
        return WorkoutRepositoryImpl(workoutDao, gson)
    }

    @Provides
    @Singleton
    fun provideProgressRepository(
        weightDao: WeightDao
    ): ProgressRepositoryImpl {
        return ProgressRepositoryImpl(weightDao)
    }
}
