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
            clearData()
        }
        btnNatUpdate!!.setOnClickListener {
            getData()
        }
        return view
    }
    private fun getData(){
        phoneList.clear()
        messageList.clear()
        sharedPreferences = activity?.getSharedPreferences("ipAddress", 0)
        val getSms = sharedPreferences?.getString("smsHistory", "")?.replace(" ", "")
        val getPhone = sharedPreferences?.getString("phoneHistory", "")?.replace(" ", "")
        println("smsHistory: $getSms")
        println("phoneHistory: $getPhone")
        if (getSms!!.isEmpty() && getPhone!!.isEmpty()) {
            Toast.makeText(activity, "No Data Found", Toast.LENGTH_SHORT).show()
        } else {
            val smsList = getSms.split(",")
            val phoneList = getPhone!!.split(",")
            adapter = DashAdapter(activity!!, phoneList, smsList)
            listNatVIew?.adapter = adapter
        }
    }
    private fun clearData() {
        sharedPreferences = activity?.getSharedPreferences("ipAddress", 0)
        val editor = sharedPreferences?.edit()
        editor?.putString("smsHistory", "")
        editor?.putString("phoneHistory", "")
        editor?.apply()
        getData()
    }
}