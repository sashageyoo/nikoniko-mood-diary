package com.example.moodtracker

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
@Entity(tableName = "mood_table")
data class MoodEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val moodIndex: Int,
    val notes: String,
    val date: String,

    val weather: String? = null,
    val social: String? = null,
    val imageUri: String? = null
)

@Entity(tableName = "habit_table")
data class Habit(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val iconRes: Int,
    val title: String,
    val description: String,
    val points: Int,
    val isCompleted: Boolean = false,
    val reminders: String = "07:00 AM",
    val streak: Int = 0,
    val totalCount: Int = 0,
    val lastCompletedDate: Long = 0L
)
@Dao
interface MoodDao {
    @Query("SELECT * FROM mood_table ORDER BY id DESC")
    fun getAllMoods(): Flow<List<MoodEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMood(mood: MoodEntry)

    @Delete
    suspend fun deleteMood(mood: MoodEntry)
}

@Dao
interface HabitDao {
    @Query("SELECT * FROM habit_table ORDER BY id ASC")
    fun getAllHabits(): Flow<List<Habit>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: Habit)

    @Update
    suspend fun updateHabit(habit: Habit)

    @Query("SELECT COUNT(*) FROM habit_table")
    suspend fun getCount(): Int
}

@Database(entities = [MoodEntry::class, Habit::class], version = 2, exportSchema = false)
abstract class MoodDatabase : RoomDatabase() {
    abstract fun moodDao(): MoodDao
    abstract fun habitDao(): HabitDao

    companion object {
        @Volatile
        private var INSTANCE: MoodDatabase? = null

        fun getDatabase(context: Context): MoodDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MoodDatabase::class.java,
                    "mood_tracker_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}