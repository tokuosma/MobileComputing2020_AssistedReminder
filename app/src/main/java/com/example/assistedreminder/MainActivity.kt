package com.example.assistedreminder

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

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

        buttonToast.setOnClickListener{
            Toast.makeText(applicationContext, "A Fine Toast!" , Toast.LENGTH_SHORT)
                .show()
        }
        floatingActionButton_TimeActivity.setOnClickListener{
            startActivity(Intent(applicationContext, TimeActivity::class.java))
        }
        floatingActionButton_MapActivity.setOnClickListener{
            startActivity(Intent(applicationContext, MapActivity::class.java))
        }


    }


}
