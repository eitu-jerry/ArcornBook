package com.eitu.arcornbook

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.eitu.arcornbook.databinding.FragmentMainpageBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread

class MainPageFragment : Fragment() {

    lateinit var mainActivity: MainActivity
    lateinit var helper:SQLiteHelper

    private val adapter = CustomPagerAdapter()
    private val infoList = mutableListOf<CardInfo>()

    lateinit var binding: FragmentMainpageBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //바인딩
        binding = FragmentMainpageBinding.inflate(inflater, container, false)

        mainActivity = activity as MainActivity
        helper = SQLiteHelper(mainActivity, "bookKeep", 1)

        adapter.infoList = infoList
        binding.viewPager.adapter = adapter

        //현재 시간용
        var now = System.currentTimeMillis()
        var month = SimpleDateFormat("MM", Locale.KOREA).format(now)
        var day = SimpleDateFormat("dd", Locale.KOREA).format(now)

        val handler = object : Handler(Looper.getMainLooper()){
            override fun handleMessage(msg: Message) {
                now = System.currentTimeMillis()
                month = SimpleDateFormat("MM", Locale.KOREA).format(now)
                day = SimpleDateFormat("dd", Locale.KOREA).format(now)
                val time = SimpleDateFormat("HH:mm:ss", Locale.KOREA).format(now)
                val date = "${month}월${day}일"
                binding.date.text = date
                binding.time.text = time
            }
        }
        thread(start = true) {
            while(true){
                if(mainActivity.currentF == 0){
                    handler.sendEmptyMessage(1)
                }
                Thread.sleep(1000)
            }
        }

        setPage()

        return binding.root
    }

    //보여지는 화면 요소들의 값을 설정하는 함수
    fun setPage(){
        val sp = mainActivity.getSharedPreferences("isFirst", Context.MODE_PRIVATE)

        //오늘 날짜로 작성된 내역이 몇건인지 데이터베이스에서 가져옴
        val count = helper.selectTodayCount()
        val todayText = " (${count}건)"
        binding.todayOutHow.count.text = todayText

        //오늘 날짜로 작성된 내역의 합을 가져와서 표시
        var todayUse = 0L
        val todayList = helper.selectTodayUse()
        for(index in 0 until todayList.size){
            todayUse += todayList[index].price
        }
        binding.todayOutHow.price.text = mainActivity.intToPrice(todayUse.toInt())

        //오늘 날짜로 작성된 내역 중 최근 입력한 3건을 표시
        Log.d("size", "${todayList.size}")
        when(todayList.size){
            0->{
                binding.todayList.title1.visibility = View.GONE
                binding.todayList.price1.visibility = View.GONE
                binding.todayList.title2.visibility = View.GONE
                binding.todayList.price2.visibility = View.GONE
                binding.todayList.title3.visibility = View.GONE
                binding.todayList.price3.visibility = View.GONE
            }
            1->{
                //1번 항목
                val title = "${kindsToName(todayList[0].kinds)}  |  ${todayList[0].detail}"
                binding.todayList.title1.text = title
                binding.todayList.price1.text = mainActivity.intToPrice(todayList[0].price.toInt())

                binding.todayList.title1.visibility = View.VISIBLE
                binding.todayList.price1.visibility = View.VISIBLE
                binding.todayList.title2.visibility = View.GONE
                binding.todayList.price2.visibility = View.GONE
                binding.todayList.title3.visibility = View.GONE
                binding.todayList.price3.visibility = View.GONE
            }
            2->{
                //1번 항목
                var title = "${kindsToName(todayList[0].kinds)}  |  ${todayList[0].detail}"
                binding.todayList.title1.text = title
                binding.todayList.price1.text = mainActivity.intToPrice(todayList[0].price.toInt())
                //2번 항목
                title = "${kindsToName(todayList[1].kinds)}  |  ${todayList[1].detail}"
                binding.todayList.title2.text = title
                binding.todayList.price2.text = mainActivity.intToPrice(todayList[1].price.toInt())

                binding.todayList.title1.visibility = View.VISIBLE
                binding.todayList.price1.visibility = View.VISIBLE
                binding.todayList.title2.visibility = View.VISIBLE
                binding.todayList.price2.visibility = View.VISIBLE
                binding.todayList.title3.visibility = View.GONE
                binding.todayList.price3.visibility = View.GONE
            }
            else->{
                //1번 항목
                var title = "${kindsToName(todayList[0].kinds)}  |  ${todayList[0].detail}"
                binding.todayList.title1.text = title
                binding.todayList.price1.text = mainActivity.intToPrice(todayList[0].price.toInt())
                //2번 항목
                title = "${kindsToName(todayList[1].kinds)}  |  ${todayList[1].detail}"
                binding.todayList.title2.text = title
                binding.todayList.price2.text = mainActivity.intToPrice(todayList[1].price.toInt())
                //3번 항목
                title = "${kindsToName(todayList[2].kinds)}  |  ${todayList[2].detail}"
                binding.todayList.title3.text = title
                binding.todayList.price3.text = mainActivity.intToPrice(todayList[2].price.toInt())

                binding.todayList.title1.visibility = View.VISIBLE
                binding.todayList.price1.visibility = View.VISIBLE
                binding.todayList.title2.visibility = View.VISIBLE
                binding.todayList.price2.visibility = View.VISIBLE
                binding.todayList.title3.visibility = View.VISIBLE
                binding.todayList.price3.visibility = View.VISIBLE
            }
        }

        //현금 한도와 사용량을 가져와서 표시
        val cashLimit = sp.getInt("cashLimit", 0)
        val cashUse = helper.selectMonthCashUse().toInt()
        //현금 한도의 경우 0으로 저장되어 있으면 한도를 지정하지 않은 것으로 표시
        binding.cashInfo.Limit.text = if(cashLimit == 0) "한도 없음" else mainActivity.intToPrice(cashLimit)
        binding.cashInfo.Use.text = mainActivity.intToPrice(cashUse)

        //1번 카드의 이름과 한도를 가져옴 (현금 한도와 마찬가지로 0일 경우 한도를 지정하지 않은 것으로 표시)
        val c1name = sp.getString("card1Name", "")
        val c1l = sp.getInt("card1Limit", 0)
        val c1limit = if(c1l != 0) mainActivity.intToPrice(c1l) else "한도 없음"
        //2번 카드의 이름과 한도를 가져옴 (현금 한도와 마찬가지로 0일 경우 한도를 지정하지 않은 것으로 표시)
        val c2name = sp.getString("card2Name", "")
        val c2l = sp.getInt("card2Limit", 0)
        val c2limit = if(c2l != 0) mainActivity.intToPrice(c2l) else "한도 없음"
        //3번 카드의 이름과 한도를 가져옴 (현금 한도와 마찬가지로 0일 경우 한도를 지정하지 않은 것으로 표시)
        val c3name = sp.getString("card3Name", "")
        val c3l = sp.getInt("card3Limit", 0)
        val c3limit = if(c3l != 0) mainActivity.intToPrice(c3l) else "한도 없음"

        //카드 리스트 초기화
        infoList.clear()

        var info:CardInfo
        //카드 정보의 경우 뷰페이저를 통해 표현되기 때문에 각각 저장된 CardInfo 형식의 info를 infoList에 추가함
        when(sp.getInt("cardNum", 0)){
            0->{
                binding.viewPager.visibility = View.INVISIBLE
                binding.noCard.root.visibility = View.VISIBLE
            }
            1->{
                val c1Use = helper.selectMonthCard1Use().toInt()
                info = CardInfo(c1name!!, c1limit, mainActivity.intToPrice(c1Use))
                infoList.add(info)

                binding.viewPager.visibility = View.VISIBLE
                binding.noCard.root.visibility = View.INVISIBLE
            }
            2->{
                var cUse = helper.selectMonthCard1Use().toInt()
                info = CardInfo(c1name!!, c1limit, mainActivity.intToPrice(cUse))
                infoList.add(info)

                cUse = helper.selectMonthCard2Use().toInt()
                info = CardInfo(c2name!!, c2limit, mainActivity.intToPrice(cUse))
                infoList.add(info)

                binding.viewPager.visibility = View.VISIBLE
                binding.noCard.root.visibility = View.INVISIBLE
            }
            3->{
                var cUse = helper.selectMonthCard1Use().toInt()
                info = CardInfo(c1name!!, c1limit, mainActivity.intToPrice(cUse))
                infoList.add(info)

                cUse = helper.selectMonthCard2Use().toInt()
                info = CardInfo(c2name!!, c2limit, mainActivity.intToPrice(cUse))
                infoList.add(info)

                cUse = helper.selectMonthCard3Use().toInt()
                info = CardInfo(c3name!!, c3limit, mainActivity.intToPrice(cUse))
                infoList.add(info)

                binding.viewPager.visibility = View.VISIBLE
                binding.noCard.root.visibility = View.INVISIBLE
            }
        }
        //카드 리스트 갱신 알림
        adapter.notifyDataSetChanged()
    }

    private fun kindsToName(kinds:String):String{
        val sp = mainActivity.getSharedPreferences("isFirst", Context.MODE_PRIVATE)
        var name = ""
        when(kinds){
            "Cash"->{
                name = "현금"
            }
            "Card1"->{
                name = sp.getString("card1Name", "카드1")!!
            }
            "Card2"->{
                name = sp.getString("card2Name", "카드2")!!
            }
            "Card3"->{
                name = sp.getString("card3Name", "카드3")!!
            }
        }
        return name
    }

    override fun onStart() {
        super.onStart()
        setPage()
    }
}