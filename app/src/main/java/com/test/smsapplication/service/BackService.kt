@file:Suppress("SENSELESS_COMPARISON")

package com.test.smsapplication.service

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Service
import android.content.*
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.*
import android.telephony.SmsManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.test.smsapplication.R
import com.test.smsapplication.models.DataClass
import java.util.*


class BackService : Service() {

    private lateinit var handler: Handler
    private var count = 0
    private lateinit var wakeLock: PowerManager.WakeLock
    private val phoneNumbers = ArrayList<String>()
    private val message = ArrayList<String>()
    val smsId = ArrayList<Int>()
    private var sharedPreferences: SharedPreferences? = null
    private var mediaPlayer: MediaPlayer? = null

    var totalElements = 0
    var totalPages = 0
    var size = 150
    var page = 0
    var ipLink = ""
    var phone = ""
    var url = ""
    var messages = ""
    private var vibrator: Vibrator? = null
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
    }

    @SuppressLint("InvalidWakeLockTag", "WakelockTimeout")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "CounterWakeLock")
        wakeLock.acquire()
        sharedPreferences = this.getSharedPreferences("ipAddress", 0)
        val data = sharedPreferences?.getString("ipAddress", "")
        for (i in data?.split(",")?.indices!!) {
            if (data.split(",")[i].contains("$1")) {
                ipLink = data.split(",")[i].replace("$1", "")
                break
            } else {
                ipLink = data.split(",")[0].replace("$0", "")
            }
        }
        phone = sharedPreferences?.getString("phone", "").toString()
        if (phone.isEmpty() || phone == " ") {
            Toast.makeText(this, "Telefon raqamni kiriting!", Toast.LENGTH_SHORT).show()
            return START_NOT_STICKY
        }
        val smsLimit = sharedPreferences?.getString("smsLimit", "")
        if (smsLimit == "0" || smsLimit == " " || smsLimit == null) {
            Toast.makeText(this, "SMS limiti kiriting!", Toast.LENGTH_SHORT).show()
        }
        messages = intent?.getStringExtra("message").toString()
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.SEND_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                applicationContext as Activity,
                arrayOf(Manifest.permission.SEND_SMS),
                1
            )
        } else {
            url =
                "${ipLink}api/sms/status?page=$page&size=$size&employeePhone=%2B998$phone&status=2"
            getResponse(url)
        }
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

    @SuppressLint("SuspiciousIndentation")
    private fun getResponse(url: String) {
        phoneNumbers.clear()
        message.clear()
        smsId.clear()
        sharedPreferences = this.getSharedPreferences("ipAddress", 0)
        var smsLimit = sharedPreferences?.getString("smsLimit", "")
        if (smsLimit != null || smsLimit != " " || smsLimit != "0") {
            val queue = Volley.newRequestQueue(this)
            val stringRequest = StringRequest(
                Request.Method.GET, url,
                { response ->
                    val gson = Gson()
                    val dataClass = gson.fromJson(response, DataClass::class.java)

                    println("messages: $messages")
                    if (messages == "1") {
                        for (i in dataClass.data?.content?.indices!!) {
                            if (smsLimit != "0") {
                                phoneNumbers.add(dataClass.data?.content!![i].tel!!)
                                message.add(dataClass.data?.content!![i].zapros!!)
                                smsId.add(dataClass.data?.content!![i].id!!)
                                smsLimit = (smsLimit!!.toInt() - 1).toString()
                                if (smsLimit == "0") break
                            } else {
                                break
                            }
                        }
                    }else{
                        for (i in dataClass.data?.content?.indices!!) {
                            if (smsLimit != "0") {
                                //if rezult = chek, pay, qarz
                                if (dataClass.data?.content!![i].rezult == "chek" || dataClass.data?.content!![i].rezult == "pay" || dataClass.data?.content!![i].rezult == "qarz") {
                                    phoneNumbers.add(dataClass.data?.content!![i].tel!!)
                                    message.add(dataClass.data?.content!![i].zapros!!)
                                    smsId.add(dataClass.data?.content!![i].id!!)
                                    smsLimit = (smsLimit!!.toInt() - 1).toString()
                                    if (smsLimit == "0") break
                                }
                            } else {
                                break
                            }
                        }
                    }

                    val getRes = gson.fromJson(response, DataClass::class.java)
                    totalElements = getRes.data?.numberOfElements!!
                    totalPages = getRes.data?.totalPages!!
                    println("totalPages: $totalPages")
                    size = getRes.data?.size!!
                    page = getRes.data?.number!!
                    val editors = sharedPreferences?.edit()
                    editors?.putString("smsLimit", smsLimit)
                    editors?.apply()
                    if (phoneNumbers.isNotEmpty() || message.isNotEmpty() || smsId.isNotEmpty())
                        sendSMS()
                },
                { println("That didn't work!") })
            queue.add(stringRequest)
        } else {
            println("SMS limitini kiriting!")
            Toast.makeText(this, "SMS limitini kiriting!", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun sendSMS() {
        sharedPreferences = this.getSharedPreferences("ipAddress", 0)
        val smsManager = SmsManager.getDefault()
        for (i in message) {
            println("Message: $i")
            println("Phone: ${phoneNumbers[message.indexOf(i)]}")
            if (i.isNotEmpty() || i != " " || i != null) {
                Handler(Looper.getMainLooper()).postDelayed({
                    if (i.length > 140) {
                        val parts = smsManager.divideMessage(i)
                        Toast.makeText(this, "Message: $parts", Toast.LENGTH_SHORT).show()
                        smsManager.sendMultipartTextMessage(
                            phoneNumbers[message.indexOf(i)],
                            null,
                            parts,
                            null,
                            null
                        )
                        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            vibrator!!.vibrate(
                                VibrationEffect.createOneShot(
                                    100,
                                    VibrationEffect.DEFAULT_AMPLITUDE
                                )
                            )
                        } else {
                            vibrator!!.vibrate(100)
                        }
                    } else {
                        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            vibrator!!.vibrate(
                                VibrationEffect.createOneShot(
                                    100,
                                    VibrationEffect.DEFAULT_AMPLITUDE
                                )
                            )
                        } else {
                            vibrator!!.vibrate(100)
                        }
                        smsManager.sendTextMessage(
                            phoneNumbers[message.indexOf(i)],
                            null, i,
                            null,
                            null
                        )
                    }
                }, 3000)
            } else {
                Toast.makeText(this, "Message is empty!", Toast.LENGTH_SHORT).show()
                println("Message is empty!")
                break
            }
        }

        updateSmsStatus(phoneNumbers, message, smsId)
    }

    private fun updateSmsStatus(
        phoneNumbers: ArrayList<String>,
        message: ArrayList<String>,
        smsId: ArrayList<Int>
    ) {
        sharedPreferences = this.getSharedPreferences("ipAddress", 0)
        val queue = Volley.newRequestQueue(this)
        val url = "${ipLink}api/sms"
        val stringRequest = object : StringRequest(
            Method.PUT, url,
            { response ->
                //println("Response is: $response")
                saveSendSms(phoneNumbers, message, smsId)
            },
            {
                println("That didn't work!")
                saveSendSmsErr(phoneNumbers, message, smsId)
            }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                val gson = Gson()
                val json = gson.toJson(smsId)
                return json.toByteArray()
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json"
                headers["Authorization"] = "Bearer 5927728152:AAExhfEpagD__0D9A6b_qJs56SuXV06oZ-8"
                return headers
            }
        }
        queue.add(stringRequest)

    }
    private fun saveSendSms(
        phoneNumbers: ArrayList<String>,
        message: ArrayList<String>,
        smsId: ArrayList<Int>
    ) {
        for (i in phoneNumbers) {
            val getMassage = sharedPreferences?.getString("smsHistory", "")
            if (getMassage == null) {
                val editor = sharedPreferences?.edit()
                editor?.putString("smsHistory", message[phoneNumbers.indexOf(i)])
                editor?.apply()
            } else {
                val editor = sharedPreferences?.edit()
                editor?.putString("smsHistory", "$getMassage,${message[phoneNumbers.indexOf(i)]}")
                editor?.apply()
            }
            val getPhone = sharedPreferences?.getString("phoneHistory", "")
            if (getPhone == null) {
                val editor2 = sharedPreferences?.edit()
                editor2?.putString("phoneHistory", phoneNumbers[phoneNumbers.indexOf(i)])
                editor2?.apply()
            } else {
                val editor2 = sharedPreferences?.edit()
                editor2?.putString(
                    "phoneHistory",
                    "$getPhone,${phoneNumbers[phoneNumbers.indexOf(i)]}"
                )
                editor2?.apply()
            }
        }
        mediaPlayer = MediaPlayer.create(this, R.raw.sound)
        mediaPlayer?.start()
    }

    private fun saveSendSmsErr(
        phoneNumbers: ArrayList<String>,
        message: ArrayList<String>,
        smsId: ArrayList<Int>
    ) {
        for (i in phoneNumbers) {
            val getMassage = sharedPreferences?.getString("smsErrHistory", "")
            if (getMassage == null) {
                val editor = sharedPreferences?.edit()
                editor?.putString("smsErrHistory", message[phoneNumbers.indexOf(i)])
                editor?.apply()
            } else {
                val editor = sharedPreferences?.edit()
                editor?.putString(
                    "smsErrHistory",
                    "$getMassage,${message[phoneNumbers.indexOf(i)]}"
                )
                editor?.apply()
            }
            val getPhone = sharedPreferences?.getString("phoneErrHistory", "")
            if (getPhone == null) {
                val editor2 = sharedPreferences?.edit()
                editor2?.putString("phoneErrHistory", phoneNumbers[phoneNumbers.indexOf(i)])
                editor2?.apply()
            } else {
                val editor2 = sharedPreferences?.edit()
                editor2?.putString(
                    "phoneErrHistory",
                    "$getPhone,${phoneNumbers[phoneNumbers.indexOf(i)]}"
                )
                editor2?.apply()
            }
            val getErrSmsId = sharedPreferences?.getString("errSmsId", "")
            if (getErrSmsId == null) {
                val editor3 = sharedPreferences?.edit()
                editor3?.putString("errSmsId", smsId[phoneNumbers.indexOf(i)].toString())
                editor3?.apply()
            } else {
                val editor3 = sharedPreferences?.edit()
                editor3?.putString("errSmsId", "$getErrSmsId,${smsId[phoneNumbers.indexOf(i)]}")
                editor3?.apply()
            }
        }
        mediaPlayer = MediaPlayer.create(this, R.raw.sound)
        mediaPlayer?.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        wakeLock.release()
    }
}