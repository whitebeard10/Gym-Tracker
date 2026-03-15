package com.fitcore.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.fitcore.domain.model.UserProfile
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

@Singleton
class UserPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object PreferencesKeys {
        val DARK_THEME = booleanPreferencesKey("dark_theme")
        val USER_NAME = stringPreferencesKey("user_name")
        val WEIGHT_KG = doublePreferencesKey("weight_kg")
        val HEIGHT_CM = intPreferencesKey("height_cm")
        val GOAL_WEIGHT_KG = doublePreferencesKey("goal_weight_kg")
        val DAILY_CALORIE_TARGET = intPreferencesKey("daily_calorie_target")
        val PROTEIN_TARGET_G = intPreferencesKey("protein_target_g")
        val CARB_TARGET_G = intPreferencesKey("carb_target_g")
        val FAT_TARGET_G = intPreferencesKey("fat_target_g")
        val WATER_TARGET_LITERS = doublePreferencesKey("water_target_liters")
        val STREAK = intPreferencesKey("streak")
        val LAST_ACTIVE_DATE = stringPreferencesKey("last_active_date")
    }

    val darkThemeFlow: Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { it[PreferencesKeys.DARK_THEME] ?: true }

    val userProfileFlow: Flow<UserProfile> = context.dataStore.data
        .map { preferences ->
            UserProfile(
                name = preferences[PreferencesKeys.USER_NAME] ?: "Abish",
                heightCm = preferences[PreferencesKeys.HEIGHT_CM] ?: 160,
                currentWeightKg = preferences[PreferencesKeys.WEIGHT_KG] ?: 59.0,
                goalWeightKg = preferences[PreferencesKeys.GOAL_WEIGHT_KG] ?: 56.0,
                dailyCalorieTarget = preferences[PreferencesKeys.DAILY_CALORIE_TARGET] ?: 1750,
                proteinTargetG = preferences[PreferencesKeys.PROTEIN_TARGET_G] ?: 130,
                carbTargetG = preferences[PreferencesKeys.CARB_TARGET_G] ?: 170,
                fatTargetG = preferences[PreferencesKeys.FAT_TARGET_G] ?: 45,
                waterTargetLiters = preferences[PreferencesKeys.WATER_TARGET_LITERS] ?: 3.5,
                currentStreak = preferences[PreferencesKeys.STREAK] ?: 0
            )
        }

    suspend fun updateDarkTheme(isDark: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.DARK_THEME] = isDark }
    }

    suspend fun updateUserName(name: String) {
        context.dataStore.edit { it[PreferencesKeys.USER_NAME] = name }
    }

    suspend fun updateWeight(weight: Double) {
        context.dataStore.edit { it[PreferencesKeys.WEIGHT_KG] = weight }
    }

    suspend fun updateGoalWeight(goalWeight: Double) {
        context.dataStore.edit { it[PreferencesKeys.GOAL_WEIGHT_KG] = goalWeight }
    }

    suspend fun updateCalorieTarget(target: Int) {
        context.dataStore.edit { it[PreferencesKeys.DAILY_CALORIE_TARGET] = target }
    }

    suspend fun updateProteinTarget(target: Int) {
        context.dataStore.edit { it[PreferencesKeys.PROTEIN_TARGET_G] = target }
    }

    suspend fun updateCarbTarget(target: Int) {
        context.dataStore.edit { it[PreferencesKeys.CARB_TARGET_G] = target }
    }

    suspend fun updateFatTarget(target: Int) {
        context.dataStore.edit { it[PreferencesKeys.FAT_TARGET_G] = target }
    }

    suspend fun updateWaterTarget(target: Double) {
        context.dataStore.edit { it[PreferencesKeys.WATER_TARGET_LITERS] = target }
    }

    suspend fun updateStreak() {
        context.dataStore.edit { preferences ->
            val today = LocalDate.now().toString()
            val lastActive = preferences[PreferencesKeys.LAST_ACTIVE_DATE]
            val currentStreak = preferences[PreferencesKeys.STREAK] ?: 0

            if (lastActive != today) {
                val yesterday = LocalDate.now().minusDays(1).toString()
                if (lastActive == yesterday) {
                    preferences[PreferencesKeys.STREAK] = currentStreak + 1
                } else if (lastActive == null) {
                    preferences[PreferencesKeys.STREAK] = 1
                } else {
                    preferences[PreferencesKeys.STREAK] = 1 // Reset if missed a day
                }
                preferences[PreferencesKeys.LAST_ACTIVE_DATE] = today
            }
        }
    }

    suspend fun clearAll() {
        context.dataStore.edit { it.clear() }
    }
}
