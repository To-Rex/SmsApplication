package com.test.smsapplication.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.test.smsapplication.R
import com.test.smsapplication.ui.settings.SettingsFragment

class SetAdapter(
    private val context: Context,
    private val itemList: List<String>,
    private val itemList1: List<String>
) : BaseAdapter() {
    private var onItemClickListener: OnItemClickListener? = null
    public interface OnItemClickListener {
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var convertView = convertView

        if (convertView == null) {
            convertView =
                LayoutInflater.from(context).inflate(R.layout.item_settings, parent, false)
        }

        val txtItemMessage = convertView!!.findViewById<TextView>(R.id.txtItemSettings)
        val imgItemSettings = convertView.findViewById<ImageButton>(R.id.imgItemSettings)
        val itemSettings = convertView.findViewById<View>(R.id.itemSettings)

        if (itemList1[position].contains("$0")) {
            txtItemMessage.text = itemList1[position].replace("$0", "")
            itemSettings.setBackgroundResource(R.drawable.buttons_back)
            imgItemSettings.setBackgroundResource(R.drawable.buttons_back)
        } else {
            txtItemMessage.text = itemList1[position].replace("$1", "")
            itemSettings.setBackgroundResource(R.drawable.buttons_back_green)
            imgItemSettings.setBackgroundResource(R.drawable.buttons_back_green)
        }

        imgItemSettings.setOnClickListener {
            val editor = context.getSharedPreferences("ipAddress", 0).edit()
            val data = context.getSharedPreferences("ipAddress", 0).getString("ipAddress", "")
            val data1: MutableList<String> = if (data?.contains("$0,")!!) {
                data.split("$0,").toMutableList()
            } else {
                data.split("$1,").toMutableList()
            }
            data1.removeAt(position)
            editor.putString("ipAddress", data1.joinToString("$0,"))
            editor.apply()
            notifyDataSetChanged()
        }

        convertView.setOnClickListener(View.OnClickListener {
            val dialog = android.app.AlertDialog.Builder(context)
            dialog.setTitle(itemList[position])
            dialog.setMessage(itemList1[position])
            dialog.setPositiveButton("OK") { dialog, _ ->
                val editor = context.getSharedPreferences("ipAddress", 0).edit()
                val data = context.getSharedPreferences("ipAddress", 0).getString("ipAddress", "")
                val data1 = data?.split(",")?.toMutableList()
                println(data1)
                if (data1?.get(position)?.contains("$0")!!) {
                    data1[position] = data1[position].replace("$0", "$1")
                    itemSettings.setBackgroundResource(R.drawable.buttons_back)
                    imgItemSettings.setBackgroundResource(R.drawable.buttons_back)
                } else {
                    data1[position] = data1[position].replace("$1", "$0")
                    itemSettings.setBackgroundResource(R.drawable.buttons_back_green)
                    imgItemSettings.setBackgroundResource(R.drawable.buttons_back_green)
                }
                editor.putString("ipAddress", data1.joinToString(","))
                editor.apply()
                notifyDataSetChanged()
                dialog.dismiss()
            }
            dialog.setNegativeButton("Cancel") { dialog, _ ->
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