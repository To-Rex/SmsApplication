package com.test.smsapplication.ui.dashboard

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.test.smsapplication.R
import com.test.smsapplication.adapters.DashAdapter

class DashboardFragment : Fragment() {

    var sharedPreferences: SharedPreferences? = null
    private var phoneList = ArrayList<String>()
    private var messageList = ArrayList<String>()
    private var smsId = ArrayList<String>()
    var btnDashRefresh: Button? = null
    var dashList: ListView? = null
    var ipLink = ""

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)
        dashList = view.findViewById(R.id.dashList)
        btnDashRefresh = view.findViewById(R.id.btnDashRefresh)
        btnDashRefresh!!.setOnClickListener {
            smsId.clear()
            sharedPreferences = activity?.getSharedPreferences("ipAddress", 0)
            val getErrSmsId = sharedPreferences?.getString("errSmsId", "")
            val getErrSmsIdList = getErrSmsId?.split(",")
            for (i in getErrSmsIdList?.indices!!) {
                if (getErrSmsIdList[i].isNotEmpty()) {
                    smsId.add(getErrSmsIdList[i])
                }
            }
            updateSmsStatus(phoneList, smsId, smsId)
        }
        getData()
        return view
    }

    private fun getData() {
        phoneList.clear()
        messageList.clear()
        sharedPreferences = activity?.getSharedPreferences("ipAddress", 0)
        val data = sharedPreferences?.getString("ipAddress", "")
        for (i in data?.split(",")?.indices!!) {
            if (data.split(",")[i].contains("$1")) {
                ipLink = data.split(",")[i].replace("$1", "")
                break
            } else {
                ipLink = data.split(",")[0].replace("$0", "")
            }
        }
        val getSms = sharedPreferences?.getString("smsErrHistory", "")?.replace(" ", "")
        val getPhone = sharedPreferences?.getString("phoneErrHistory", "")?.replace(" ", "")

        if (getSms!!.isEmpty() && getPhone!!.isEmpty()) {
            Toast.makeText(activity, "No Data Found", Toast.LENGTH_SHORT).show()
        } else {
            val smsList = getSms.split(",")
            val phoneList = getPhone!!.split(",")
            val adapter = DashAdapter(activity!!, phoneList, smsList)
            dashList?.adapter = adapter
        }
    }
    private fun clearData() {
        sharedPreferences = activity?.getSharedPreferences("ipAddress", 0)
        val editor = sharedPreferences?.edit()
        editor?.putString("smsErrHistory", "")
        editor?.putString("phoneErrHistory", "")
        editor?.apply()
        getData()
    }
    private fun updateSmsStatus(
        phoneNumbers: ArrayList<String>,
        message: ArrayList<String>,
        smsId: ArrayList<String>
    ) {
        sharedPreferences = activity?.getSharedPreferences("ipAddress", 0)
        val queue = Volley.newRequestQueue(activity)
        val url = "${ipLink}api/sms"
        println("url: $url")
        println("phoneNumbers: $phoneNumbers")
        println("message: $message")
        println("smsId: $smsId")
        val stringRequest = object : StringRequest(
            Method.PUT, url,
            { response ->
                println("Response is: $response")
                if (response.contains("true")) {
                    clearData()
                }else{
                    Toast.makeText(activity, "Nimadur xato ketdi", Toast.LENGTH_SHORT).show()
                }
            },
            {
                println("That didn't work!")
                showDialog("Xatolik", "Internetga ulanishda xatolik yoki serverga ulanishda xatolik")
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

    private fun showDialog(title: String, message: String){
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("OK"){dialog, which ->
            dialog.dismiss()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}