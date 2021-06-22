package com.eitu.arcornbook

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Color
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.viewpager2.widget.ViewPager2
import com.eitu.arcornbook.databinding.FragmentBookBinding
import com.google.android.material.tabs.TabLayoutMediator
import java.text.SimpleDateFormat
import java.util.*

class BookFragment : Fragment() {

    lateinit var binding:FragmentBookBinding
    lateinit var mainActivity: MainActivity
    lateinit var adapter:FragmentAdapter

    private val tabTiles = mutableListOf<String>()

    private var picker = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = FragmentBookBinding.inflate(inflater, container, false)

        mainActivity = activity as MainActivity
        adapter = FragmentAdapter(mainActivity)

        setDatePicker()

        setOnClick()

        setViewpager()

        return binding.root
    }

    private fun setDatePicker(){
        val now = Calendar.getInstance()
        val sdf = SimpleDateFormat("yy.MM.dd", Locale.KOREA)
        Log.d("now", sdf.format(now.time))
        binding.datePicker.datePickerTo.text = sdf.format(now.time)
        now.add(Calendar.DATE, -7)
        Log.d("now", sdf.format(now.time))
        binding.datePicker.datePickerFrom.text = sdf.format(now.time)
    }

    private fun setOnClick(){
        val listener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            //형식화
            val year2 = year.toString().substring(2)
            val month2 = if(month<9) "0${month+1}" else "${month+1}"
            val day2 = if(dayOfMonth < 10) "0$dayOfMonth" else "$dayOfMonth"

            //텍스트 입력
            val dateText = "$year2.$month2.$day2"
            if(picker == 0){
                binding.datePicker.datePickerFrom.text = dateText
            }
            else if (picker == 1){
                binding.datePicker.datePickerTo.text = dateText
            }
        }

        binding.datePicker.datePickerFrom.setOnClickListener {
            picker = 0

            val now = Date(System.currentTimeMillis())

            val year = SimpleDateFormat("yyyy", Locale.KOREA).format(now).toInt()
            val month = SimpleDateFormat("MM", Locale.KOREA).format(now).toInt()
            val day = SimpleDateFormat("dd", Locale.KOREA).format(now).toInt()

            val dialog = DatePickerDialog(mainActivity, listener, year, month, day)
            dialog.datePicker.maxDate = Calendar.getInstance().timeInMillis
            dialog.show()
        }
        binding.datePicker.datePickerTo.setOnClickListener {
            picker = 1

            val now = Date(System.currentTimeMillis())

            val year = SimpleDateFormat("yyyy", Locale.KOREA).format(now).toInt()
            val month = SimpleDateFormat("MM", Locale.KOREA).format(now).toInt()
            val day = SimpleDateFormat("dd", Locale.KOREA).format(now).toInt()

            val dialog = DatePickerDialog(mainActivity, listener, year, month, day)
            dialog.datePicker.maxDate = Calendar.getInstance().timeInMillis
            dialog.show()
        }
        binding.datePicker.go.setOnClickListener {
            val dFrom = binding.datePicker.datePickerFrom.text.toString().replace(".", "").toLong()
            val dTo = binding.datePicker.datePickerTo.text.toString().replace(".", "").toLong()
            if(dFrom > dTo){
                Toast.makeText(mainActivity, "날짜 설정을 확인하세요", LENGTH_LONG).show()
            }
            else{
                setByDate(dFrom, dTo)
            }
        }
    }

    private fun setViewpager() {
        Log.d("setView", "start")
        val sp = mainActivity.getSharedPreferences("isFirst", Context.MODE_PRIVATE)

        val fragmentList = mainActivity.returnFragmentList()

        tabTiles.clear()

        tabTiles.add("현금")
        when (fragmentList.size) {
            2 -> {
                val cardName = sp.getString("card1Name", "카드1")!!
                tabTiles.add(cardName)
            }
            3 -> {
                var cardName = sp.getString("card1Name", "카드1")!!
                tabTiles.add(cardName)
                cardName = sp.getString("card2Name", "카드2")!!
                tabTiles.add(cardName)
            }
            4 -> {
                var cardName = sp.getString("card1Name", "카드1")!!
                tabTiles.add(cardName)
                cardName = sp.getString("card2Name", "카드2")!!
                tabTiles.add(cardName)
                cardName = sp.getString("card3Name", "카드3")!!
                tabTiles.add(cardName)
            }
        }

        adapter.fragmentList = fragmentList
        binding.viewPager.adapter = adapter

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                Log.d("pageselected", "$position")
                mainActivity.viewPosition = position
            }
        })

        val normalColor: Int = Color.parseColor("#693400")
        val selectedColor: Int = Color.WHITE
        binding.viewPagerTab.setTabTextColors(normalColor,selectedColor)
        //탭과 뷰페이저를 연결
        TabLayoutMediator(binding.viewPagerTab, binding.viewPager){tab, position ->
            tab.text = tabTiles[position]
        }.attach()
    }

    private fun setByDate(from:Long, to:Long){
        val sp = mainActivity.getSharedPreferences("isFirst", Context.MODE_PRIVATE)
        when(mainActivity.viewPosition){
            0->{
                val cash = adapter.createFragment(0) as CashFragment

                cash.setByDate(from, to)
            }
            1->{
                val cash = adapter.createFragment(0) as CashFragment
                val card1 = adapter.createFragment(1) as Card1Fragment

                cash.setByDate(from, to)
                card1.setByDate(from, to)
            }
            2->{
                val cash = adapter.createFragment(0) as CashFragment
                val card1 = adapter.createFragment(1) as Card1Fragment
                val card2 = adapter.createFragment(2) as Card2Fragment

                cash.setByDate(from, to)
                card1.setByDate(from, to)
                card2.setByDate(from, to)
            }
            3->{
                val cash = adapter.createFragment(0) as CashFragment
                val card1 = adapter.createFragment(1) as Card1Fragment
                val card2 = adapter.createFragment(2) as Card2Fragment
                val card3 = adapter.createFragment(3) as Card3Fragment

                cash.setByDate(from, to)
                card1.setByDate(from, to)
                card2.setByDate(from, to)
                card3.setByDate(from, to)
            }
        }
    }

    fun callNotify(){
        when(mainActivity.viewPosition){
            0->{
                val fragment = adapter.createFragment(0) as CashFragment
                fragment.resetRecycler()
            }
            1->{
                val fragment = adapter.createFragment(1) as Card1Fragment
                fragment.resetRecycler()
            }
            2->{
                val fragment = adapter.createFragment(2) as Card2Fragment
                fragment.resetRecycler()
            }
            3->{
                val fragment = adapter.createFragment(3) as Card3Fragment
                fragment.resetRecycler()
            }
        }
    }

}