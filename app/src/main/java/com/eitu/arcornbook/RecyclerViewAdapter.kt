package com.eitu.arcornbook

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.eitu.arcornbook.databinding.RecyclerItemBinding
import java.text.DecimalFormat

class RecyclerViewAdapter(fList:MutableList<BookItem>, val helper: SQLiteHelper, val activity: MainActivity):RecyclerView.Adapter<RecyclerViewAdapter.ReHolder>() {
    var list = fList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReHolder {
        val binding = RecyclerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ReHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ReHolder, position: Int) {
        val bookItem = list[position]
        holder.setReItem(bookItem)
    }

    inner class ReHolder(val binding:RecyclerItemBinding):RecyclerView.ViewHolder(binding.root){
        var item:BookItem? = null
        init {
            binding.deleteButton.setOnClickListener {
                showDialogD()
            }
            binding.itemLayout.setOnLongClickListener {
                showDialogU()
                return@setOnLongClickListener true
            }
        }
        fun setReItem(bookItem: BookItem){
            binding.Detail.text = bookItem.detail
            binding.Use.text = longToPrice(bookItem.price)
            binding.Pattern.text = bookItem.pattern
            when(bookItem.pattern){
                "식비"->{binding.patternImage.setImageResource(R.drawable.meal)}
                "쇼핑"->{binding.patternImage.setImageResource(R.drawable.shopping)}
                "교통비"->{binding.patternImage.setImageResource(R.drawable.car)}
                "취미·여가"->{binding.patternImage.setImageResource(R.drawable.hobby)}
                "병원비"->{binding.patternImage.setImageResource(R.drawable.hospital)}
                "기타"->{binding.patternImage.setImageResource(R.drawable.arcorn_moneybag)}
            }
            item = bookItem
        }
        private fun longToPrice(price:Long):String{
            var reText = price.toString()
            val dec = DecimalFormat("###,###")
            val changed = dec.format(reText.toInt())
            reText = changed.plus("원")

            return reText
        }
        private fun showDialogD(){
            val dialog = Dialog(activity)
            val dialogView = LayoutInflater.from(activity).inflate(R.layout.custom_dialog_delete_one, null)

            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(dialogView)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            dialog.show()

            dialogView.findViewById<Button>(R.id.negative).setOnClickListener {
                dialog.dismiss()
            }
            dialogView.findViewById<Button>(R.id.positive).setOnClickListener {
                dialog.dismiss()
                helper.deleteOne(item!!)
                list.remove(item!!)
                notifyDataSetChanged()
            }
        }
        private fun showDialogU(){
            val dialog = Dialog(activity)
            val dialogView = LayoutInflater.from(activity).inflate(R.layout.custom_dialog_update, null)

            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(dialogView)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            dialog.show()

            dialogView.findViewById<Button>(R.id.negative).setOnClickListener {
                dialog.dismiss()

            }
            dialogView.findViewById<Button>(R.id.positive).setOnClickListener {
                dialog.dismiss()
                activity.updateItem(item!!)
            }
        }
    }

}

