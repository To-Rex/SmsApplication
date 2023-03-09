package com.test.smsapplication.ui.home
import android.Manifest
import android.annotation.SuppressLint
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
    var phoneList = ArrayList<String>()
    var messageList = ArrayList<String>()
    var adapter: DashAdapter? = null
    var homeList: ListView? = null
    var btnHomeNewSms: Button? = null
    var btnHomSendSms: Button? = null
    var sharedPreferences: SharedPreferences? = null
    val permissionRequest = 101
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
        //activity?.startService(Intent(activity, BackService::class.java))
        btnHomeNewSms!!.setOnClickListener {
            getData()
        }
        btnHomSendSms!!.setOnClickListener {
            //send sms "+998995340313"
            activity?.startService(Intent(activity, BackService::class.java))
        }
        getData()
        return view
    }
    private fun getData(){
        homeList!!.adapter = null
        phoneList.clear()
        messageList.clear()
        /*ApiClient.userService.updateStatus("2").enqueue(
            object : Callback<DataClass> {
                override fun onResponse(
                    call: Call<DataClass>,
                    response: Response<DataClass>
                ) {
                    if (response.isSuccessful) {
                        val data = response.body()
                        for (i in data?.data?.indices!!) {
                            phoneList.add(data.data!![i].tel!!)
                            messageList.add(data.data!![i].zapros!!)
                        }
                        adapter = DashAdapter(activity!!, phoneList, messageList)
                        homeList!!.adapter = adapter
                    } else {
                        println("Error ------ : ${response.errorBody()}")
                    }
                }

                override fun onFailure(call: Call<DataClass>, t: Throwable) {
                    println("Errors => : ${t.message}")
                }
            }
        )*/
        sharedPreferences = activity?.getSharedPreferences("ipAddress", 0)
        val data = sharedPreferences?.getString("ipAddress", "")
        var ipAdress = ""
        for (i in data?.split(",")?.indices!!) {
            if (data.split(",")[i].contains("$1")) {
                ipAdress = data.split(",")[i].replace("$1", "")
                break
            }else{
                ipAdress = data[0].toString().replace("$0", "")
            }
        }
        Toast.makeText(activity, data, Toast.LENGTH_SHORT).show()
        val queue = Volley.newRequestQueue(activity)
        val url = "${ipAdress}sms/status?status=2"
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                println("Response is: $response")
                val gson = Gson()
                val dataClass = gson.fromJson(response, DataClass::class.java)
                for (i in dataClass.data?.indices!!) {
                    phoneList.add(dataClass.data!![i].tel!!)
                    messageList.add(dataClass.data!![i].zapros!!)
                }
                adapter = DashAdapter(activity!!, phoneList, messageList)
                homeList!!.adapter = adapter
            },
            { println("That didn't work!") })
        queue.add(stringRequest)
    }
}