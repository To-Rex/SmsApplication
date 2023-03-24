package com.test.smsapplication.ui.settings

import android.annotation.SuppressLint
import android.app.AlertDialog
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.test.smsapplication.R
import com.test.smsapplication.adapters.MyAdapterCallback
import com.test.smsapplication.adapters.SetAdapter
class SettingsFragment : Fragment() {
    var ediSetIpAdress: EditText? = null
    var ediSetPhone: EditText? = null
    var btnSetAdd: ImageView? = null
    var btnSetAddPhone: ImageView? = null
    var txtSetIpAdress: TextView? = null
    var txtSetPhone: TextView? = null
    var txtSetphoneId: TextView? = null
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
        ediSetPhone = view.findViewById(R.id.ediSetPhone)
        btnSetAdd = view.findViewById(R.id.btnSetAdd)
        listSettings = view.findViewById(R.id.listSettings)
        txtSetIpAdress = view.findViewById(R.id.txtSetIpAdress)
        txtSetPhone = view.findViewById(R.id.txtSetPhone)
        btnSetAddPhone = view.findViewById(R.id.btnSetAddPhone)
        txtSetphoneId = view.findViewById(R.id.txtSetphoneId)


        sharedPreferences = activity?.getSharedPreferences("ipAddress", 0)
        data = sharedPreferences?.getString("ipAddress", "").toString()
        getPref()
        btnSetAdd?.setOnClickListener {
            val editor = sharedPreferences?.edit()
            editor?.putString("ipAddress", data+ediSetIpAdress?.text.toString().trim()+"$0,")
            editor?.apply()
            getData()
        }
        btnSetAddPhone?.setOnClickListener {
            val editor = sharedPreferences?.edit()
            editor?.putString("phone", ediSetPhone?.text.toString().trim())
            editor?.apply()
            getData()
        }

        txtSetPhone?.setOnClickListener {
            val dialog = AlertDialog.Builder(context)
            dialog.setTitle("Telefon raqamni o`chirish")
            dialog.setMessage("Telefon raqamni o`chirishni yoki o`zgartirishni istaysizmi?")
            dialog.setPositiveButton("O`chirish") { _, _ ->
                val editor = sharedPreferences?.edit()
                editor?.putString("phone", "")
                editor?.apply()
                getData()
            }
            dialog.setNegativeButton("O`zgartirish") { _, _ ->
                ediSetPhone?.visibility = View.VISIBLE
                btnSetAddPhone?.visibility = View.VISIBLE
                txtSetphoneId?.visibility = View.VISIBLE
            }
            dialog.setNeutralButton("Yo`q") { dialog, _ ->
                dialog.dismiss()
            }
            dialog.show()
        }

        object : MyAdapterCallback {
            override fun onDeleteButtonClicked(position: Int) {
                Toast.makeText(activity, "clicked$position", Toast.LENGTH_SHORT).show()
            }

            override fun onEditButtonClicked(position: Int) {
                Toast.makeText(activity, "clicked$position", Toast.LENGTH_SHORT).show()
            }
        }

        getData()
        return view
    }
    private fun getPref(){
        sharedPreferences = activity?.getSharedPreferences("ipAddress", 0)
        data = sharedPreferences?.getString("ipAddress", "").toString()
        val phone = sharedPreferences?.getString("phone", "").toString()


        if (phone == ""){
            txtSetPhone?.text = resources.getString(R.string.telefon_raqam)
            ediSetPhone?.visibility = View.VISIBLE
            btnSetAddPhone?.visibility = View.VISIBLE
            txtSetphoneId?.visibility = View.VISIBLE
        }else{
            txtSetPhone?.text = phone
            ediSetPhone?.visibility = View.GONE
            btnSetAddPhone?.visibility = View.GONE
            txtSetphoneId?.visibility = View.GONE
        }
        txtSetPhone?.text = phone
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
                ipAddress = data.split(",")[0].replace("$0","")
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
        adapter = SetAdapter(activity!!,verList, linkList, object : MyAdapterCallback {
            override fun onDeleteButtonClicked(position: Int) {
                Toast.makeText(activity, "clicked$position", Toast.LENGTH_SHORT).show()
                val editor = context?.getSharedPreferences("ipAddress", 0)?.edit()
                val data = context?.getSharedPreferences("ipAddress", 0)?.getString("ipAddress", "")
                val data1: MutableList<String> = if (data?.contains("$0,")!!) {
                    data.split("$0,").toMutableList()
                } else {
                    data.split("$1,").toMutableList()
                }
                data1.removeAt(position)
                editor!!.putString("ipAddress", data1.joinToString("$0,"))
                editor.apply()
                getData()
            }

            override fun onEditButtonClicked(position: Int) {
                getData()
            }
        })
        listSettings?.adapter = adapter
        getPref()
    }

}