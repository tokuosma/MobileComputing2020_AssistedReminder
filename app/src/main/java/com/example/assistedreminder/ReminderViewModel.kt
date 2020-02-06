package com.example.assistedreminder

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ReminderViewModel (application: Application) : AndroidViewModel(application){

    private val repository: ReminderRepository
    val allReminders: LiveData<List<Reminder>>

    init{
        val remindersDao = AppDatabase.getDatabase(application, viewModelScope).reminderDao()
        repository = ReminderRepository(remindersDao)
        allReminders = repository.allReminders
    }

    fun insert(reminder: Reminder) = viewModelScope.launch {
        repository.insert(reminder)
    }
}