package com.test.smsapplication.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.test.smsapplication.MainActivity
import com.test.smsapplication.R
import com.test.smsapplication.Sample
import com.test.smsapplication.adapters.DashAdapter
import com.test.smsapplication.models.DataClass
import com.test.smsapplication.service.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {
    var phoneList = ArrayList<String>()
    var messageList = ArrayList<String>()
    var adapter: DashAdapter? = null
    var homeList: ListView? = null
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        homeList = view.findViewById<ListView>(R.id.homeList)
        //val itemList = listOf("bjhbjh 1", "kjnkjnk 2", "nkjkj 3", "Item 4", "Item 5","Item 3", "Item 4", "Item 5","Item 3", "Item 4", "Item 5","Item 3", "Item 4", "Item 5","Item 3", "Item 4", "Item 5")
        //val itemList1 = listOf("Item 1", "Item 2", "Item 3", "Item 4", "Item 5","Item 3", "Item 4", "Item 5","Item 3", "Item 4", "Item 5","Item 3", "Item 4", "Item 5","Item 3", "Item 4", "Item 5")
        //adapter = DashAdapter(activity!!, itemList, itemList1)
        //homeList.adapter = adapter
        getData()
        return view
    }

    fun getData(){
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
                            /*phoneNumbers.add(data.data!![i].tel!!)
                            message.add(data.data!![i].zapros!!)
                            smsId.add(data.data!![i].id)*/
                            //Toast.makeText(this@BackService, "Tel: ${data.data!![i].tel}", Toast.LENGTH_SHORT).show()
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