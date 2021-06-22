package com.eitu.arcornbook

import android.content.Context
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.eitu.arcornbook.databinding.FragmentCard2Binding
import java.text.SimpleDateFormat
import java.util.*

class Card2Fragment : Fragment() {

    lateinit var binding:FragmentCard2Binding

    lateinit var mainActivity:MainActivity
    lateinit var helper:SQLiteHelper
    lateinit var adapter: GroupAdapter
    private val groupItemList = mutableListOf<GroupItem>()
    private val list = mutableListOf<Long>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCard2Binding.inflate(inflater, container, false)

        adapter = GroupAdapter(mainActivity, helper)

        binding.recyclerView.layoutManager = LinearLayoutManager(mainActivity)
        binding.recyclerView.adapter = adapter

        setRecycler()

        return binding.root
    }

    fun resetRecycler(){
        for(index in list){
            groupItemList.add(GroupItem(index, helper.selectCashByDay(index)))
        }

        adapter.list = groupItemList
        adapter.notifyDataSetChanged()
    }

    fun setByDate(fDate:Long, tDate:Long){
        groupItemList.clear()

        list.clear()
        list.addAll(helper.selectDate("Card2", fDate, tDate))

        if(list.size == 0){
            binding.nothing.visibility = View.VISIBLE
        }
        else{
            binding.nothing.visibility = View.INVISIBLE
            for(index in list){
                groupItemList.add(GroupItem(index, helper.selectCashByDay(index)))
            }
        }
        adapter.list = groupItemList
        adapter.notifyDataSetChanged()
    }

    //리사이클러뷰 설정
    private fun setRecycler(){
        val now = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyMMdd", Locale.KOREA)
        val tDate = sdf.format(now.time).toLong()
        now.add(Calendar.DATE, -7)
        val fDate = sdf.format(now.time).toLong()

        list.addAll(helper.selectDate("Card2", fDate, tDate))
        if(list.size == 0){
            binding.nothing.visibility = View.VISIBLE
        }
        else{
            binding.nothing.visibility = View.INVISIBLE
            for(index in list){
                groupItemList.add(GroupItem(index, helper.selectCard2ByDay(index)))
            }
        }
        adapter.list = groupItemList
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
        helper = SQLiteHelper(context, "bookKeep", 1)
    }

}