package com.example.assistedreminder

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_time.*
import java.util.*

class TimeActivity : AppCompatActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time)

        button_save.setOnClickListener{
            val calendar = GregorianCalendar(
                datePicker.year,
                datePicker.month,
                datePicker.dayOfMonth,
                timePicker.hour,
                timePicker.minute
            )

            val reminder = Reminder(
                uid = null,
                time = calendar.timeInMillis,
                message = et_message.text.toString(),
                location = null
            )

            val replyIntent = Intent()
            if(TextUtils.isEmpty(reminder.message)){
                setResult(Activity.RESULT_CANCELED, replyIntent)
            }
            else{
                replyIntent.putExtra(EXTRA_REPLY, reminder)
                setResult(Activity.RESULT_OK, replyIntent)
            }
            finish()
        }
    }

    companion object {
        const val EXTRA_REPLY = "com.example.assistedreminder.REPLY"
    }
}
