package com.test.smsapplication.ui.notifications

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.test.smsapplication.R
import com.test.smsapplication.adapters.DashAdapter
import com.test.smsapplication.models.DataClass

class NotificationsFragment : Fragment() {
    private var sharedPreferences: SharedPreferences? = null
    private var listNatVIew: ListView? = null
    private var btnNatUpdate: Button? = null
    private var btnNatClear: Button? = null
    private var txtSetIpAdress: TextView? = null

    private var phoneList = ArrayList<String>()
    private var messageList = ArrayList<String>()
    private var adapter: DashAdapter? = null
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notifications, container, false)
        sharedPreferences = activity?.getSharedPreferences("ipAddress", 0)
        val getPref = sharedPreferences?.getString("smsHistory", "")
        println("smsHistory: $getPref")
        btnNatClear = view.findViewById(R.id.btnNatClear)
        btnNatUpdate = view.findViewById(R.id.btnNatUpdate)
        listNatVIew = view.findViewById(R.id.listNatVIew)
        txtSetIpAdress = view.findViewById(R.id.txtSetIpAdress)

        getData()
        btnNatClear!!.setOnClickListener {
            sharedPreferences = activity?.getSharedPreferences("ipAddress", 0)
            val editor = sharedPreferences?.edit()
            editor?.putString("smsHistory", "")
            editor?.apply()
            getData()
        }
        btnNatUpdate!!.setOnClickListener {
            getData()
        }
        return view
    }
    private fun getData(){
        listNatVIew!!.adapter = null
        phoneList.clear()
        messageList.clear()
        val data = sharedPreferences?.getString("ipAddress", "")
        var ipAddress = ""
        if (data == null||data== ""||data==" ") {
            Toast.makeText(activity, "Bunday Ip adress mavjud emas", Toast.LENGTH_SHORT).show()
        }else{
            println(data.toString())
            for (i in data.split(",").indices) {
                if (data.split(",")[i].contains("$1")) {
                    ipAddress = data.split(",")[i].split("$1")[0]
                    break
                }else{
                    ipAddress = data[0].toString().split("$0")[0]
                }
            }
            txtSetIpAdress?.text = ipAddress
            val getPref = sharedPreferences?.getString("smsHistory", "")
            if (getPref == null||getPref== ""||getPref==" ") {
                Toast.makeText(activity, "Smslar tarixi bo'sh", Toast.LENGTH_SHORT).show()
            }else{
                val gson = Gson()
                val data = gson.fromJson(getPref, DataClass::class.java)
                for (i in data.data!!.indices) {
                    phoneList.add(data.data!![i].tel!!)
                    messageList.add(data.data!![i].zapros!!)
                }
                adapter = DashAdapter(activity!!, phoneList, messageList)
                listNatVIew!!.adapter = adapter

            }
            println(getPref.toString())
        }

    }
}