package com.example.assistedreminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.assistedreminder.MainActivity.Companion.EXTRA_ALARM_MESSAGE
import org.jetbrains.anko.toast

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val reminderMessage = intent.getStringExtra(EXTRA_ALARM_MESSAGE)
        context.toast(reminderMessage)
        MainActivity.showNotification(context, reminderMessage)

    }


}