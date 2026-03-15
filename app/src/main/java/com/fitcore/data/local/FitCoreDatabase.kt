package com.fitcore.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.fitcore.data.local.dao.*
import com.fitcore.data.local.entity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

/**
 * Main Room database for FitCore.
 */
@Database(
    entities = [
        FoodEntity::class,
        MealEntity::class,
        WorkoutEntity::class,
        WeightEntry::class,
        FoodLogEntry::class,
        WaterLogEntity::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class FitCoreDatabase : RoomDatabase() {

    abstract fun foodDao(): FoodDao
    abstract fun mealDao(): MealDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun weightDao(): WeightDao
    abstract fun foodLogDao(): FoodLogDao
    abstract fun waterLogDao(): WaterLogDao

    companion object {
        @Volatile
        private var INSTANCE: FitCoreDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): FitCoreDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FitCoreDatabase::class.java,
                    "fitcore_database"
                )
                    .addCallback(FitCoreDatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class FitCoreDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        if (database.foodDao().getFoodCount() == 0) {
                            seedDatabase(database.foodDao(), database.mealDao())
                        }
                    }
                }
            }

            suspend fun seedDatabase(foodDao: FoodDao, mealDao: MealDao) {
                val initialFoods = listOf(
                    FoodEntity(name = "Dal (per bowl)", calories = 180.0, protein = 12.0, carbs = 30.0, fats = 2.0, servingSize = "1 bowl"),
                    FoodEntity(name = "Roti (1 medium)", calories = 120.0, protein = 3.0, carbs = 24.0, fats = 2.0, servingSize = "1 piece"),
                    FoodEntity(name = "Chicken breast (100g)", calories = 165.0, protein = 31.0, carbs = 0.0, fats = 3.6, servingSize = "100g"),
                    FoodEntity(name = "Paneer (100g)", calories = 265.0, protein = 18.0, carbs = 3.0, fats = 20.0, servingSize = "100g"),
                    FoodEntity(name = "Curd (200g)", calories = 120.0, protein = 8.0, carbs = 9.0, fats = 4.0, servingSize = "200g"),
                    FoodEntity(name = "Whey protein (1 scoop)", calories = 120.0, protein = 25.0, carbs = 3.0, fats = 1.5, servingSize = "1 scoop"),
                    FoodEntity(name = "Banana (1 medium)", calories = 105.0, protein = 1.0, carbs = 27.0, fats = 0.3, servingSize = "1 medium"),
                    FoodEntity(name = "Almonds (7 pieces)", calories = 55.0, protein = 2.0, carbs = 2.0, fats = 5.0, servingSize = "7 pieces"),
                    FoodEntity(name = "Walnuts (4 halves)", calories = 65.0, protein = 1.5, carbs = 1.4, fats = 6.5, servingSize = "4 halves"),
                    FoodEntity(name = "Peanut butter (1 tbsp)", calories = 95.0, protein = 4.0, carbs = 3.0, fats = 8.0, servingSize = "1 tbsp"),
                    FoodEntity(name = "Brown bread (1 slice)", calories = 70.0, protein = 3.0, carbs = 12.0, fats = 1.0, servingSize = "1 slice"),
                    FoodEntity(name = "Chach/buttermilk (1 glass)", calories = 40.0, protein = 3.0, carbs = 5.0, fats = 0.9, servingSize = "1 glass"),
                    FoodEntity(name = "Sabzi (avg, per bowl)", calories = 80.0, protein = 3.0, carbs = 12.0, fats = 2.0, servingSize = "1 bowl"),
                    FoodEntity(name = "Rice (1 bowl cooked)", calories = 200.0, protein = 4.0, carbs = 44.0, fats = 0.4, servingSize = "1 bowl"),
                    FoodEntity(name = "Boiled chana (per bowl)", calories = 210.0, protein = 12.0, carbs = 35.0, fats = 3.0, servingSize = "1 bowl"),
                    FoodEntity(name = "Egg white (1)", calories = 17.0, protein = 3.6, carbs = 0.2, fats = 0.0, servingSize = "1 egg"),
                    FoodEntity(name = "Moong dal (per bowl)", calories = 150.0, protein = 10.0, carbs = 26.0, fats = 0.8, servingSize = "1 bowl"),
                    FoodEntity(name = "Oats (40g dry)", calories = 148.0, protein = 5.0, carbs = 25.0, fats = 3.0, servingSize = "40g"),
                    FoodEntity(name = "Cucumber (100g)", calories = 16.0, protein = 0.7, carbs = 3.6, fats = 0.1, servingSize = "100g"),
                    FoodEntity(name = "Tomato (1 medium)", calories = 22.0, protein = 1.0, carbs = 4.8, fats = 0.2, servingSize = "1 medium")
                )
                foodDao.insertFoods(initialFoods)

                // Pre-load initial meal plan for today
                val today = LocalDate.now()
                val meals = listOf(
                    MealEntity(date = today, name = "Early Morning", scheduledTime = LocalTime.of(5, 30), foodIds = emptyList()),
                    MealEntity(date = today, name = "Post Workout", scheduledTime = LocalTime.of(7, 45), foodIds = listOf(6, 7)), 
                    MealEntity(date = today, name = "Breakfast", scheduledTime = LocalTime.of(9, 0), foodIds = listOf(3, 2, 19)), 
                    MealEntity(date = today, name = "Lunch", scheduledTime = LocalTime.of(13, 0), foodIds = listOf(2, 1, 13, 19)), 
                    MealEntity(date = today, name = "Snack", scheduledTime = LocalTime.of(16, 0), foodIds = listOf(12, 8)), 
                    MealEntity(date = today, name = "Dinner", scheduledTime = LocalTime.of(19, 30), foodIds = listOf(5, 4, 9, 19)) 
                )
                meals.forEach { mealDao.insertMeal(it) }
            }
        }
    }
}
