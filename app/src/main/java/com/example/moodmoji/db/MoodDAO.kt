package com.example.moodmoji.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import kotlinx.coroutines.flow.Flow
import androidx.room.Query
import androidx.room.Update

@Dao
interface MoodDAO {
    @Insert()
    suspend fun insert(moodEntity: MoodEntity)

    @Query("SELECT * FROM mood_table WHERE date = :date LIMIT 1")
    fun getMoodByDate(date: String): Flow<MoodEntity?>

    @Query("SELECT * FROM mood_table ORDER BY date DESC")
    fun getAllMoods(): Flow<List<MoodEntity>>

    @Update()
    suspend fun update(moodEntity: MoodEntity)

    @Delete()
    suspend fun delete(moodEntity: MoodEntity)
}