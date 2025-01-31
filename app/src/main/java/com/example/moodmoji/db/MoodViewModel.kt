package com.example.moodmoji.db

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class MoodViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = MoodDatabase.getDatabase(application).MoodDAO()
    val allMoods: Flow<List<MoodEntity>> = dao.getAllMoods()

    fun addMood(mood: MoodEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.insert(mood)
        }
    }

    fun removeMood(mood: MoodEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.delete(mood)
        }
    }

    fun updateMood(mood: MoodEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.update(mood)
        }
    }
    fun getMoodByDate(date: String): Flow<MoodEntity?> {
        return dao.getMoodByDate(date)
    }

}


