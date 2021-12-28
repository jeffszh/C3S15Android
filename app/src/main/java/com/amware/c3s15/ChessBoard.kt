package com.amware.c3s15

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import kotlin.math.min

class ChessBoard(context: Context) : View(context) {

	private var cellSize = 20
	private val gridPen = Paint().apply {
		color = Color.BLACK
		style = Paint.Style.STROKE
		strokeWidth = 3F
	}

	override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
		// super.onMeasure(widthMeasureSpec, heightMeasureSpec)
		// val x = (parent as MainActivity).tv01.width / 2
		// setMeasuredDimension(x, x)
//		setMeasuredDimension(200, 200)

		setMeasuredDimension(
			getDefaultSize(0, widthMeasureSpec),
			getDefaultSize(0, heightMeasureSpec)
		)
		cellSize = min(measuredWidth, measuredHeight) / 5
		val childWidthSize = cellSize * 5
		val actualSize = MeasureSpec.makeMeasureSpec(childWidthSize, MeasureSpec.EXACTLY)
		super.onMeasure(actualSize, actualSize)
	}

	override fun onDraw(canvas: Canvas?) {
		canvas ?: return
		super.onDraw(canvas)
		for (i in 1..4) {
			val d = (i * cellSize).toFloat()
			canvas.drawLine(d, 0f, d, width.toFloat(), gridPen)
			canvas.drawLine(0F, d, width.toFloat(), d, gridPen)
		}
	}

}
