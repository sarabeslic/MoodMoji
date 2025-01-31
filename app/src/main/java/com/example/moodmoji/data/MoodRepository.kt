package com.example.moodmoji.data

import com.example.moodmoji.db.MoodDAO
import com.example.moodmoji.db.MoodEntity
import kotlinx.coroutines.flow.Flow

class MoodRepository (private val moodDao: MoodDAO) {

    val readAllData: Flow<List<MoodEntity>> = moodDao.getAllMoods()

    suspend fun  insert(mood: MoodEntity) {
        moodDao.insert(mood)
    }
    suspend fun update(mood: MoodEntity) {
        moodDao.update(mood)
    }
    suspend fun delete(mood: MoodEntity) {
        moodDao.delete(mood)
    }
    fun getMoodByDate(date: String): Flow<MoodEntity?> {
        return moodDao.getMoodByDate(date)
    }

}
