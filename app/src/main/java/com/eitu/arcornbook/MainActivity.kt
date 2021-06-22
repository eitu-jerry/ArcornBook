package com.eitu.arcornbook

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.view.animation.AlphaAnimation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.eitu.arcornbook.databinding.ActivityMainBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    //바인딩 설정
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    //데이터베이스 헬퍼
    private val helper = SQLiteHelper(this, "bookKeep", 1)

    //뷰페이저에 연결되는 탭 리스트
    private val tabTiles = mutableListOf<String>()

    //뷰페이저에 표시될 프래그먼트 리스트
    private val list = mutableListOf<Fragment>()

    //액티비티 프래그먼트
    private lateinit var firstSet:FirstSetFragment
    private lateinit var mainPage:MainPageFragment
    private lateinit var book:BookFragment
    private lateinit var report:ReportFragment
    private lateinit var settingList:SettingListFragment
    private lateinit var setting:SettingFragment
    private lateinit var setInput:InputFragment
    private lateinit var writeFragment:WriteFragment
    private lateinit var writeInput:InputFragment
    private lateinit var updateFragment:UpdateFragment

    //프래그먼트들에 영향을 주는 변수
    private var whichTextView = 0
    var viewPosition = 0
    var currentF = 0
    var eveF = 0
    var tabSelect = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        GlobalScope.launch {
            returnFragmentList()
            returnCardName()

            setOnClick()

            setTabImage()
        }
        CoroutineScope(Dispatchers.Main).launch {
            clickHome()

            checkFirst()
        }
    }


    //탭 클릭 시 실행하는 함수 부분 시작
    private fun setOnClick(){
        binding.tab.home.setOnClickListener {
            clickHome()
        }
        binding.tab.homeText.setOnClickListener {
            clickHome()
        }
        binding.tab.book.setOnClickListener {
            clickBook()
        }
        binding.tab.bookText.setOnClickListener {
            clickBook()
        }
        binding.tab.report.setOnClickListener {
            clickReport()
        }
        binding.tab.reportText.setOnClickListener {
            clickReport()
        }
        binding.tab.setting.setOnClickListener {
            clickSetting()
        }
        binding.tab.settingText.setOnClickListener {
            clickSetting()
        }
        binding.write.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                openWrite()
            }
        }
    }

    //탭에서 홈 누를때
    private fun clickHome(){
        binding.write.visibility = View.VISIBLE

        currentF = 0

        tabSelect = 0
        setTabImage()

        CoroutineScope(Dispatchers.Main).launch {
            val sfm = supportFragmentManager
            mainPage = MainPageFragment()
            val transaction = sfm.beginTransaction()
            transaction.replace(R.id.frameL, mainPage, "mainPage")
            transaction.commit()

            sfm.executePendingTransactions()
        }
    }
    //탭에서 내역 누를때
    private fun clickBook(){
        binding.write.visibility = View.VISIBLE

        currentF = 1

        tabSelect = 1
        setTabImage()

        CoroutineScope(Dispatchers.Main).launch {
            val sfm = supportFragmentManager
            book = BookFragment()
            val transaction = sfm.beginTransaction()
            transaction.replace(R.id.frameL, book, "book")
            transaction.commit()

            sfm.executePendingTransactions()
        }
    }
    //탭에서 리포트 누를때
    private fun clickReport(){
        binding.write.visibility = View.GONE

        currentF = 2

        tabSelect = 2
        setTabImage()

        CoroutineScope(Dispatchers.Main).launch {
            val sfm = supportFragmentManager
            report = ReportFragment()
            val transaction = sfm.beginTransaction()
            transaction.replace(R.id.frameL, report, "report")
            transaction.commit()

            sfm.executePendingTransactions()
        }
    }
    //탭에서 설정 누를때
    private fun clickSetting(){
        binding.write.visibility = View.GONE

        currentF = 3

        tabSelect = 3
        setTabImage()

        CoroutineScope(Dispatchers.Main).launch {
            val sfm = supportFragmentManager
            settingList = SettingListFragment()
            val transaction = sfm.beginTransaction()
            transaction.replace(R.id.frameL, settingList, "settingList")
            transaction.commit()

            sfm.executePendingTransactions()
        }
    }
    //각 탭을 눌렀을때 이미지 변화
    private fun setTabImage(){
        when(tabSelect){
            0->{
                binding.tab.home.setImageResource(R.drawable.home_sel)
                binding.tab.book.setImageResource(R.drawable.chart_unsel)
                binding.tab.report.setImageResource(R.drawable.chart_unsel)
                binding.tab.setting.setImageResource(R.drawable.set_unsel)
            }
            1->{
                binding.tab.home.setImageResource(R.drawable.home_unsel)
                binding.tab.book.setImageResource(R.drawable.chart_sel)
                binding.tab.report.setImageResource(R.drawable.chart_unsel)
                binding.tab.setting.setImageResource(R.drawable.set_unsel)
            }
            2->{
                binding.tab.home.setImageResource(R.drawable.home_unsel)
                binding.tab.book.setImageResource(R.drawable.chart_unsel)
                binding.tab.report.setImageResource(R.drawable.chart_sel)
                binding.tab.setting.setImageResource(R.drawable.set_unsel)
            }
            3->{
                binding.tab.home.setImageResource(R.drawable.home_unsel)
                binding.tab.book.setImageResource(R.drawable.chart_unsel)
                binding.tab.report.setImageResource(R.drawable.chart_unsel)
                binding.tab.setting.setImageResource(R.drawable.set_sel)
            }
        }
    }
    //탭 클릭 시 실행하는 함수 부분 끝

    //앱을 처음 실행하는지 확인하는 함수
    private fun checkFirst(){
        val sp = getSharedPreferences("isFirst", Context.MODE_PRIVATE)

        if(sp.getBoolean("isFirst", true)){
            currentF = 9

            openFirstFrame()

            binding.frameL.visibility = View.GONE

            val sfm = supportFragmentManager
            firstSet = FirstSetFragment()
            val transaction = sfm.beginTransaction()
            transaction.add(R.id.frameL3, firstSet, "firstSetting")
            transaction.commit()

            sfm.executePendingTransactions()
        }
        else {
            binding.intro.root.visibility = View.VISIBLE
            val animation = AlphaAnimation(1.0f, 0.0f)
            animation.startOffset = 1500
            animation.duration = 500
            binding.intro.root.visibility = View.INVISIBLE
            binding.intro.root.animation = animation
        }

    }

    //설정 화면 프래그먼트 종료 시 호출되는 함수
    fun endSetting(cashLimit:String, cardNum:Int,
                   c1name:String, c1limit:String, c2name:String, c2limit:String, c3name:String, c3limit:String){
        val sp = getSharedPreferences("isFirst", Context.MODE_PRIVATE)
        val editor = sp.edit()

        editor.putBoolean("isFirst", false) //앱 첫 실행 시 설정을 완료하면 isFirst를 false로 함

        //카드 갯수, 현금 한도, 카드 이름, 카드 한도를 저장
        editor.putInt("cardNum", cardNum)
        editor.putInt("cashLimit", toInt(cashLimit))
        editor.putString("card1Name", c1name)
        editor.putInt("card1Limit", toInt(c1limit))
        editor.putString("card2Name", c2name)
        editor.putInt("card2Limit", toInt(c2limit))
        editor.putString("card3Name", c3name)
        editor.putInt("card3Limit", toInt(c3limit))

        //에디터 값을 적용함
        editor.apply()

        if(currentF == 9){ //첫 실행 시 설정 화면인지
            currentF = 0
            removeFirstFrame()
            clickHome()
        }
        else{              //일반적인 설정 화면인지
            onBackPressed()
        }
    }

    fun openSetting(){
        currentF = 4

        CoroutineScope(Dispatchers.Main).launch {
            val sfm = supportFragmentManager
            setting = SettingFragment()
            val transaction = sfm.beginTransaction()
            transaction.setCustomAnimations(R.anim.input_open, R.anim.input_close, R.anim.input_open, R.anim.input_close)
            transaction.add(R.id.frameL, setting, "setting")
            transaction.addToBackStack(null)
            transaction.commit()

            sfm.executePendingTransactions()
        }
    }



    //설정 화면에서 한도 설정 시 뜨는 값 입력 프래그먼트 설정
    fun openInput(id:Int){
        currentF = if(currentF == 4) 5 else 10 //앱 첫 실행 후 설정화면에서 호출된것인지 일반 설정인지 구분하기 위함
        //일반 설정 -> 5
        //첫 실행 설정 -> 10

        when(id){
            0->{whichTextView=id}
            1->{whichTextView=id}
            2->{whichTextView=id}
            3->{whichTextView=id}
            else->{}
        }

        if(currentF == 10){
            val fragment = supportFragmentManager.findFragmentByTag("firstSetting") as FirstSetFragment?
            fragment?.turnTextView(whichTextView) //텍스트 뷰 클릭한 후 종료 전까지 중복 클릭을 방지하기 위함
        }
        else{
            val fragment = supportFragmentManager.findFragmentByTag("setting") as SettingFragment?
            fragment?.turnTextView(whichTextView) //텍스트 뷰 클릭한 후 종료 전까지 중복 클릭을 방지하기 위함
        }

        openFrame() //입력 프래그먼트가 포함되는 프레임 가시화

        val sfm = supportFragmentManager
        setInput = InputFragment()
        val transaction = sfm.beginTransaction()
        transaction.setCustomAnimations(R.anim.input_open, R.anim.input_close, R.anim.input_open, R.anim.input_close)
        transaction.add(R.id.frameL2, setInput)
        transaction.addToBackStack(null)
        transaction.commit()

        sfm.executePendingTransactions()
    }

    //입력 화면 프래그먼트 종료 시 호출되는 함수
    fun endInput(howmuch:String){
        when (currentF) {
            11 -> {
                val fragment = supportFragmentManager.findFragmentByTag("firstSetting") as FirstSetFragment?
                fragment?.setValue(whichTextView, howmuch)
            }
            9->{
                val fragment = supportFragmentManager.findFragmentByTag("updateFragment") as UpdateFragment?
                fragment?.setPrice(howmuch+"원")
            }
            7 -> {
                val fragment = supportFragmentManager.findFragmentByTag("write") as WriteFragment?
                fragment?.setPrice(howmuch+"원")
            }
            5 -> {
                val fragment = supportFragmentManager.findFragmentByTag("setting") as SettingFragment?
                fragment?.setValue(whichTextView, howmuch)
            }
        }
        onBackPressed()
    }



    //화면 오른쪽 아래 작성 버튼 클릭 시 실행되는 함수
    private fun openWrite(){
        eveF = currentF

        currentF = 6

        val sfm = supportFragmentManager
        val transaction = sfm.beginTransaction()

        CoroutineScope(Dispatchers.Main).launch {
            launch {
                Log.d("coroutine1", "here")
                writeFragment = WriteFragment()
                transaction.setCustomAnimations(R.anim.write_open, R.anim.write_close, R.anim.write_open, R.anim.write_close)
                transaction.add(R.id.frameL2, writeFragment, "write")
                transaction.addToBackStack(null)
            }.join()
            launch {
                Log.d("coroutine2", "here")
                //작성 화면이 표시될 프레임 보이게 설정
                openFrame()
                transaction.commit()
                sfm.executePendingTransactions()
            }
        }
    }

    fun endWrite(kinds:String, pattern:String, detail:String, price:Long, date:Long){
        val sp = getSharedPreferences("isFirst", Context.MODE_PRIVATE)

        //데이터베이스에 분류 열에 저장될 값 구함
        val dbKinds:String = when(kinds){
            sp.getString("card1Name", "카드1")->{
                "Card1"
            }
            sp.getString("card2Name", "카드2")->{
                "Card2"
            }
            sp.getString("card3Name", "카드3")->{
                "Card3"
            }
            else->{
                "Cash"
            }
        }

        //값을 데이터화 하여 데이터베이스에 저장
        val item = BookItem(null, dbKinds, pattern, detail, price, date)
        helper.insertItem(item)

        onBackPressed()
    }
    //작성 종료 시 실행되는 함수



    //숫자 입력 프래그먼트를 여는 함수
    fun openWriteInput(){
        currentF = if(currentF == 6) 7 else 9 //내역 입력인지 내역 수정인지에 따라 구분
        //내역 입력 -> 7
        //내역 수정 -> 9

        val sfm = supportFragmentManager
        writeInput = InputFragment()
        val transaction = sfm.beginTransaction()
        transaction.setCustomAnimations(R.anim.input_open, R.anim.input_close, R.anim.input_open, R.anim.input_close)
        transaction.add(R.id.frameL2, writeInput, "writeInput")
        transaction.addToBackStack(null)
        transaction.commit()

        sfm.executePendingTransactions()
    }



    //내역 수정 페이지
    fun updateItem(item:BookItem){
        currentF = 8

        openFrame()

        val sfm = supportFragmentManager
        updateFragment = UpdateFragment()
        val transaction = sfm.beginTransaction()
        transaction.setCustomAnimations(R.anim.write_open, R.anim.write_close, R.anim.write_open, R.anim.write_close)
        transaction.add(R.id.frameL2, updateFragment, "updateFragment")
        transaction.addToBackStack("updateFragment")
        transaction.commit()

        sfm.executePendingTransactions()

        val upfrag = sfm.findFragmentByTag("updateFragment") as UpdateFragment?
        Log.d("upfrag", "$upfrag")
        upfrag?.setItem(item)
    }

    fun endUpdate(item:BookItem){
        helper.updateItem(item)
        onBackPressed()
        val fragment = supportFragmentManager.findFragmentByTag("book") as BookFragment?
        fragment?.callNotify()
    }
    //내역 수정 종료



    //정수값을 형식화된 문자열로 변경하여 리턴
    fun intToPrice(price:Int):String{
        val dec = DecimalFormat("###,###")
        val changed = dec.format(price)

        return changed.plus("원")
    }

    //정수값을 형식화된 문자열로 변경하여 리턴 (단, 원을 붙이지 않음)
    fun intToDecimal(price:Int):String{
        val dec = DecimalFormat("###,###")

        return dec.format(price)
    }

    //형식화된 문자열을 정수로 변환하여 리턴
    private fun toInt(oriText:String):Int{
        return oriText.replace(",", "").replace("원", "").toInt()
    }

    //형식화된 문자열을 Long으로 변환하여 반환하는 함수
    fun toLong(oriText:String):Long{
        return oriText.replace(",", "").replace("원", "").toLong()
    }

    //요청된 값에 따라 저장된 카드 이름을 리턴
    fun callCName(which:Int):String{
        val sp = getSharedPreferences("isFirst", Context.MODE_PRIVATE)
        var cName = ""
        when(which){
            1->{ cName = sp.getString("card1Name", "카드1").toString() }
            2->{ cName = sp.getString("card2Name", "카드2").toString() }
            3->{ cName = sp.getString("card3Name", "카드3").toString() }
        }
        return cName
    }

    //뷰페이저 프래그먼트 리스트 반환
    fun returnFragmentList():MutableList<Fragment>{
        val sp = getSharedPreferences("isFirst", Context.MODE_PRIVATE)

        list.clear()

        val id = sp.getInt("cardNum", 0)
        val cashFragment = CashFragment()
        val card1Fragment = Card1Fragment()
        val card2Fragment = Card2Fragment()
        val card3Fragment = Card3Fragment()
        when(id){
            0->{
                list.add(cashFragment)
            }
            1->{
                list.add(cashFragment)
                list.add(card1Fragment)
            }
            2->{
                list.add(cashFragment)
                list.add(card1Fragment)
                list.add(card2Fragment)
            }
            3->{
                list.add(cashFragment)
                list.add(card1Fragment)
                list.add(card2Fragment)
                list.add(card3Fragment)
            }
        }
        return list
    }

    //작성 화면의 스피너에 표시될 카드 이름 리스트를 반환하는 함수
    fun returnCardName():MutableList<String>{
        val sp = getSharedPreferences("isFirst", Context.MODE_PRIVATE)

        tabTiles.clear()

        tabTiles.add("현금")
        when (list.size) {
            2 -> {
                val cardName = sp.getString("card1Name", "카드1")!!
                tabTiles.add(cardName)
            }
            3 -> {
                var cardName = sp.getString("card1Name", "카드1")!!
                tabTiles.add(cardName)
                cardName = sp.getString("card2Name", "카드2")!!
                tabTiles.add(cardName)
            }
            4 -> {
                var cardName = sp.getString("card1Name", "카드1")!!
                tabTiles.add(cardName)
                cardName = sp.getString("card2Name", "카드2")!!
                tabTiles.add(cardName)
                cardName = sp.getString("card3Name", "카드3")!!
                tabTiles.add(cardName)
            }
        }

        return tabTiles
    }

    //현재 뷰페이저 포지션 반환 함수
    fun returnViewPosition():Int{
        return viewPosition
    }

    //숫자 입력 프래그먼트가 열릴 프레임을 보이게 설정
    private fun openFrame(){
        val animation = AlphaAnimation(0f,1f)
        animation.duration = 500
        binding.frameL2.visibility = View.VISIBLE
        binding.frameL2.animation = animation
    }

    //숫자 입력 프래그먼트가 열린 프레임을 보이지 않게 설정
    private fun removeFrame(){
        val animation = AlphaAnimation(1f,0f)
        animation.duration = 500
        binding.frameL2.visibility = View.INVISIBLE
        binding.frameL2.animation = animation
    }

    //첫 실행 시 프래그먼트가 열릴 프레임을 보이게 설정
    private fun openFirstFrame(){
        val animation = AlphaAnimation(0f,1f)
        animation.duration = 500
        binding.frameL3.bringToFront()
        binding.frameL3.visibility = View.VISIBLE
        binding.frameL3.animation = animation
    }

    //첫 실행 시 프래그먼트가 열린 프레임을 보이지 않게 설정
    private fun removeFirstFrame(){
        val animation = AlphaAnimation(1f,0f)
        animation.duration = 500
        binding.frameL3.visibility = View.GONE
        binding.frameL3.animation = animation
        binding.frameL.visibility = View.VISIBLE
    }

    //메인 화면에서 백버튼 입력시 유저에게 앱을 종료하려는지 확인하는 창을 띄우는 함수
    private fun showDialog(){
        val dialog = Dialog(this)
        val dialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog, null)

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(dialogView)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.show()

        dialogView.findViewById<Button>(R.id.negative).setOnClickListener {
            dialog.dismiss()
        }
        dialogView.findViewById<Button>(R.id.positive).setOnClickListener {
            dialog.dismiss()
            finish()
        }
    }

    //설정에서 가계부 내역 전체 삭제 선택시 열림
    fun showDialogDelete(){
        val dialog = Dialog(this)
        val dialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog_delete, null)

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(dialogView)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.show()

        dialogView.findViewById<Button>(R.id.negative).setOnClickListener {
            dialog.dismiss()
        }
        dialogView.findViewById<Button>(R.id.positive).setOnClickListener {
            dialog.dismiss()
            helper.deleteAll()
        }
    }



    //현재 화면이 어떤 화면인지에 따라 백버튼 입력시 실행되는지가 다르다
    override fun onBackPressed() {
        when(currentF){
            0->{
                //메인 페이지
                showDialog()
            }
            1->{
                //내역 페이지
                showDialog()
            }
            2->{
                //리포트 페이지
                showDialog()
            }
            3->{
                //설정 리스트 페이지
                showDialog()
            }
            4->{
                //현금 및 카드 설정 페이지
                super.onBackPressed()

                currentF = 3
            }
            5->{
                //현금 및 카드 설정 한도 입력창
                super.onBackPressed()

                removeFrame()

                currentF = 4

                val fragment = supportFragmentManager.findFragmentByTag("setting") as SettingFragment?
                fragment?.turnTextView(whichTextView)
            }
            6->{
                //내역 입력 페이지
                currentF = eveF

                val sfm = supportFragmentManager
                if(currentF == 0){
                    val fragment = sfm.findFragmentByTag("mainPage") as MainPageFragment?
                    fragment?.setPage()
                }
                else if(currentF == 1){
                    val fragment = sfm.findFragmentByTag("book") as BookFragment?
                    fragment?.callNotify()
                }

                super.onBackPressed()

                removeFrame()
            }
            7->{
                //내역 가격 입력창
                super.onBackPressed()

                currentF = 6
            }
            8->{
                //내역 수정 페이지
                currentF = 1

                val fragment = supportFragmentManager.findFragmentByTag("book") as BookFragment?
                fragment?.callNotify()

                super.onBackPressed()

                removeFrame()
            }
            9->{
                //내역 수정 가격 입력창
                super.onBackPressed()

                currentF = 8
            }
            10->{
                //첫 실행 페이지
                showDialog()
            }
            11->{
                //첫 실행 한도 입력창
                super.onBackPressed()

                removeFrame()

                currentF = 9

                val fragment = supportFragmentManager.findFragmentByTag("firstSetting") as FirstSetFragment?
                fragment?.turnTextView(whichTextView)
            }
        }
    }

    //에디트텍스트 이외 부분 터치시 키보드 내려감
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val focusView = currentFocus
        if(focusView != null){
            val rect = Rect()
            focusView.getGlobalVisibleRect(rect)
            val x = ev!!.x.toInt()
            val y = ev.y.toInt()
            if(!rect.contains(x,y)){
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(focusView.windowToken, 0)
                focusView.clearFocus()
            }
        }
        return super.dispatchTouchEvent(ev)
    }
}