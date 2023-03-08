package com.test.smsapplication.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.TextView
import com.test.smsapplication.R

class SetAdapter(private val context: Context, private val itemList: List<String>, private val itemList1: List<String>) : BaseAdapter(){
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var convertView = convertView

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_settings, parent, false)
        }

        val txtItemMessage = convertView!!.findViewById<TextView>(R.id.txtItemSettings)
        val imgItemSettings = convertView.findViewById<ImageButton>(R.id.imgItemSettings)
        txtItemMessage.text = itemList1[position].replace("$0", "")

        imgItemSettings.setOnClickListener {

            //delete shared preferences data delete until separated by commas
            val editor = context.getSharedPreferences("ipAddress", 0).edit()
            editor.putString("ipAddress", itemList[position].replace(itemList1[position], ""))
            editor.apply()
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