@file:Suppress("SENSELESS_COMPARISON")

package com.test.smsapplication.service

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
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
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.test.smsapplication.models.DataClass

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
        val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
        /*if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            sendSMS()
        } else {
           ActivityCompat.requestPermissions(applicationContext as Activity, arrayOf(Manifest.permission.SEND_SMS), PERMISSION_SEND_SMS)
        }*/
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        sharedPreferences = this.getSharedPreferences("ipAddress", 0)
        val data = sharedPreferences?.getString("ipAddress", "")
        var ipAdress = ""
        for (i in data?.split(",")?.indices!!) {
            if (data.split(",")[i].contains("$1")) {
                ipAdress = data.split(",")[i].replace("$1", "")
                println(ipAdress)
                break
            } else {
                ipAdress = data[0].toString().replace("$0", "")
            }
        }
        val queue = Volley.newRequestQueue(this)
        val url = "https://${ipAdress}sms/status?status=2"
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
                    Toast.makeText(
                        this@BackService,
                        "Tel: ${dataClass.data!![i].tel}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                val getPref = sharedPreferences?.getString("smsHistory", "")
                println("smsHistory: $getPref")
                //{"message":"Here!!!","success":true,"status":200,"data":[]}
                //if getPref == null save response to sharedPref else get getPref save response.to json in data
                var json = ""

                if (getPref == null||getPref == ""||getPref == " ") {
                    json = response
                } else {
                    val gson = Gson()
                    val dataPerf = gson.fromJson(getPref, DataClass::class.java)
                    val getRes = gson.fromJson(response, DataClass::class.java)
                    val jsonArray = JsonArray()
                    for (i in dataPerf.data?.indices!!) {
                        jsonArray.add(gson.toJsonTree(dataPerf.data!![i]))
                    }
                    for (i in getRes.data?.indices!!) {
                        jsonArray.add(gson.toJsonTree(getRes.data!![i]))
                    }

                    val jsonObject = JsonObject()
                    jsonObject.add("data", jsonArray)
                    json = jsonObject.toString()
                    println("json: $json")
                }
                //sendSMS()
                println("PhoneNumbers: $phoneNumbers")
                println("Message: $message")

                //save to sharedPref
                val editor = sharedPreferences?.edit()
                editor?.putString("smsHistory", json)
                editor?.apply()
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

    private fun updateSmsStatus() {
        sharedPreferences = this.getSharedPreferences("ipAddress", 0)
        val data = sharedPreferences?.getString("ipAddress", "")
        var ipAdress = ""
        for (i in data?.split(",")?.indices!!) {
            if (data.split(",")[i].contains("$1")) {
                ipAdress = data.split(",")[i].replace("$1", "")
                println(ipAdress)
                break
            } else {
                ipAdress = data[0].toString().replace("$0", "")
            }
        }
        val queue = Volley.newRequestQueue(this)
        val url = "https://${ipAdress}sms"
        val stringRequest = object : StringRequest(
            Method.PUT, url,
            { response ->
                println("Response is: $response")
            },
            { println("That didn't work!") }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                val gson = Gson()
                val json = gson.toJson(smsId)
                return json.toByteArray()
            }
        }

        queue.add(stringRequest)

    }

    @SuppressLint("ObsoleteSdkInt")
    private fun sendSMS() {
        val smsManager = SmsManager.getDefault()
        for (phoneNumber in phoneNumbers) {
            val sendded: Boolean = smsManager.sendTextMessage(
                phoneNumber,
                null,
                message[phoneNumbers.indexOf(phoneNumber)],
                null,
                null
            ) == null
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