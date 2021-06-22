package com.eitu.arcornbook

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import com.eitu.arcornbook.databinding.FragmentFirstSetBinding

class FirstSetFragment : Fragment() {

    lateinit var mainActivity: MainActivity

    lateinit var binding:FragmentFirstSetBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentFirstSetBinding.inflate(inflater, container, false)

        mainActivity = activity as MainActivity

        setOnClick()

        setPage()

        setSwitch()

        return binding.root
    }

    private fun setOnClick(){
        binding.firstPage.start.setOnClickListener { //앱 첫 실행 시 나오는 화면의 시작하기 버튼 클릭 시
            val animation = AnimationUtils.loadAnimation(mainActivity, R.anim.pop_close)
            binding.firstPage.root.visibility = View.INVISIBLE
            binding.firstPage.root.animation = animation

            val animation2 = AnimationUtils.loadAnimation(mainActivity, R.anim.input_open)
            binding.inside.root.visibility = View.VISIBLE
            binding.inside.root.animation = animation2
        }

        binding.inside.cashlimit2.setOnClickListener { //현금 한도 설정 시
            mainActivity.openInput(0)
        }
        binding.inside.card1info.cardLimit.setOnClickListener { //1번 카드 한도 설정 시
            mainActivity.openInput(1)
        }
        binding.inside.card2info.cardLimit.setOnClickListener { //2번 카드 한도 설정 시
            mainActivity.openInput(2)
        }
        binding.inside.card3info.cardLimit.setOnClickListener { //3번 카드 한도 설정 시
            mainActivity.openInput(3)
        }

        binding.inside.finish.setOnClickListener{ //확인 버튼 클릭 시
            setFinish()
        }

    }

    //화면 요소 설정
    private fun setPage(){
        //앱 첫 실행 시 맨 위에 설정 이라는 텍스트뷰와 첫 페이지가 보이게 설정
        binding.inside.root.visibility = View.GONE
        binding.firstPage.root.visibility = View.VISIBLE
        binding.inside.title.visibility = View.GONE

        //현금 한도 기본값 0
        binding.inside.cashlimit2.text = "0"

        //카드 한도 기본값 0, 1번 카드만 보이게 설정
        binding.inside.card1sw.isChecked = true
        binding.inside.card2sw.isChecked = false
        binding.inside.card3sw.isChecked = false
        binding.inside.card1info.cardName.text = null
        binding.inside.card1info.cardLimit.text = "0"
        sw1Changed()
    }

    //확인 버튼 클릭 시
    private fun setFinish(){
        val cashLimit:String = binding.inside.cashlimit2.text.toString()

        val c1n:String = binding.inside.card1info.cardName.text.toString()
        val c1name:String = if(c1n == "") "카드1" else c1n //에디트 텍스트에 값이 없으면 카드1로 카드이름을 저장
        val c1limit:String = binding.inside.card1info.cardLimit.text.toString()

        val c2n:String = binding.inside.card2info.cardName.text.toString()
        val c2name:String = if(c2n == "") "카드2" else c2n //에디트 텍스트에 값이 없으면 카드2로 카드이름을 저장
        val c2limit:String = binding.inside.card2info.cardLimit.text.toString()

        val c3n:String = binding.inside.card3info.cardName.text.toString()
        val c3name:String = if(c3n == "") "카드3" else c3n //에디트 텍스트에 값이 없으면 카드3로 카드이름을 저장
        val c3limit:String = binding.inside.card3info.cardLimit.text.toString()

        var cardNum:Int = 0
        //설정된 카드가 몇개인지에 따라 넘겨주는 인수의 종류가 다르다
        when {
            binding.inside.card3sw.isChecked -> {
                cardNum = 3
                mainActivity.endSetting(cashLimit, cardNum, c1name, c1limit, c2name, c2limit, c3name, c3limit)
            }
            binding.inside.card2sw.isChecked -> {
                cardNum = 2
                mainActivity.endSetting(cashLimit, cardNum, c1name, c1limit, c2name, c2limit, "", "0")
            }
            binding.inside.card1sw.isChecked -> {
                cardNum = 1
                mainActivity.endSetting(cashLimit, cardNum, c1name, c1limit, "", "0", "", "0")
            }
            else -> {
                mainActivity.endSetting(cashLimit, cardNum, "", "0", "", "0", "", "0")
            }
        }
    }

    //스위치 클릭 시 실행되는 함수 시작
    private fun setSwitch(){
        binding.inside.card1sw.setOnCheckedChangeListener { _, _ ->
            sw1Changed()
        }
        binding.inside.card2sw.setOnCheckedChangeListener { _, _ ->
            sw2Changed()
        }
        binding.inside.card3sw.setOnCheckedChangeListener { _, _ ->
            sw3Changed()
        }
    }

    private fun sw1Changed(){
        if(binding.inside.card1sw.isChecked){
            binding.inside.card1info.root.visibility = View.VISIBLE
            binding.inside.card2sw.visibility = View.VISIBLE
            sw2Changed()
        }
        else{
            binding.inside.card1info.root.visibility = View.GONE
            binding.inside.card2sw.visibility = View.GONE
            binding.inside.card2sw.isChecked = false
            sw2Changed()
        }
    }
    private fun sw2Changed() {
        if (binding.inside.card2sw.isChecked) {
            binding.inside.card2info.root.visibility = View.VISIBLE
            binding.inside.card3sw.visibility = View.VISIBLE
            sw3Changed()
        } else {
            binding.inside.card2info.root.visibility = View.GONE
            binding.inside.card3sw.visibility = View.GONE
            binding.inside.card3sw.isChecked = false
            sw3Changed()
        }
    }
    private fun sw3Changed(){
        if(binding.inside.card3sw.isChecked){
            binding.inside.card3info.root.visibility = View.VISIBLE
        }
        else{
            binding.inside.card3info.root.visibility = View.GONE
        }
    }
    //스위치 클릭 시 실행되는 함수 끝

    //한도 입력 화면에서 넘어온 값을 해당하는 텍스트뷰에 설정하는 함수
    fun setValue(id:Int, howmuch:String){
        when(id){
            0->{binding.inside.cashlimit2.text = howmuch}
            1->{binding.inside.card1info.cardLimit.text = howmuch}
            2->{binding.inside.card2info.cardLimit.text = howmuch}
            3->{binding.inside.card3info.cardLimit.text = howmuch}
        }
    }

    //텍스트뷰 중복 클릭을 막기위한 함수
    fun turnTextView(id:Int){
        when(id){
            0->{binding.inside.cashlimit2.isClickable = !binding.inside.cashlimit2.isClickable}
            1->{binding.inside.card1info.cardLimit.isClickable = !binding.inside.card1info.cardLimit.isClickable}
            2->{binding.inside.card2info.cardLimit.isClickable = !binding.inside.card2info.cardLimit.isClickable}
            3->{binding.inside.card3info.cardLimit.isClickable = !binding.inside.card3info.cardLimit.isClickable}
        }
    }

}