package com.eitu.arcornbook

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.eitu.arcornbook.databinding.FragmentSettingBinding

class SettingFragment : Fragment() {

    lateinit var mainActivity: MainActivity

    lateinit var binding: FragmentSettingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //바인딩
        binding = FragmentSettingBinding.inflate(inflater, container, false)

        mainActivity = activity as MainActivity

        setOnClick()

        setPage()

        setSwitch()

        return binding.root
    }



    private fun setOnClick(){
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
        val sp = mainActivity.getSharedPreferences("isFirst", Context.MODE_PRIVATE)!!

        //저장된 현금 한도값을 가져와서 형식화하여 표시
        binding.inside.cashlimit2.text = mainActivity.intToDecimal(sp.getInt("cashLimit", 0))

        //설정된 카드 갯수에 따라 카드 이름, 카드 한도, 스위치의 온오프 여부를 표시
        when(sp.getInt("cardNum", 1)){
            1->{binding.inside.card1sw.isChecked = true
                binding.inside.card1info.cardName.setText(mainActivity.callCName(1))
                binding.inside.card1info.cardLimit.text = mainActivity.intToDecimal(sp.getInt("card1Limit", 0))
                sw1Changed()
            }
            2->{binding.inside.card1sw.isChecked = true
                binding.inside.card2sw.isChecked = true
                binding.inside.card1info.cardName.setText(mainActivity.callCName(1))
                binding.inside.card1info.cardLimit.text = mainActivity.intToDecimal(sp.getInt("card1Limit", 0))
                binding.inside.card2info.cardName.setText(mainActivity.callCName(2))
                binding.inside.card2info.cardLimit.text = mainActivity.intToDecimal(sp.getInt("card2Limit", 0))
                sw1Changed()
            }
            3->{binding.inside.card1sw.isChecked = true
                binding.inside.card2sw.isChecked = true
                binding.inside.card3sw.isChecked = true
                binding.inside.card1info.cardName.setText(mainActivity.callCName(1))
                binding.inside.card1info.cardLimit.text = mainActivity.intToDecimal(sp.getInt("card1Limit", 0))
                binding.inside.card2info.cardName.setText(mainActivity.callCName(2))
                binding.inside.card2info.cardLimit.text = mainActivity.intToDecimal(sp.getInt("card2Limit", 0))
                binding.inside.card3info.cardName.setText(mainActivity.callCName(3))
                binding.inside.card3info.cardLimit.text = mainActivity.intToDecimal(sp.getInt("card3Limit", 0))
                sw1Changed()
            }
            else->{binding.inside.card1sw.isChecked = false
                binding.inside.card2sw.isChecked = false
                binding.inside.card3sw.isChecked = false
                sw1Changed()
            }
        }
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