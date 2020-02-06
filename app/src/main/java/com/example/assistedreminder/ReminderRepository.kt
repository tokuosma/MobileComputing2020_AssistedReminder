package com.example.assistedreminder

import androidx.lifecycle.LiveData

class ReminderRepository(private val reminderDao: ReminderDao){

    val allReminders: LiveData<List<Reminder>> = reminderDao.getReminders()

    suspend fun insert(reminder:Reminder){
        reminderDao.insert(reminder)
    }
}