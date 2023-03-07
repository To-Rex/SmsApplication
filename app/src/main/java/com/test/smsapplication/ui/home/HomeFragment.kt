package com.test.smsapplication.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.test.smsapplication.R
import com.test.smsapplication.adapters.DashAdapter
import com.test.smsapplication.models.DataClass
import com.test.smsapplication.service.ApiClient
import com.test.smsapplication.service.BackService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {
    var phoneList = ArrayList<String>()
    var messageList = ArrayList<String>()
    var adapter: DashAdapter? = null
    var homeList: ListView? = null
    var btnHomeNewSms: Button? = null
    var btnHomSendSms: Button? = null
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        homeList = view.findViewById(R.id.homeList)
        btnHomeNewSms = view.findViewById(R.id.btnHomNewSms)
        btnHomSendSms = view.findViewById(R.id.btnHomSendSms)

        btnHomeNewSms!!.setOnClickListener {
            getData()
        }
        btnHomSendSms!!.setOnClickListener {
            activity?.startService(Intent(activity, BackService::class.java))
        }
        getData()
        return view
    }

    private fun getData(){
        homeList!!.adapter = null
        phoneList.clear()
        messageList.clear()
        ApiClient.userService.updateStatus("2").enqueue(
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
        )
    }
}