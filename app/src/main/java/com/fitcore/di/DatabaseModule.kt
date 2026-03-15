package com.fitcore.di

import android.content.Context
import com.fitcore.data.local.FitCoreDatabase
import com.fitcore.data.local.dao.FoodDao
import com.fitcore.data.local.dao.FoodLogDao
import com.fitcore.data.local.dao.MealDao
import com.fitcore.data.local.dao.WaterLogDao
import com.fitcore.data.local.dao.WeightDao
import com.fitcore.data.local.dao.WorkoutDao
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideCoroutineScope(): CoroutineScope = CoroutineScope(SupervisorJob())

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
        scope: CoroutineScope
    ): FitCoreDatabase {
        return FitCoreDatabase.getDatabase(context, scope)
    }

    @Provides
    fun provideFoodDao(database: FitCoreDatabase): FoodDao = database.foodDao()

    @Provides
    fun provideMealDao(database: FitCoreDatabase): MealDao = database.mealDao()

    @Provides
    fun provideWorkoutDao(database: FitCoreDatabase): WorkoutDao = database.workoutDao()

    @Provides
    fun provideWeightDao(database: FitCoreDatabase): WeightDao = database.weightDao()

    @Provides
    fun provideFoodLogDao(database: FitCoreDatabase): FoodLogDao = database.foodLogDao()

    @Provides
    fun provideWaterLogDao(database: FitCoreDatabase): WaterLogDao = database.waterLogDao()

    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()
}
