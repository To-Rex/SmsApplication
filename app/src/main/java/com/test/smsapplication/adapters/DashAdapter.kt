package com.test.smsapplication.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.test.smsapplication.R

class DashAdapter(private val context: Context, private val itemList: List<String>,private val itemList1: List<String>) : BaseAdapter(){
    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var convertView = convertView
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_kontakt, parent, false)
        }

        val txtItemMessage = convertView!!.findViewById<TextView>(R.id.txtItemMessage)
        val txtItemPhone = convertView.findViewById<TextView>(R.id.txtItemPhone)
        if (itemList1[position].isEmpty()||itemList[position].isEmpty()){
            txtItemMessage.text = "No data"
            txtItemPhone.text = "No data"
        } else{
            txtItemMessage.text = itemList1[position]
            txtItemPhone.text = itemList[position]
        }
        convertView.setOnClickListener(View.OnClickListener {
            val dialog = android.app.AlertDialog.Builder(context)
            dialog.setTitle(itemList[position])
            dialog.setMessage(itemList1[position])
            dialog.setPositiveButton("OK"){ dialog, _ ->
                dialog.dismiss()
            }
            dialog.show()
        })

        return convertView
    }

    override fun getItem(position: Int): Any {
        return itemList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return itemList.size
    }
}