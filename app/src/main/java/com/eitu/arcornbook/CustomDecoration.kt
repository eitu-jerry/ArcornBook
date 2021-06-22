package com.eitu.arcornbook

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.recyclerview.widget.RecyclerView

class CustomDecoration(val height:Float, val padding:Float):RecyclerView.ItemDecoration() {
    private val paint = Paint()
    private val dividerColor = Color.parseColor("#FF9123")
    init {
        paint.color = dividerColor
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
        val left = parent.paddingStart + padding*2
        val right = parent.width - parent.paddingEnd - padding*2
        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val top = (child.bottom + params.bottomMargin).toFloat()
            val bottom = top + height
            c.drawRect(left, top, right, bottom, paint)
        }

    }
}