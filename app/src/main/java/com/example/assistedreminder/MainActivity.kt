package com.example.assistedreminder

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Double.parseDouble
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    lateinit var reminderViewModel: ReminderViewModel
    private lateinit var geofencingClient: GeofencingClient
    private val newTimeReminderActivityRequestCode = 1
    private val newLocationReminderActivityRequestCode = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        geofencingClient = LocationServices.getGeofencingClient(this)

        var fabOpened = false

        floatingActionButton_ShowActivity.setOnClickListener {
            if (!fabOpened) {
                fabOpened = true
                floatingActionButton_TimeActivity.animate()
                    .translationY(-resources.getDimension(R.dimen.standard_66))
                floatingActionButton_MapActivity.animate()
                    .translationY(-resources.getDimension(R.dimen.standard_116))
            } else {
                fabOpened = false
                floatingActionButton_TimeActivity.animate().translationY(0f)
                floatingActionButton_MapActivity.animate().translationY(0f)
            }
        }

        floatingActionButton_TimeActivity.setOnClickListener {
            startActivityForResult(
                Intent(applicationContext, TimeActivity::class.java),
                newTimeReminderActivityRequestCode
            )
        }
        floatingActionButton_MapActivity.setOnClickListener {
            startActivityForResult(
                Intent(applicationContext, MapActivity::class.java),
                newLocationReminderActivityRequestCode
            )
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = ReminderViewAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)


        reminderViewModel = ViewModelProviders.of(this).get(ReminderViewModel::class.java)
        reminderViewModel.allReminders.observe(this, Observer { reminders ->
            reminders?.let { adapter.setReminders(it) }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == newTimeReminderActivityRequestCode && resultCode == Activity.RESULT_OK) {
            data?.getParcelableExtra<Reminder>(EXTRA_REPLY)?.let {
                val reminder = it
                reminderViewModel.insert(reminder)
                setAlarm(reminder)
            }


        } else if (requestCode == newLocationReminderActivityRequestCode && resultCode == Activity.RESULT_OK) {
            data?.getParcelableExtra<Reminder>(EXTRA_REPLY)?.let {
                val reminder = it

                if (reminder.location != null) {
                    val coordinates = reminder.location!!.split(',')
                    val location = LatLng(parseDouble(coordinates[0]), parseDouble(coordinates[1]))
                    reminderViewModel.insert(reminder)
                    createGeofence(location, reminder, geofencingClient)
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Location could not be determined. Reminder was not added.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        } else {
            Toast.makeText(
                applicationContext,
                "Reminder was not added.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun deleteReminder(reminder: Reminder){
        reminderViewModel.delete(reminder)
    }

    private fun setAlarm(reminder:Reminder) {
        val intent = Intent(this, ReminderReceiver::class.java)
        intent.putExtra(EXTRA_ALARM_MESSAGE, reminder.message)
        intent.putExtra(EXTRA_REMINDER, reminder)
        val pendingIntent = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_ONE_SHOT)
        val manager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        reminder.time?.let { manager.setExact(AlarmManager.RTC, it, pendingIntent) }
    }

    private fun createGeofence(
        selectedLocation: LatLng,
        reminder: Reminder,
        geofencingClient: GeofencingClient
    ) {
        val geofence = Geofence.Builder()
            .setRequestId(GEOFENCE_ID)
            .setCircularRegion(
                selectedLocation.latitude,
                selectedLocation.longitude,
                GEOFENCE_RADIUS.toFloat()
            )
            .setExpirationDuration(GEOFENCE_EXPIRATION)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_DWELL)
            .setLoiteringDelay(GEOFENCE_DWELL_DELAY)

        val geofenceRequest = GeofencingRequest.Builder()
            .setInitialTrigger(Geofence.GEOFENCE_TRANSITION_ENTER)
            .addGeofence(geofence.build())

        val intent = Intent(this, GeofenceReceiver::class.java)
            .putExtra(EXTRA_REMINDER, reminder)

        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        geofencingClient.addGeofences(geofenceRequest.build(), pendingIntent)
    }


    companion object {
        const val EXTRA_REPLY = "com.example.assistedreminder.REPLY"
        const val EXTRA_REMINDER = "com.example.assistedreminder.REMINDER"
        const val EXTRA_ALARM_MESSAGE = "com.example.assistedreminder.EXTRA_ALARM_MESSAGE"
        const val CHANNEL_ID = "REMINDER_NOTIFICATION_CHANNEL"
        const val GEOFENCE_ID = "REMINDER_GEOFENCE_ID"
        const val GEOFENCE_RADIUS = 500 //500 meters
        const val GEOFENCE_EXPIRATION = 120 * 24 * 60 * 60 * 1000.toLong()
        const val GEOFENCE_DWELL_DELAY = 2 * 60 * 1000

        fun showNotification(context: Context, message: String) {
            var notificationId = 1589
            notificationId += Random(notificationId).nextInt(1, 30)

            var notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_toys_24px)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    context.getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = context.getString(R.string.app_name)
                }
                notificationManager.createNotificationChannel(channel)
            }
            notificationManager.notify(notificationId, notificationBuilder.build())


        }

    }


}

