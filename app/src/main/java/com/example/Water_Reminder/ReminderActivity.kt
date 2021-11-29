package com.example.Water_Reminder

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.google.android.material.textfield.TextInputEditText
import java.lang.String
import java.text.SimpleDateFormat
import java.util.*

class ReminderActivity : AppCompatActivity() {

    private lateinit var slTimeCal: Calendar
    private lateinit var wkTimeCal: Calendar

   @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createNotificationChannel()


        val etWeight = findViewById<TextInputEditText>(R.id.et_weight)
        val calculate = findViewById<Button>(R.id.calculate)
        val tvDisplay = findViewById<TextView>(R.id.tv_display)
        val wktime = findViewById<Button>(R.id.btn_wktime)
        val sltime = findViewById<Button>(R.id.btn_sltime)

        wktime.setOnClickListener {
            val mcurrentTime: Calendar = Calendar.getInstance()
            val hour: Int = mcurrentTime.get(Calendar.HOUR_OF_DAY)
            val minute: Int = mcurrentTime.get(Calendar.MINUTE)
            val mTimePicker = TimePickerDialog(
                this,
                { _, selectedHour, selectedMinute ->
                    wkTimeCal = Calendar.getInstance()
                    val year: Int = wkTimeCal.get(Calendar.YEAR)
                    val month: Int = wkTimeCal.get(Calendar.MONTH)
                    val day: Int = wkTimeCal.get(Calendar.DATE)
                    wkTimeCal.set(year, month, day, selectedHour, selectedMinute, 0)
                    val time = SimpleDateFormat("hh:mm a", Locale.US).format(wkTimeCal.time)
                    wktime.text = time

                },
                hour,
                minute,
                false
            )
            mTimePicker.setTitle("Wakeup Time")
            mTimePicker.show()
        }

        sltime.setOnClickListener {
            val mcurrentTime: Calendar = Calendar.getInstance()
            val hour: Int = mcurrentTime.get(Calendar.HOUR_OF_DAY)
            val minute: Int = mcurrentTime.get(Calendar.MINUTE)
            val mTimePicker = TimePickerDialog(
                this,
                { _, selectedHour, selectedMinute ->
                    slTimeCal = Calendar.getInstance()
                    val year: Int = slTimeCal.get(Calendar.YEAR)
                    val month: Int = slTimeCal.get(Calendar.MONTH)
                    val day: Int = slTimeCal.get(Calendar.DATE)
                    slTimeCal.set(year, month, day, selectedHour, selectedMinute, 0)
                    val time = SimpleDateFormat("hh:mm a", Locale.US).format(slTimeCal.time)
                    sltime.text = time

                },
                hour,
                minute,
                false
            )
            mTimePicker.setTitle("Sleep Time")
            mTimePicker.show()
        }

        calculate.setOnClickListener {
            val weight = etWeight.text.toString().toInt()

            val diff: Long =  slTimeCal.timeInMillis - wkTimeCal.timeInMillis
            val seconds = diff / 1000
            val minutes = seconds / 60
            val hours = minutes / 60

            val amountOfWater = weight * 0.03
            val water = amountOfWater / hours

            val intent = Intent(application, NotificationReceiver::class.java)

            val pendingIntent = PendingIntent.getBroadcast(
                application,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

            val alarmManager =
                application.getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager


            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                1000 * 60 * 60 * 60, pendingIntent
            )


            tvDisplay.visibility = View.VISIBLE
            val str = String.format("%.02f", amountOfWater)
            val str1 = String.format("%.02f", water * 1000)
            tvDisplay.text =
                "You Should Drink A Total of $str L Per Day \nYou will be required to drink $str1 ml every Hour."


        }

    }

    private fun createNotificationChannel() {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val description = "Channel for sending notes notification"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("channelId", "channelName", importance)
            channel.description = description
            channel.vibrationPattern = longArrayOf(0, 1000, 500, 1000)
            channel.enableVibration(true)
            notificationManager.createNotificationChannel(channel)
        }
    }
}
