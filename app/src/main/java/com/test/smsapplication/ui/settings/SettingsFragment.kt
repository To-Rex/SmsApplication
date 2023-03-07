package com.test.smsapplication.ui.settings

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.test.smsapplication.R

class SettingsFragment : Fragment() {

    var ediSetIpAdress: EditText? = null
    var btnSetAdd: ImageView? = null
    var sharedPreferences: SharedPreferences? = null
    var listSettings: ListView? = null
    var data = ""
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


        sharedPreferences = activity?.getSharedPreferences("ipAddress", 0)
        //get ip address
        Toast.makeText(activity,data+ sharedPreferences?.getString("ipAddress", ""), Toast.LENGTH_SHORT).show()
        data = sharedPreferences?.getString("ipAddress", "").toString()
        btnSetAdd?.setOnClickListener {
            //shared preferences list save ip address
            val editor = sharedPreferences?.edit()
            editor?.putString("ipAddress", data+","+ediSetIpAdress?.text.toString()+"$0")
            editor?.apply()
        }
        getData()
        return view
    }

    fun getData(){
        sharedPreferences = activity?.getSharedPreferences("ipAddress", 0)
        val listDat: MutableList<String> = mutableListOf()
        //for spilit "," sharedPreferences data and add list
        val data = sharedPreferences?.getString("ipAddress", "").toString()
        for (i in data.split(",")){
            listDat.add(i)
        }
        val adapter = ArrayAdapter(activity!!, android.R.layout.simple_list_item_1, listDat)
        listSettings?.adapter = adapter
    }
}