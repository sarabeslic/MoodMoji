package com.example.moodmoji.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mood_table")

data class MoodEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int=0,
    val emojiId: Int,
    val date: String,
    val dayDescription: String
)