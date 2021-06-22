package com.eitu.arcornbook

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

class SQLiteHelper(context: Context, name:String, version:Int):SQLiteOpenHelper(context, name, null, version) {

    //데이터 베이스 생성 시 한번 콜
    override fun onCreate(db: SQLiteDatabase?) {
        val create = "create table bookKeep ( no integer primary key, kinds text, pattern text, detail text, price integer, date integer )"

        db?.execSQL(create)
    }

    fun insertItem(bookItem: BookItem){
        //Map 처럼 쓰는 ContentValues
        val values = ContentValues()
        values.put("kinds", bookItem.kinds)
        values.put("pattern", bookItem.pattern)
        values.put("detail", bookItem.detail)
        values.put("price", bookItem.price)
        values.put("date", bookItem.date)

        val wd = writableDatabase
        wd.insert("bookKeep", null, values)
        wd.close()

    }

    fun updateItem(bookItem: BookItem){
        //Map 처럼 쓰는 ContentValues
        val values = ContentValues()
        values.put("kinds", bookItem.kinds)
        values.put("pattern", bookItem.pattern)
        values.put("detail", bookItem.detail)
        values.put("price", bookItem.price)
        values.put("date", bookItem.date)

        val wd = writableDatabase
        wd.update("bookKeep", values, "no = ${bookItem.no}", null)
        wd.close()

    }

    fun selectDate(target:String, fDate:Long, tDate:Long):MutableList<Long>{
        val list = mutableListOf<Long>()

        val select = "select distinct date from bookKeep where kinds = '$target' and date >= $fDate and date <= $tDate order by date desc"
        val rd = readableDatabase
        val cursor = rd.rawQuery(select, null)
        while (cursor.moveToNext()){
            val date = cursor.getLong(cursor.getColumnIndex("date"))
            list.add(date)
        }

        cursor.close()
        rd.close()

        return list
    }

    fun selectCashByDay(targetDate:Long):MutableList<BookItem>{
        val list = mutableListOf<BookItem>()
        val target = "Cash"

        val select = "select * from bookKeep where date = $targetDate and kinds = '$target'"
        val rd = readableDatabase
        val cursor = rd.rawQuery(select, null)
        while(cursor.moveToNext()){
            val no = cursor.getLong(cursor.getColumnIndex("no"))
            val kinds = cursor.getString(cursor.getColumnIndex("kinds"))
            val pattern = cursor.getString(cursor.getColumnIndex("pattern"))
            val detail = cursor.getString(cursor.getColumnIndex("detail"))
            val price = cursor.getLong(cursor.getColumnIndex("price"))
            val date = cursor.getLong(cursor.getColumnIndex("date"))
            list.add(BookItem(no, kinds, pattern, detail, price, date))
        }

        cursor.close()
        rd.close()

        return list
    }

    fun selectCard1ByDay(targetDate:Long):MutableList<BookItem>{
        val list = mutableListOf<BookItem>()
        val target = "Card1"

        val select = "select * from bookKeep where date = $targetDate and kinds = '$target'"
        val rd = readableDatabase
        val cursor = rd.rawQuery(select, null)
        while(cursor.moveToNext()){
            val no = cursor.getLong(cursor.getColumnIndex("no"))
            val kinds = cursor.getString(cursor.getColumnIndex("kinds"))
            val pattern = cursor.getString(cursor.getColumnIndex("pattern"))
            val detail = cursor.getString(cursor.getColumnIndex("detail"))
            val price = cursor.getLong(cursor.getColumnIndex("price"))
            val date = cursor.getLong(cursor.getColumnIndex("date"))
            list.add(BookItem(no, kinds, pattern, detail, price, date))
        }

        cursor.close()
        rd.close()

        return list
    }

    fun selectCard2ByDay(targetDate:Long):MutableList<BookItem>{
        val list = mutableListOf<BookItem>()
        val target = "Card2"

        val select = "select * from bookKeep where date = $targetDate and kinds = '$target'"
        val rd = readableDatabase
        val cursor = rd.rawQuery(select, null)
        while(cursor.moveToNext()){
            val no = cursor.getLong(cursor.getColumnIndex("no"))
            val kinds = cursor.getString(cursor.getColumnIndex("kinds"))
            val pattern = cursor.getString(cursor.getColumnIndex("pattern"))
            val detail = cursor.getString(cursor.getColumnIndex("detail"))
            val price = cursor.getLong(cursor.getColumnIndex("price"))
            val date = cursor.getLong(cursor.getColumnIndex("date"))
            list.add(BookItem(no, kinds, pattern, detail, price, date))
        }

        cursor.close()
        rd.close()

        return list
    }

    fun selectCard3ByDay(targetDate:Long):MutableList<BookItem>{
        val list = mutableListOf<BookItem>()
        val target = "Card3"

        val select = "select * from bookKeep where date = $targetDate and kinds = '$target'"
        val rd = readableDatabase
        val cursor = rd.rawQuery(select, null)
        while(cursor.moveToNext()){
            val no = cursor.getLong(cursor.getColumnIndex("no"))
            val kinds = cursor.getString(cursor.getColumnIndex("kinds"))
            val pattern = cursor.getString(cursor.getColumnIndex("pattern"))
            val detail = cursor.getString(cursor.getColumnIndex("detail"))
            val price = cursor.getLong(cursor.getColumnIndex("price"))
            val date = cursor.getLong(cursor.getColumnIndex("date"))
            list.add(BookItem(no, kinds, pattern, detail, price, date))
        }

        cursor.close()
        rd.close()

        return list
    }

    @SuppressLint("SimpleDateFormat")
    fun selectTodayUse():MutableList<BookItem>{
        val list = mutableListOf<BookItem>()

        val now = System.currentTimeMillis()
        val date = Date(now)
        val sdf = SimpleDateFormat("yyMMdd")
        val today = sdf.format(date).toLong()

        val select = "select * from bookKeep where date = $today order by no desc"

        val rd = readableDatabase
        val cursor = rd.rawQuery(select, null)
        while(cursor.moveToNext()){
            val no = cursor.getLong(cursor.getColumnIndex("no"))
            val kinds = cursor.getString(cursor.getColumnIndex("kinds"))
            //val pattern = cursor.getString(cursor.getColumnIndex("pattern"))
            val detail = cursor.getString(cursor.getColumnIndex("detail"))
            val price = cursor.getLong(cursor.getColumnIndex("price"))
            //val date = cursor.getLong(cursor.getColumnIndex("date"))
            list.add(BookItem(no,kinds,"",detail,price,0L))
        }

        cursor.close()
        rd.close()

        return list
    }

    @SuppressLint("SimpleDateFormat")
    fun selectMonthCashUse():Long{
        var monthCashUse:Long = 0
        val target = "Cash"

        val now = System.currentTimeMillis()
        val date = Date(now)
        val sdf = SimpleDateFormat("yyMM")
        val thisMonth = sdf.format(date).toLong()*100

        val select = "select price from bookKeep where date > $thisMonth and kinds = '$target'"
        Log.d("monthcash", "$thisMonth")

        val rd = readableDatabase
        val cursor = rd.rawQuery(select, null)
        while(cursor.moveToNext()){
            monthCashUse += cursor.getLong(cursor.getColumnIndex("price"))
            Log.d("monthcashuse", "$monthCashUse")
        }

        cursor.close()
        rd.close()

        return monthCashUse
    }

    @SuppressLint("SimpleDateFormat")
    fun selectMonthCard1Use():Long{
        var monthCashUse:Long = 0
        val target = "Card1"

        val now = System.currentTimeMillis()
        val date = Date(now)
        val sdf = SimpleDateFormat("yyMM")
        val thisMonth = sdf.format(date).toLong()*100

        val select = "select price from bookKeep where date > $thisMonth and kinds = '$target'"
        Log.d("monthcard1", "$thisMonth")

        val rd = readableDatabase
        val cursor = rd.rawQuery(select, null)
        while(cursor.moveToNext()){
            monthCashUse += cursor.getLong(cursor.getColumnIndex("price"))
            Log.d("monthcard1use", "$monthCashUse")
        }

        cursor.close()
        rd.close()

        return monthCashUse
    }

    @SuppressLint("SimpleDateFormat")
    fun selectMonthCard2Use():Long{
        var monthCashUse:Long = 0
        val target = "Card2"

        val now = System.currentTimeMillis()
        val date = Date(now)
        val sdf = SimpleDateFormat("yyMM")
        val thisMonth = sdf.format(date).toLong()*100

        val select = "select price from bookKeep where date > $thisMonth and kinds = '$target'"
        Log.d("monthcard2", "$thisMonth")

        val rd = readableDatabase
        val cursor = rd.rawQuery(select, null)
        while(cursor.moveToNext()){
            monthCashUse += cursor.getLong(cursor.getColumnIndex("price"))
            Log.d("monthcard2use", "$monthCashUse")
        }

        cursor.close()
        rd.close()

        return monthCashUse
    }

    @SuppressLint("SimpleDateFormat")
    fun selectMonthCard3Use():Long{
        var monthCashUse:Long = 0
        val target = "Card3"

        val now = System.currentTimeMillis()
        val date = Date(now)
        val sdf = SimpleDateFormat("yyMM", Locale.KOREA)
        val thisMonth = sdf.format(date).toLong()*100

        val select = "select price from bookKeep where date > $thisMonth and kinds = '$target'"
        Log.d("monthcard3", "$thisMonth")

        val rd = readableDatabase
        val cursor = rd.rawQuery(select, null)
        while(cursor.moveToNext()){
            monthCashUse += cursor.getLong(cursor.getColumnIndex("price"))
            Log.d("monthcard3use", "$monthCashUse")
        }

        cursor.close()
        rd.close()

        return monthCashUse
    }

    fun selectTodayCount():Long{
        var count = 0L

        val now = System.currentTimeMillis()
        val date = Date(now)
        val sdf = SimpleDateFormat("yyMMdd", Locale.KOREA)
        val today = sdf.format(date).toLong()

        val select = "select count(price) from bookKeep where date = $today"

        val rd = readableDatabase
        val cursor = rd.rawQuery(select, null)
        while(cursor.moveToNext()){
            count = cursor.getLong(cursor.getColumnIndex("count(price)"))
        }

        cursor.close()
        rd.close()

        return count
    }

    fun selectByPattern(pattern:String, month:Long):MutableList<Long>{
        val list = mutableListOf<Long>()
        var price = 0L

        val select = "select count(price) from bookKeep where pattern = '$pattern' and date > $month and date < ${month+100}"
        val select2 = "select price from bookKeep where pattern = '$pattern' and date > $month and date < ${month+100}"

        val rd = readableDatabase
        val cursor = rd.rawQuery(select, null)
        while(cursor.moveToNext()){
            list.add(cursor.getLong(cursor.getColumnIndex("count(price)")))
        }

        cursor.close()

        val cursor2 = rd.rawQuery(select2, null)
        while (cursor2.moveToNext()){
            price += cursor2.getLong(cursor2.getColumnIndex("price"))
        }

        cursor2.close()
        rd.close()

        list.add(price)

        return list
    }

    fun deleteAll(){
        val delete = "delete from bookKeep"
        val wd = writableDatabase
        wd.execSQL(delete)
        wd.close()
    }

    fun deleteOne(bookItem: BookItem){
        val delete = "delete from bookKeep where no = ${bookItem.no}"
        val wd = writableDatabase
        wd.execSQL(delete)
        wd.close()
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }
}