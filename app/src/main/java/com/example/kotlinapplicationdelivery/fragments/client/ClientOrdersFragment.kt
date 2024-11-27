package com.example.kotlinapplicationdelivery.fragments.client

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.kotlinapplicationdelivery.R
import com.example.kotlinapplicationdelivery.adapters.TabsPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class ClientOrdersFragment : Fragment() {

    private var myView: View? = null

    private var viewpager: ViewPager2? = null
    private var tabLayout: TabLayout? = null
    private var toolbar: Toolbar? = null
    private var titleBar : TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        myView = inflater.inflate(R.layout.fragment_client_orders, container, false)
        titleBar = myView?.findViewById(R.id.custom_toolbar_title)
        viewpager = myView?.findViewById(R.id.viewpager)
        tabLayout = myView?.findViewById(R.id.tab_layout)

        toolbar = myView?.findViewById(R.id.toolbar)
        toolbar?.setTitleTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        toolbar?.title = ""
        titleBar?.text = "Đơn hàng"
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        tabLayout?.setSelectedTabIndicatorColor(Color.BLACK)
        tabLayout?.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
        tabLayout?.tabTextColors = ContextCompat.getColorStateList(requireContext(), R.color.black)
        tabLayout?.tabMode = TabLayout.MODE_SCROLLABLE
        tabLayout?.isInlineLabel = true

        val numberOfTabs = 4

        val adapter = TabsPagerAdapter(requireActivity().supportFragmentManager, lifecycle, numberOfTabs)
        viewpager?.adapter = adapter
        viewpager?.isUserInputEnabled = true

        TabLayoutMediator(tabLayout!!, viewpager!!) { tab, position ->

            when(position) {
                0 -> {
                    tab.text = "Chờ thanh toán"
                }
                1 -> {
                    tab.text = "Đã gửi đi"
                }
                2 -> {
                    tab.text = "Đang trên đường"
                }
                3 -> {
                    tab.text = "Đã giao hàng"
                }
            }

        }.attach()


        return myView
    }
}