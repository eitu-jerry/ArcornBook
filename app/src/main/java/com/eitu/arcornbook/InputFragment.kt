package com.eitu.arcornbook

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.eitu.arcornbook.databinding.FragmentInputBinding
import java.text.DecimalFormat

class InputFragment : Fragment() {

    lateinit var binding:FragmentInputBinding

    var mainActivity:MainActivity? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentInputBinding.inflate(inflater, container, false)

        binding.input.howmuch.text = "0원"

        val btlist = listOf<Button>(binding.input.bt0, binding.input.bt1,binding.input.bt2,binding.input.bt3,binding.input.bt4,
                binding.input.bt5,binding.input.bt6,binding.input.bt7,binding.input.bt8,binding.input.bt9)
        for(bt in btlist){
            bt.setOnClickListener {
                if(binding.input.howmuch.text == "0원"){
                    binding.input.howmuch.text = bt.text.toString().plus("원")
                }
                else{
                    val oriText = binding.input.howmuch.text.toString()
                    Log.d("reText", "문자열 가져옴 $oriText")

                    var reText = oriText.replace("원", "")
                    Log.d("reText", "원 제거 $reText")
                    reText = reText.replace(",", "")
                    Log.d("reText", "자릿수 구분 제거 $reText")

                    if(reText.length < 8){
                        val btT = bt.text.toString()
                        reText = reText.plus(btT)
                        Log.d("reText", "숫자 붙힘 $reText")
                    }

                    val dec = DecimalFormat("###,###")
                    val changed = dec.format(reText.toInt())
                    Log.d("changed", "바뀐 포맷 $changed")

                    reText = changed.plus("원")
                    Log.d("reText", "원 붙임 $reText")
                    binding.input.howmuch.text = reText
                }
            }
        }
        binding.input.back.setOnClickListener {
            if(binding.input.howmuch.text != "0원"){
                if(binding.input.howmuch.text.length != 2){
                    val oriText = binding.input.howmuch.text.toString()
                    Log.d("reText", "문자열 가져옴 $oriText")

                    var reText = oriText.replace("원", "")
                    Log.d("reText", "원 제거 $reText")
                    reText = reText.replace(",", "")
                    Log.d("reText", "자릿수 구분 제거 $reText")

                    reText = reText.substring(0, reText.length-1)
                    Log.d("reText", "마지막 숫자 지우기 $reText")

                    val dec = DecimalFormat("###,###")
                    val changed = dec.format(reText.toInt())
                    Log.d("changed", "바뀐 포맷 $changed")

                    reText = changed.plus("원")
                    Log.d("reText", "원 붙임 $reText")
                    binding.input.howmuch.text = reText
                }
                else binding.input.howmuch.text = "0원"
            }
        }
        binding.input.ok.setOnClickListener {
            val oriText = binding.input.howmuch.text.toString()
            Log.d("reText", "문자열 가져옴 $oriText")

            val reText = oriText.replace("원", "")
            if(mainActivity != null) { mainActivity?.endInput(reText) }
        }

        return binding.root
    }

    //숫자 입력 프래그먼트는 메인 액티비티와 북 액티비티 양쪽에서 모두 호출되기 때문에
    //어떤 액티비티에서 호출된 것인지 onAttach에서 구분
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is MainActivity) mainActivity = context
    }
}