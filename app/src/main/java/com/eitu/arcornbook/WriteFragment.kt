package com.eitu.arcornbook

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.eitu.arcornbook.databinding.FragmentWriteBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class WriteFragment : Fragment() {

    lateinit var mainActivity: MainActivity
    lateinit var binding:FragmentWriteBinding

    private var pattern = "식비"
    private var kinds = "Cash"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentWriteBinding.inflate(inflater, container, false)

        CoroutineScope(Dispatchers.Main).launch {
            launch {
                setSpinner()
            }.join()
            launch {
                setDate()
            }.join()
            launch {
                setOnClick()
            }
        }

        return binding.root
    }



    @SuppressLint("SimpleDateFormat")
    private fun setDate(){
        val now = Date(System.currentTimeMillis())
        val today = SimpleDateFormat("yy.MM.dd").format(now)
        binding.write.datePicker.text = today
    }

    //화면 구성 요소들의 클릭 메서드 정의
    @SuppressLint("SimpleDateFormat")
    private fun setOnClick(){
        val listener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            //형식화
            val year2 = year.toString().substring(2)
            val month2 = if(month<9) "0${month+1}" else "${month+1}"
            val day2 = if(dayOfMonth < 10) "0$dayOfMonth" else "$dayOfMonth"

            //텍스트 입력
            val dateText = "$year2.$month2.$day2"
            binding.write.datePicker.text = dateText
        }

        binding.write.datePicker.setOnClickListener {
            val now = Date(System.currentTimeMillis())

            val year = SimpleDateFormat("yyyy").format(now).toInt()
            val month = SimpleDateFormat("MM").format(now).toInt()
            val day = SimpleDateFormat("dd").format(now).toInt()

            val dialog = DatePickerDialog(mainActivity, listener, year, month-1, day)
            dialog.datePicker.maxDate = now.time
            dialog.show()
        }

        binding.write.price.setOnClickListener {
            mainActivity.openWriteInput()
        }

        binding.write.endWrite.setOnClickListener {
            val detail = binding.write.Detail.text.toString()
            val price = mainActivity.toLong(binding.write.price.text.toString())
            val date = binding.write.datePicker.text.toString().replace(".", "").toLong()
            if(detail != "" && price != 0L){
                mainActivity.endWrite(kinds, pattern, detail, price, date)
            }

        }
    }

    //카테고리 스피너 정의
    private fun setSpinner(){
        val nameList = listOf<String>("식비", "쇼핑", "교통비", "취미·여가", "병원비", "기타")
        val imageList = listOf<Int>(R.drawable.meal, R.drawable.shopping, R.drawable.car, R.drawable.hobby, R.drawable.hospital, R.drawable.arcorn_moneybag)
        val spinnerItem = mutableListOf<SpinnerItem>()

        for(index in nameList.indices){
            spinnerItem.add(SpinnerItem(imageList[index], nameList[index]))
        }

        val adapter = SpinnerAdapter(mainActivity, R.layout.spinner_view2, spinnerItem)

        binding.write.spinner.adapter = adapter
        binding.write.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val item = binding.write.spinner.getItemAtPosition(position) as SpinnerItem
                pattern = item.name
            }
        }

        setKindsSpinner()
    }

    private fun setKindsSpinner(){
        val list = mainActivity.returnCardName()
        val adapter = ArrayAdapter<String>(mainActivity, R.layout.spinner_view, list)
        binding.write.spinnerKinds.adapter = adapter
        binding.write.spinnerKinds.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
            ) {
                val item = list[position]
                kinds = item
            }
        }

        kinds = list[mainActivity.returnViewPosition()]
        binding.write.spinnerKinds.setSelection(mainActivity.returnViewPosition())
    }

    fun setPrice(text:String){
        binding.write.price.text = text
    }

    override fun onResume() {
        super.onResume()
        Log.d("pageselected", "${mainActivity.returnViewPosition()}")
        binding.write.spinnerKinds.setSelection(mainActivity.returnViewPosition())
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }
}