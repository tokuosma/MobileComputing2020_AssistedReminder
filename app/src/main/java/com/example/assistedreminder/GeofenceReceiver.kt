package com.example.assistedreminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent

class GeofenceReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        val geofencingTransition = geofencingEvent.geofenceTransition

        if (geofencingTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
            geofencingTransition == Geofence.GEOFENCE_TRANSITION_DWELL
        ) {
            val reminder = intent!!.getParcelableExtra<Reminder>(MainActivity.EXTRA_REMINDER)

            MainActivity.showNotification(context!!, message = reminder.message)

//            doAsync{
//                val db = AppDatabase.getDatabase(context)
//                db.reminderDao().deleteSync(reminder)
//            }
        }
    }

}