package com.test.smsapplication.ui.settings

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.test.smsapplication.R
import com.test.smsapplication.adapters.SetAdapter

class SettingsFragment : Fragment() {
    var ediSetIpAdress: EditText? = null
    var btnSetAdd: ImageView? = null
    var txtSetIpAdress: TextView? = null
    var sharedPreferences: SharedPreferences? = null
    var listSettings: ListView? = null
    var data = ""
    var adapter: SetAdapter? = null
    var linkList = ArrayList<String>()
    var verList = ArrayList<String>()
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        ediSetIpAdress = view.findViewById(R.id.ediSetIpAdress)
        btnSetAdd = view.findViewById(R.id.btnSetAdd)
        listSettings = view.findViewById(R.id.listSettings)
        txtSetIpAdress = view.findViewById(R.id.txtSetIpAdress)

        sharedPreferences = activity?.getSharedPreferences("ipAddress", 0)
        data = sharedPreferences?.getString("ipAddress", "").toString()
        getPref()
        btnSetAdd?.setOnClickListener {
            val editor = sharedPreferences?.edit()
            editor?.putString("ipAddress", data+ediSetIpAdress?.text.toString()+"$0,")
            editor?.apply()
            getData()
        }
        getData()
        return view
    }

    fun getPref(){
        sharedPreferences = activity?.getSharedPreferences("ipAddress", 0)
        data = sharedPreferences?.getString("ipAddress", "").toString()
        var ipAddress = ""
        if (data== "" || data==" ") {
            Toast.makeText(activity, "bunday Ip adress mavjud emas", Toast.LENGTH_SHORT).show()
            return
        }
        println(data)
        for (i in data.split(",").indices) {
            println(data[i].toString())
            if (data.split(",")[i].contains("$1")) {
                ipAddress = data.split(",")[i].split("$1")[0]
                break
            }else{
                ipAddress = data[0].toString().split("$0")[0]
                println("======"+ipAddress)
            }
        }
        txtSetIpAdress?.text = ipAddress
    }
    private fun getData(){
        linkList.clear()
        verList.clear()
        listSettings?.adapter = null
        sharedPreferences = activity?.getSharedPreferences("ipAddress", 0)
        val data = sharedPreferences?.getString("ipAddress", "").toString()
        println(data)
        if (data == ""){
            Toast.makeText(activity,"Ip adress mavjud emas",Toast.LENGTH_SHORT).show()
            return
        }
        for (i in data.split(",")){
            if (i == ""){
                continue
            }else{
                linkList.add(i)
            }
        }
        for (i in linkList){
            if (i == ""){
                continue
            }else{
                verList.add(i.substring(i.length-1,i.length))
            }
        }
        adapter = SetAdapter(activity!!,verList, linkList)
        listSettings?.adapter = adapter
        getPref()
    }

    fun updateList() {

    }
}