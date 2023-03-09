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
import com.test.smsapplication.adapters.DashAdapter
import com.test.smsapplication.adapters.SetAdapter

class SettingsFragment : Fragment() {

    var ediSetIpAdress: EditText? = null
    var btnSetAdd: ImageView? = null
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


        sharedPreferences = activity?.getSharedPreferences("ipAddress", 0)
        data = sharedPreferences?.getString("ipAddress", "").toString()
        
        btnSetAdd?.setOnClickListener {
            //shared preferences list save ip address
            val editor = sharedPreferences?.edit()
            editor?.putString("ipAddress", data+ediSetIpAdress?.text.toString()+"$0,")
            editor?.apply()
            getData()
        }
        /*val editor = sharedPreferences?.edit()
        editor?.clear()
        editor?.apply()*/
        getData()
        return view
    }

    fun getData(){
        linkList.clear()
        verList.clear()
        listSettings?.adapter = null
        sharedPreferences = activity?.getSharedPreferences("ipAddress", 0)
        val data = sharedPreferences?.getString("ipAddress", "").toString()
        println(data)
        if (data == ""){
            Toast.makeText(activity,"No data", Toast.LENGTH_SHORT).show()
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
    }
}