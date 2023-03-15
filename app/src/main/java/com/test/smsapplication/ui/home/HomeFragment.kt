package com.test.smsapplication.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Vibrator
import android.telephony.SmsManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
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
    var txtHomeLimit: TextView? = null
    private var sharedPreferences: SharedPreferences? = null
    var totalElements = 0
    var totalPages = 0
    var size = 50
    var page = 1
    var swipeRefreshHome: SwipeRefreshLayout? = null
    var SCROLL_STATE_IDLE = 0
    private var vibrator: Vibrator? = null

    @SuppressLint(
        "MissingInflatedId", "ObsoleteSdkInt", "ServiceCast", "SetTextI18n",
        "UseCompatLoadingForDrawables"
    )
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
        txtHomeLimit = view.findViewById(R.id.txtHomipLimt)
        swipeRefreshHome = view.findViewById(R.id.swipeRefreshHome)

        swipeRefreshHome?.setOnRefreshListener {
            getData()
        }
        //homeList scroll from bottom to top listView when it reaches the top swipeRefreshHome will be enabled
        homeList?.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {
                if (scrollState == SCROLL_STATE_IDLE) {
                    if (homeList?.firstVisiblePosition == 0) {
                        swipeRefreshHome?.isEnabled = true
                    }
                }
            }

            override fun onScroll(
                view: AbsListView?,
                firstVisibleItem: Int,
                visibleItemCount: Int,
                totalItemCount: Int
            ) {
                if (firstVisibleItem > 0) {
                    swipeRefreshHome?.isEnabled = false
                }
            }
        })

        btnHomeNewSms!!.setOnClickListener {
            getNewData()
        }

        btnHomSendSms!!.setOnClickListener {
            if (phoneList.size == 0) {
                Toast.makeText(
                    activity,
                    "SMS yuborish uchun telefon raqam va xabar kiriting",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            activity?.startService(Intent(activity, BackService::class.java))
        }
        if (context?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.SEND_SMS
                )
            } != PackageManager.PERMISSION_GRANTED) {
            val alertDialog = AlertDialog.Builder(context)
            alertDialog.setTitle("Ruxsat")
            alertDialog.setMessage("Ushbu ilovaga Sozlamalar->Izohlar->SMS yuborishga ruxsat berish kerak")
            alertDialog.setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            alertDialog.show()
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.SEND_SMS),
                1
            )
        }

        val displayMetrics = resources.displayMetrics
        val displayWidth = displayMetrics.widthPixels
        txtHomeLimit!!.setOnClickListener {
            val alertDialog = AlertDialog.Builder(context)
            alertDialog.setCancelable(false)
            alertDialog.setTitle("Smslaringizni sonini")
            alertDialog.setMessage("Smslaringizni sonini kiriting! (minimal 50 ta)")
            val input = EditText(context)
            alertDialog.setView(input)
            input.inputType = 2
            input.hint = "Smslar soni kiriting"
            //edittext width 60% of screen width and height 100dp and background color white and corner radius 10dp
            input.width = (displayWidth / 100) * 60
            input.background = resources.getDrawable(R.drawable.edit_text_back)

            alertDialog.setPositiveButton("Kiritish") { dialog, _ ->
                val smsLimit = input.text.toString()
                if (smsLimit.trim().isEmpty()) {
                    showDialog("Xatolik", "Smslar soni kiriting")
                } else if (smsLimit.toInt() < 50) {
                    showDialog("Xatolik", "Smslar soni minimal 50 ta")
                } else if (smsLimit.toInt() > 5000) {
                    showDialog("Xatolik", "Smslar soni maximal 5000 ta")
                } else {
                    sharedPreferences = activity?.getSharedPreferences("ipAddress", 0)
                    sharedPreferences?.edit()?.putString("smsLimit", smsLimit)?.apply()
                    txtHomeLimit!!.text = "Sms Limit: $smsLimit"
                    dialog.dismiss()
                    showDialog("Xabar", "Smslar soni saqlandi")
                }
            }
            alertDialog.setNegativeButton("Bekor qilish") { dialog, _ ->
                dialog.dismiss()
            }
            alertDialog.show()
        }
        getData()
        return view
    }

    private fun showDialog(title: String, message: String) {
        val alertDialog = AlertDialog.Builder(context)
        alertDialog.setCancelable(false)
        alertDialog.setTitle(title)
        alertDialog.setMessage(message)
        alertDialog.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        alertDialog.show()
    }

    @SuppressLint("SetTextI18n")
    private fun getData() {
        homeList!!.adapter = null
        phoneList.clear()
        messageList.clear()
        sharedPreferences = activity?.getSharedPreferences("ipAddress", 0)
        val data = sharedPreferences?.getString("ipAddress", "")
        var ipAddress = ""
        if (data == null || data == "" || data == " ") {
            Toast.makeText(activity, "iltimos IP manzilni kiriting", Toast.LENGTH_SHORT).show()
            return
        }
        val phone = sharedPreferences?.getString("phone", "").toString()
        Toast.makeText(activity, phone, Toast.LENGTH_SHORT).show()
        if (phone.isEmpty() || phone == " ") {
            Toast.makeText(activity, "iltimos telefon raqamni kiriting", Toast.LENGTH_SHORT).show()
            showDialog("Xatolik", "iltimos telefon raqamni kiriting")
            return
        }
        val smsLimit = sharedPreferences?.getString("smsLimit", "")
        if (smsLimit == null || smsLimit == "" || smsLimit == " ") {
            txtHomeLimit!!.text = "Sms Limit: 50"
        } else {
            txtHomeLimit!!.text = "Sms Limit: $smsLimit"
        }
        for (i in data.split(",").indices) {
            println(data[i].toString())
            if (data.split(",")[i].contains("$1")) {
                ipAddress = data.split(",")[i].split("$1")[0]
                break
            } else {
                ipAddress = data.split(",")[0].replace("$0", "")
            }
        }
        txtHomipAdress!!.text = ipAddress
        var pageSize = 0
        //val url = "${ipAddress}api/sms/998$phone?page=0&size=20&flag=2"
        //https://api.teda.uz:7788/api/sms/status?page=0&size=20&employeePhone=%2B998977515747&status=2
        val url = "${ipAddress}api/sms/status?page=0&size=20&employeePhone=%2B998$phone&status=2"
        getResponse(url)
        /*if(page == 1){
            getResponse(url)
        }else{
            for (i in 1..page){
                getResponse(url)
                pageSize++
            }
        }*/
    }

    private fun getResponse(url: String) {
        println("====-= - =-= =--=-=-=-=- " + url)
        val queue = Volley.newRequestQueue(activity)
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                println("Response is-------: $response")
                val gson = Gson()
                val dataClass = gson.fromJson(response, DataClass::class.java)
                for (i in dataClass.data?.content?.indices!!) {
                    phoneList.add(dataClass.data?.content!![i].tel!!)
                    messageList.add(dataClass.data?.content!![i].zapros!!)
                }
                totalElements = dataClass.data?.totalElements!!
                totalPages = dataClass.data?.totalPages!!
                page = dataClass.data?.number!!

                println("totalElements: $totalElements")
                println("totalPages: $totalPages")
                println("page: $page")

                adapter = DashAdapter(activity!!, phoneList, messageList)
                homeList!!.adapter = adapter
                swipeRefreshHome?.isRefreshing = false
            },
            {
                println("That didn't work!")
                swipeRefreshHome?.isRefreshing = false
            })
        queue.add(stringRequest)
    }

    @SuppressLint("SetTextI18n")
    private fun getNewData() {
        phoneList.clear()
        messageList.clear()
        sharedPreferences = activity?.getSharedPreferences("ipAddress", 0)
        val data = sharedPreferences?.getString("ipAddress", "")
        var ipAddress = ""
        if (data == null || data == "" || data == " ") {
            Toast.makeText(activity, "iltimos IP manzilni kiriting", Toast.LENGTH_SHORT).show()
            return
        }
        val phone = sharedPreferences?.getString("phone", "").toString()
        Toast.makeText(activity, phone, Toast.LENGTH_SHORT).show()
        if (phone.isEmpty() || phone == " ") {
            Toast.makeText(activity, "iltimos telefon raqamni kiriting", Toast.LENGTH_SHORT).show()
            showDialog("Xatolik", "iltimos telefon raqamni kiriting")
            return
        }
        val smsLimit = sharedPreferences?.getString("smsLimit", "")
        if (smsLimit == null || smsLimit == "" || smsLimit == " ") {
            txtHomeLimit!!.text = "Sms Limit: 50"
        } else {
            txtHomeLimit!!.text = "Sms Limit: $smsLimit"
        }
        for (i in data.split(",").indices) {
            println(data[i].toString())
            if (data.split(",")[i].contains("$1")) {
                ipAddress = data.split(",")[i].split("$1")[0]
                break
            } else {
                ipAddress = data.split(",")[0].replace("$0", "")
            }
        }
        txtHomipAdress!!.text = ipAddress
        val queue = Volley.newRequestQueue(activity)
        println(ipAddress)
        //val url = "https://api.teda.uz:7788/api/sms/status?page=0&size=20&employeePhone=%2B998977515747&status=1"
        val url = "${ipAddress}api/sms/status?page=0&size=20&employeePhone=%2B998$phone&status=1"
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                println("Response is: $response")
                /*//{"message":"Here!!!","success":true,"data":{"content":[{"id":2,"rezult":"nma gap","zapros":"nma gap","platforma":null,"tel":"+998977515747","employeePhone":"+998977515747","flag":3,"sana":"2023-03-11T22:13:25.975295"},{"id":1,"rezult":"Salom","zapros":"
                val gson = Gson()
                val dataClass = gson.fromJson(response, DataClass::class.java)
                for (i in dataClass.data?.content?.indices!!) {
                    phoneList.add(dataClass.data?.content!![i].tel!!)
                    messageList.add(dataClass.data?.content!![i].zapros!!)
                }
                adapter = DashAdapter(activity!!, phoneList, messageList)
                homeList!!.adapter = adapter
                swipeRefreshHome?.isRefreshing = false*/
                getData()
            },
            {
                swipeRefreshHome?.isRefreshing = false
                println("That didn't work!")
            })
        queue.add(stringRequest)

    }
}