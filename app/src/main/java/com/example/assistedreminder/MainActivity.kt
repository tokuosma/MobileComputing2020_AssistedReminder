package com.example.assistedreminder

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var reminderViewModel: ReminderViewModel
    private val newReminderActivityRequestCode = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var fabOpened = false

        floatingActionButton_ShowActivity.setOnClickListener{
            if(!fabOpened){
                fabOpened = true
                floatingActionButton_TimeActivity.animate().translationY(-resources.getDimension(R.dimen.standard_66))
                floatingActionButton_MapActivity.animate().translationY(-resources.getDimension(R.dimen.standard_116))
            }else{
                fabOpened = false
                floatingActionButton_TimeActivity.animate().translationY(0f)
                floatingActionButton_MapActivity.animate().translationY(0f)
            }
        }

        floatingActionButton_TimeActivity.setOnClickListener{
            startActivityForResult(Intent(applicationContext, TimeActivity::class.java), newReminderActivityRequestCode)
        }
        floatingActionButton_MapActivity.setOnClickListener{
            startActivity(Intent(applicationContext, MapActivity::class.java))
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = ReminderViewAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)


        reminderViewModel = ViewModelProviders.of(this).get(ReminderViewModel::class.java)
        reminderViewModel.allReminders.observe(this, Observer{ reminders ->
            reminders?.let{adapter.setReminders(it) }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == newReminderActivityRequestCode && resultCode == Activity.RESULT_OK){
            data?.getParcelableExtra<Reminder>(TimeActivity.EXTRA_REPLY)?.let {
                val reminder = it
                reminderViewModel.insert(reminder)
            }
        } else{
            Toast.makeText(applicationContext,
                "Reminder was not added.",
                Toast.LENGTH_LONG).show()
        }
    }


}
