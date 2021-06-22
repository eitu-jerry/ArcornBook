package com.eitu.arcornbook

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eitu.arcornbook.databinding.MainpageCardItemBinding

class CustomPagerAdapter: RecyclerView.Adapter<Holder>() {
    var infoList = listOf<CardInfo>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = MainpageCardItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return Holder(binding)
    }

    override fun getItemCount(): Int {
        return infoList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val info = infoList[position]
        holder.setInfo(info)
    }
}

class Holder(val binding:MainpageCardItemBinding):RecyclerView.ViewHolder(binding.root){
    fun setInfo(info:CardInfo){
        binding.Name.text = info.name
        binding.Limit.text = info.limit
        binding.Use.text = info.use
    }
}