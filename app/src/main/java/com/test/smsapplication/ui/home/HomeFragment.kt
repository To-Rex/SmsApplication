package com.test.smsapplication.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.test.smsapplication.MainActivity
import com.test.smsapplication.R
import com.test.smsapplication.Sample

class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        //toolbar disabled (activity as MainActivity).supportActionBar?.hide()
        (activity as Sample).supportActionBar?.hide()
        return view
    }
}