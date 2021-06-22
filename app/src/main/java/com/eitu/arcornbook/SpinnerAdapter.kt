package com.eitu.arcornbook

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.eitu.arcornbook.databinding.SpinnerView2Binding

class SpinnerAdapter(context: Context, val id:Int, val list: MutableList<SpinnerItem>):ArrayAdapter<SpinnerItem>(context, id, list ){
    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(position: Int): SpinnerItem? {
        return list[position]
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = SpinnerView2Binding.inflate(LayoutInflater.from(parent.context), parent, false)

        val model = list[position]
        binding.imageView.setImageResource(model.image)
        binding.textView.text = model.name


        return binding.root
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = SpinnerView2Binding.inflate(LayoutInflater.from(parent.context), parent, false)

        val model = list[position]
        binding.imageView.setImageResource(model.image)
        binding.textView.text = model.name

        return binding.root
    }
}