package com.test.smsapplication.ui.dashboard

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.test.smsapplication.R
import com.test.smsapplication.adapters.DashAdapter

class DashboardFragment : Fragment() {
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)
        val dashList = view.findViewById<ListView>(R.id.dashList)
        val itemList = listOf("bjhbjh 1", "kjnkjnk 2", "nkjkj 3", "Item 4", "Item 5","Item 3", "Item 4", "Item 5","Item 3", "Item 4", "Item 5","Item 3", "Item 4", "Item 5","Item 3", "Item 4", "Item 5")
        val itemList1 = listOf("Item 1", "Item 2", "Item 3", "Item 4", "Item 5","Item 3", "Item 4", "Item 5","Item 3", "Item 4", "Item 5","Item 3", "Item 4", "Item 5","Item 3", "Item 4", "Item 5")
        val adapter = DashAdapter(activity!!, itemList, itemList1)
        dashList.adapter = adapter
        return view
    }
}