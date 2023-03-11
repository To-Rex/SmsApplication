package com.test.smsapplication.ui.home
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.SmsManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.test.smsapplication.R
import com.test.smsapplication.adapters.DashAdapter
import com.test.smsapplication.models.DataClass
import com.test.smsapplication.service.BackService

class HomeFragment : Fragment() {
    private var phoneList = ArrayList<String>()
    private var messageList = ArrayList<String>()
    private var adapter: DashAdapter? = null
    private var homeList: ListView? = null
    private var btnHomeNewSms: Button? = null
    private var btnHomSendSms: Button? = null
    private var txtHomipAdress: TextView? = null
    private var sharedPreferences: SharedPreferences? = null
    @SuppressLint("MissingInflatedId", "ObsoleteSdkInt", "ServiceCast")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        homeList = view.findViewById(R.id.homeList)
        btnHomeNewSms = view.findViewById(R.id.btnHomNewSms)
        btnHomSendSms = view.findViewById(R.id.btnHomSendSms)
        txtHomipAdress = view.findViewById(R.id.txtHomipAdress)
        //activity?.startService(Intent(activity, BackService::class.java))
        btnHomeNewSms!!.setOnClickListener {
            getData()
        }
        btnHomSendSms!!.setOnClickListener {
            if (phoneList.size == 0) {
                Toast.makeText(activity, "SMS yuborish uchun telefon raqam va xabar kiriting", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            activity?.startService(Intent(activity, BackService::class.java))
        }
        if (context?.let { ContextCompat.checkSelfPermission(it, Manifest.permission.SEND_SMS) } != PackageManager.PERMISSION_GRANTED) {
            val alertDialog = android.app.AlertDialog.Builder(context)
            alertDialog.setTitle("Ruxsat")
            alertDialog.setMessage("Ushbu ilovaga Sozlamalar->Izohlar->SMS yuborishga ruxsat berish kerak")
            alertDialog.setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            alertDialog.show()
            ActivityCompat.requestPermissions(context as Activity, arrayOf(Manifest.permission.SEND_SMS), 1)
        }
        getData()
        return view
    }
    private fun getData(){
        homeList!!.adapter = null
        phoneList.clear()
        messageList.clear()
        sharedPreferences = activity?.getSharedPreferences("ipAddress", 0)
        val data = sharedPreferences?.getString("ipAddress", "")
        var ipAddress = ""
        if (data == null||data== ""||data==" ") {
            Toast.makeText(activity, "iltimos IP manzilni kiriting", Toast.LENGTH_SHORT).show()
            return
        }
        println(data.toString())
        Toast.makeText(activity, data.toString(), Toast.LENGTH_SHORT).show()
        for (i in data.split(",").indices) {
            println(data[i].toString())
            if (data.split(",")[i].contains("$1")) {
                ipAddress = data.split(",")[i].split("$1")[0]
                break
            }else{
                ipAddress = data.split(",")[0].replace("$0","")
            }
        }
        txtHomipAdress!!.text = ipAddress
        val queue = Volley.newRequestQueue(activity)
        println(ipAddress)
        val url = "https://${ipAddress}sms/status?status=2"
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                println("Response is: $response")
                val gson = Gson()
                val dataClass = gson.fromJson(response, DataClass::class.java)
                //var counter = 0
                for (i in dataClass.data?.indices!!) {
                    phoneList.add(dataClass.data!![i].tel!!)
                    messageList.add(dataClass.data!![i].zapros!!)
                    //counter++
                }
                adapter = DashAdapter(activity!!, phoneList, messageList)
                homeList!!.adapter = adapter
            },
            { println("That didn't work!") })
        queue.add(stringRequest)
    }
}