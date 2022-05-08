package com.ashish.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.ashish.notification.FirebaseService.Companion.token
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


const val TOPIC = "/topics/myTopic2"

class MainActivity : AppCompatActivity() {
     lateinit var btnSend: Button
     lateinit var titleEt:EditText
     lateinit var messageEt:EditText
     lateinit var tokenEt:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnSend=findViewById(R.id.btnSend)
        titleEt=findViewById(R.id.etTitle)
        messageEt=findViewById(R.id.etMessage)

        FirebaseService.sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
//        FirebaseMessaging.getInstance().token.addOnSuccessListener {
//            FirebaseService.token=it.toString()
//        }
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)

        btnSend.setOnClickListener {
            val title=titleEt.text.toString()
            val message=messageEt.text.toString()
            if(title.isNotEmpty() && message.isNotEmpty()) {
                PushNotification(
                    NotificationData(title, message),
                    TOPIC
                ).also {
                    sendNotification(it)
                }
            }
        }

    }

    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
            if(response.isSuccessful) {
                Log.d(TAG, "Response: ${Gson().toJson(response)}")
            } else {
                Log.e(TAG, response.errorBody().toString())
            }
        } catch(e: Exception) {
            Log.e(TAG, e.toString())
        }
    }
}