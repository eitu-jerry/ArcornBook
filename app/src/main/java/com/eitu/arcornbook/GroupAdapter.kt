package com.eitu.arcornbook

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eitu.arcornbook.databinding.GroupedItemBinding

class GroupAdapter(activity: MainActivity, val helper: SQLiteHelper):RecyclerView.Adapter<GroupAdapter.GroupHolder>(){
    val mainActivity = activity
    var list = mutableListOf<GroupItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupHolder {
        val binding = GroupedItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return GroupHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: GroupHolder, position: Int) {
        val group = list[position]
        holder.setView(group)
    }

    inner class GroupHolder(val binding:GroupedItemBinding):RecyclerView.ViewHolder(binding.root){
        fun setView(groupItem: GroupItem){
            binding.Date.text = dateToString(groupItem.date)
            binding.recyclerView.adapter = RecyclerViewAdapter(groupItem.itemList, helper, mainActivity)
            binding.recyclerView.layoutManager = LinearLayoutManager(mainActivity)
        }
        private fun dateToString(date:Long):String {
            val text1 = date.toString()

            return text1.substring(0, 2) + "." + text1.substring(2, 4) + "." + text1.substring(4)
        }
    }

}
