package com.example.assistedreminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.assistedreminder.TimeActivity.Companion.EXTRA_ALARM_MESSAGE
import org.jetbrains.anko.toast

class ReminderReceiver :BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val message = intent.getStringExtra(EXTRA_ALARM_MESSAGE)  ?: "IAMERROR"
        context.toast(message)
    }


}