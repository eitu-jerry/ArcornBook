package com.eitu.arcornbook

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.eitu.arcornbook.databinding.FragmentReportBinding
import java.text.SimpleDateFormat
import java.util.*

class ReportFragment : Fragment() {

    lateinit var mainActivity: MainActivity
    lateinit var helper:SQLiteHelper

    lateinit var binding:FragmentReportBinding

    var currentYear = 0L
    var currentMonth = 0L

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentReportBinding.inflate(inflater, container, false)

        mainActivity = activity as MainActivity
        helper = SQLiteHelper(mainActivity, "bookKeep", 1)

        setPicker()

        setOnClick()

        setPage()

        return binding.root
    }

    private fun setPicker(){
        val now = Calendar.getInstance()
        val year = SimpleDateFormat("yy", Locale.KOREA).format(now.time).toInt()
        currentYear = year.toLong()
        binding.monthReport.datePicker.yearPicker.minValue = 16
        binding.monthReport.datePicker.yearPicker.maxValue = year
        binding.monthReport.datePicker.yearPicker.value = year
        /*binding.monthReport.datePicker.yearPicker.setOnValueChangedListener { picker, oldVal, newVal ->
            currentYear = oldVal.toLong()
            Log.d("currentYear", "$currentYear")
        }*/
        val month = SimpleDateFormat("MM", Locale.KOREA).format(now.time).toInt()
        currentMonth = month.toLong()
        binding.monthReport.datePicker.monthPicker.minValue = 1
        binding.monthReport.datePicker.monthPicker.maxValue = 12
        binding.monthReport.datePicker.monthPicker.value = month
        /*binding.monthReport.datePicker.monthPicker.setOnValueChangedListener { picker, oldVal, newVal ->
            currentMonth = oldVal.toLong()
            Log.d("currentMonth", "$currentMonth")
        }*/
    }

    private fun setOnClick(){
        binding.monthReport.datePicker.go.setOnClickListener {
            currentYear = binding.monthReport.datePicker.yearPicker.value.toLong()
            currentMonth = binding.monthReport.datePicker.monthPicker.value.toLong()
            Log.d("currentYear", "$currentYear")
            Log.d("currentMonth", "$currentMonth")
            val month = currentYear*10000L + currentMonth*100L
            Log.d("month", "$month")
            setList(month)
        }
    }

    private fun setList(month:Long){
        //식비에 해당하는 내역의 건수와 그 합을 가져와서 표시 (Long배열로 값을 받아오는데 [0]에는 건수 [1]에는 총합이 포함)
        val eat = helper.selectByPattern("식비", month)
        binding.monthReport.eatText.Text.text = "식비 총 "
        binding.monthReport.eatText.count.text = toCount(eat[0])
        binding.monthReport.eatText.Price.text = mainActivity.intToPrice(eat[1].toInt())

        //쇼핑에 해당하는 내역의 건수와 그 합을 가져와서 표시 (Long배열로 값을 받아오는데 [0]에는 건수 [1]에는 총합이 포함)
        val shopping = helper.selectByPattern("쇼핑", month)
        binding.monthReport.shoppingText.Text.text = "쇼핑 총 "
        binding.monthReport.shoppingText.count.text = toCount(shopping[0])
        binding.monthReport.shoppingText.Price.text = mainActivity.intToPrice(shopping[1].toInt())

        //교통비에 해당하는 내역의 건수와 그 합을 가져와서 표시 (Long배열로 값을 받아오는데 [0]에는 건수 [1]에는 총합이 포함)
        val car = helper.selectByPattern("교통비", month)
        binding.monthReport.carText.Text.text = "교통비 총 "
        binding.monthReport.carText.count.text = toCount(car[0])
        binding.monthReport.carText.Price.text = mainActivity.intToPrice(car[1].toInt())

        //취미,여가비에 해당하는 내역의 건수와 그 합을 가져와서 표시 (Long배열로 값을 받아오는데 [0]에는 건수 [1]에는 총합이 포함)
        val hobby = helper.selectByPattern("취미·여가", month)
        binding.monthReport.hobbyText.Text.text = "취미·여가 총 "
        binding.monthReport.hobbyText.count.text = toCount(hobby[0])
        binding.monthReport.hobbyText.Price.text = mainActivity.intToPrice(hobby[1].toInt())

        //병원비에 해당하는 내역의 건수와 그 합을 가져와서 표시 (Long배열로 값을 받아오는데 [0]에는 건수 [1]에는 총합이 포함)
        val hospital = helper.selectByPattern("병원비", month)
        binding.monthReport.hosText.Text.text = "병원비 총 "
        binding.monthReport.hosText.count.text = toCount(hospital[0])
        binding.monthReport.hosText.Price.text = mainActivity.intToPrice(hospital[1].toInt())

        //기타에 해당하는 내역의 건수와 그 합을 가져와서 표시 (Long배열로 값을 받아오는데 [0]에는 건수 [1]에는 총합이 포함)
        val etc = helper.selectByPattern("기타", month)
        binding.monthReport.etcText.Text.text = "기타 총 "
        binding.monthReport.etcText.count.text = toCount(etc[0])
        binding.monthReport.etcText.Price.text = mainActivity.intToPrice(etc[1].toInt())
    }

    //페이지 요소들 설정 함수
    private fun setPage(){
        val now = System.currentTimeMillis()

        //현재 몇월인지 가져옴
        val m = SimpleDateFormat("MM", Locale.KOREA).format(now).toLong()
        val titleText = "도토리 묻은 곳"
        binding.monthReport.title.text = titleText

        //데이터베이스에서 이번 달에 해당하는 내역들을 가져와야하기 때문에 연,월 까지 가져온 후 100을 곱하여 00년00월00의 형식으로 만듬
        val month = SimpleDateFormat("yyMM", Locale.KOREA).format(now).toLong()*100

        setList(month)

        /*
        //식비에 해당하는 내역의 건수와 그 합을 가져와서 표시 (Long배열로 값을 받아오는데 [0]에는 건수 [1]에는 총합이 포함)
        val eat = helper.selectByPattern("식비", month)
        binding.monthReport.eatText.Text.text = "식비 총 "
        binding.monthReport.eatText.count.text = toCount(eat[0])
        binding.monthReport.eatText.Price.text = mainActivity.intToPrice(eat[1].toInt())

        //쇼핑에 해당하는 내역의 건수와 그 합을 가져와서 표시 (Long배열로 값을 받아오는데 [0]에는 건수 [1]에는 총합이 포함)
        val shopping = helper.selectByPattern("쇼핑", month)
        binding.monthReport.shoppingText.Text.text = "쇼핑 총 "
        binding.monthReport.shoppingText.count.text = toCount(shopping[0])
        binding.monthReport.shoppingText.Price.text = mainActivity.intToPrice(shopping[1].toInt())

        //교통비에 해당하는 내역의 건수와 그 합을 가져와서 표시 (Long배열로 값을 받아오는데 [0]에는 건수 [1]에는 총합이 포함)
        val car = helper.selectByPattern("교통비", month)
        binding.monthReport.carText.Text.text = "교통비 총 "
        binding.monthReport.carText.count.text = toCount(car[0])
        binding.monthReport.carText.Price.text = mainActivity.intToPrice(car[1].toInt())

        //취미,여가비에 해당하는 내역의 건수와 그 합을 가져와서 표시 (Long배열로 값을 받아오는데 [0]에는 건수 [1]에는 총합이 포함)
        val hobby = helper.selectByPattern("취미·여가", month)
        binding.monthReport.hobbyText.Text.text = "취미·여가 총 "
        binding.monthReport.hobbyText.count.text = toCount(hobby[0])
        binding.monthReport.hobbyText.Price.text = mainActivity.intToPrice(hobby[1].toInt())

        //병원비에 해당하는 내역의 건수와 그 합을 가져와서 표시 (Long배열로 값을 받아오는데 [0]에는 건수 [1]에는 총합이 포함)
        val hospital = helper.selectByPattern("병원비", month)
        binding.monthReport.hosText.Text.text = "병원비 총 "
        binding.monthReport.hosText.count.text = toCount(hospital[0])
        binding.monthReport.hosText.Price.text = mainActivity.intToPrice(hospital[1].toInt())

        //기타에 해당하는 내역의 건수와 그 합을 가져와서 표시 (Long배열로 값을 받아오는데 [0]에는 건수 [1]에는 총합이 포함)
        val etc = helper.selectByPattern("기타", month)
        binding.monthReport.etcText.Text.text = "기타 총 "
        binding.monthReport.etcText.count.text = toCount(etc[0])
        binding.monthReport.etcText.Price.text = mainActivity.intToPrice(etc[1].toInt())
        */
    }

    //건수에 해당하는 Long값을 받아와서 문자열로 반환하는 함수
    private fun toCount(c:Long):String{
        return "${c}건"
    }

}