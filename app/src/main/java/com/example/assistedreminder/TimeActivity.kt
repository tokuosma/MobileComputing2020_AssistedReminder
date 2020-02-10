package com.example.assistedreminder

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_time.*
import org.jetbrains.anko.toast
import java.util.*

class TimeActivity : AppCompatActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time)


        timePicker.setIs24HourView(android.text.format.DateFormat.is24HourFormat(applicationContext))

        button_save.setOnClickListener{
            val calendar = GregorianCalendar(
                datePicker.year,
                datePicker.month,
                datePicker.dayOfMonth,
                timePicker.hour,
                timePicker.minute
            )

            if( et_message.text.toString() != "" &&
                    calendar.timeInMillis > System.currentTimeMillis()){

                val reminder = Reminder(
                    uid = null,
                    time = calendar.timeInMillis,
                    message = et_message.text.toString(),
                    location = null
                )

                setAlarm(reminder.time!!, reminder.message)

                val replyIntent = Intent()
                replyIntent.putExtra(EXTRA_REPLY, reminder)
                setResult(Activity.RESULT_OK, replyIntent)

                finish()
            } else{
                toast(getString(R.string.timeActivity_InvalidReminder))
            }


        }
    }

    companion object {
        const val EXTRA_REPLY = "com.example.assistedreminder.REPLY"
        const val EXTRA_ALARM_MESSAGE = "com.example.assistedreminder.EXTRA_ALARM_MESSAGE"
    }

    private fun setAlarm(time:Long, message:String){
        val intent = Intent(this, ReminderReceiver::class.java)
        intent.putExtra(EXTRA_ALARM_MESSAGE, message)

        val pendingIntent = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_ONE_SHOT )

        val manager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        manager.setExact(AlarmManager.RTC, time, pendingIntent)
    }
}
