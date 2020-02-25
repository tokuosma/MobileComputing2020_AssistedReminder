package com.example.assistedreminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.assistedreminder.MainActivity.Companion.EXTRA_REMINDER
import org.jetbrains.anko.toast

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        val reminder = intent.getParcelableExtra<Reminder>(EXTRA_REMINDER)
        context.toast(reminder.message)
        MainActivity.showNotification(context, reminder.message)

//        doAsync{
//            val db = AppDatabase.getDatabase(context)
//            db.reminderDao().deleteSync(reminder)
//        }

    }


}