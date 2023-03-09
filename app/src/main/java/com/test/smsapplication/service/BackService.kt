@file:Suppress("SENSELESS_COMPARISON")

package com.test.smsapplication.service

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.*
import android.telephony.SmsManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.test.smsapplication.adapters.DashAdapter
import com.test.smsapplication.models.DataClass
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BackService : Service() {

    /*private lateinit var handler: Handler
    var count = 0
    //mediaPlayer = MediaPlayer.create(this, R.raw.sound)
    private lateinit var mediaPlayer: MediaPlayer
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
    override fun onCreate() {
        super.onCreate()
        handler = Handler(Looper.getMainLooper())
        val preferences = getSharedPreferences("Counter", Context.MODE_PRIVATE)
        count = preferences.getInt("count", 0)
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        handler.post(object : Runnable {
            override fun run() {
                *//*if(count % 2 == 0) {
                    mediaPlayer = MediaPlayer.create(this@BackService, R.raw.sound)
                    mediaPlayer.start()
                }*//*
                count++
                println("Count: $count")
                val preferences = getSharedPreferences("Counter", Context.MODE_PRIVATE)
                preferences.edit().putInt("count", count).apply()
                handler.postDelayed(this, 2000)
            }
        })
        return START_STICKY
    }
    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }*/

    private val PERMISSION_SEND_SMS = 101
    private lateinit var handler: Handler
    private var count = 0
    private lateinit var wakeLock: PowerManager.WakeLock
    val phoneNumbers = ArrayList<String>()
    val message = ArrayList<String>()
    val smsId = ArrayList<Int>()
    var sharedPreferences: SharedPreferences? = null
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @SuppressLint("InvalidWakeLockTag", "WakelockTimeout")
    override fun onCreate() {
        super.onCreate()
        handler = Handler(Looper.getMainLooper())
        val preferences = getSharedPreferences("Counter", Context.MODE_PRIVATE)
        count = preferences.getInt("count", 0)

        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "CounterWakeLock")
        wakeLock.acquire()
        val permissionCheck =
            ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            sendSMS()
            //val smsManager: SmsManager = SmsManager.getDefault()
            //smsManager.sendTextMessage("+998995340313", null, "vaaa nihoyat", null, null)
        } else {
            ActivityCompat.requestPermissions(
                applicationContext as Activity, arrayOf(Manifest.permission.SEND_SMS),
                PERMISSION_SEND_SMS
            )
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        /*ApiClient.userService.data.enqueue(object : Callback<DataClass> {
            override fun onResponse(call: Call<DataClass>, response: Response<DataClass>) {
                if (response.isSuccessful) {
                    val data = response.body()
                    for (i in data?.data?.indices!!) {
                        println("Tel: ${data.data!![i].tel}")
                        phoneNumbers.add(data.data!![i].tel!!)
                        message.add(data.data!![i].zapros!!)
                        Toast.makeText(this@BackService, "Tel: ${data.data!![i].tel}", Toast.LENGTH_SHORT).show()
                    }
                    println("PhoneNumbers: $phoneNumbers")
                    println("Message: $message")
                } else {
                    println("Error ------ : ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<DataClass>, t: Throwable) {
                println("Errors => : ${t.message}")
            }
        })*/

        /*ApiClient.userService.updateStatus("2").enqueue(
            object : Callback<DataClass> {
                override fun onResponse(
                    call: Call<DataClass>,
                    response: Response<DataClass>
                ) {
                    if (response.isSuccessful) {
                        val data = response.body()
                        for (i in data?.data?.indices!!) {
                            println("Tel: ${data.data!![i].tel}")
                            phoneNumbers.add(data.data!![i].tel!!)
                            message.add(data.data!![i].zapros!!)
                            smsId.add(data.data!![i].id)
                            Toast.makeText(this@BackService, "Tel: ${data.data!![i].tel}", Toast.LENGTH_SHORT).show()
                        }
                        sendSMS()
                        println("PhoneNumbers: $phoneNumbers")
                        println("Message: $message")
                    } else {
                        println("Error ------ : ${response.errorBody()}")
                    }
                }

                override fun onFailure(call: Call<DataClass>, t: Throwable) {
                    println("Errors => : ${t.message}")
                }
            }
        )*/

        sharedPreferences = this.getSharedPreferences("ipAddress", 0)
        val data = sharedPreferences?.getString("ipAddress", "")
        var ipAdress = ""
        for (i in data?.split(",")?.indices!!) {
            if (data.split(",")[i].contains("$1")) {
                ipAdress = data.split(",")[i].replace("$1", "")
                println(ipAdress)
                break
            }else{
                ipAdress = data[0].toString().replace("$0", "")
            }
        }
        val queue = Volley.newRequestQueue(this)
        val url = "${ipAdress}sms/status?status=2"
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                println("Response is: $response")
                val gson = Gson()
                val dataClass = gson.fromJson(response, DataClass::class.java)
                for (i in dataClass.data?.indices!!) {
                    println("Tel: ${dataClass.data!![i].tel}")
                    phoneNumbers.add(dataClass.data!![i].tel!!)
                    message.add(dataClass.data!![i].zapros!!)
                    smsId.add(dataClass.data!![i].id)
                    Toast.makeText(this@BackService, "Tel: ${dataClass.data!![i].tel}", Toast.LENGTH_SHORT).show()
                }
                sendSMS()
                println("PhoneNumbers: $phoneNumbers")
                println("Message: $message")
            },
            { println("That didn't work!") })
        queue.add(stringRequest)

        /*handler.post(object : Runnable {
            override fun run() {
                count++
                println("Count: $count")
                val preferences = getSharedPreferences("Counter", Context.MODE_PRIVATE)
                preferences.edit().putInt("count", count).apply()
                handler.postDelayed(this, 2000)
            }
        })*/
        return START_STICKY
    }
    private fun updateSmsStatus(){
        /*ApiClient.userService.updateSmsStatus(smsId).enqueue(
            object : Callback<Any> {
                override fun onResponse(
                    call: Call<Any>,
                    response: Response<Any>
                ) {
                    if (response.isSuccessful) {
                        val data = response.body()
                        if (data == true) {
                            println("Success")
                            Toast.makeText(this@BackService, "Sms Jo`natildi", Toast.LENGTH_SHORT).show()
                        } else if (data == false) {
                            println("Error")
                            Toast.makeText(this@BackService, "Qandaydur Xatolik", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@BackService, " Serverda qandaydur Xatolik", Toast.LENGTH_SHORT).show()
                        println("Error ------ : ${response.errorBody()}")
                    }
                }

                override fun onFailure(call: Call<Any>, t: Throwable) {
                    println("Errors => : ${t.message}")
                }
            }
        )*/

    }
    @SuppressLint("ObsoleteSdkInt")
    private fun sendSMS() {
        val smsManager = SmsManager.getDefault()
        for (phoneNumber in phoneNumbers) {
            var sendded: Boolean = false
            //smsManager.sendTextMessage(phoneNumber, null, message[phoneNumbers.indexOf(phoneNumber)], null, null)
            sendded = smsManager.sendTextMessage(phoneNumber, null, message[phoneNumbers.indexOf(phoneNumber)], null, null) == null
            if (sendded) {
                Toast.makeText(this, "Sms Jo`natildi", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Sms Jo`natilmadi xatolik", Toast.LENGTH_SHORT).show()
            }
        }
        updateSmsStatus()
    }
    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        wakeLock.release()
    }
}