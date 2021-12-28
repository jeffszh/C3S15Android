package com.amware.c3s15

import android.content.Context
import android.graphics.Canvas
import android.widget.LinearLayout

class ChessBoard(context: Context) : LinearLayout(context) {

	override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
		// super.onMeasure(widthMeasureSpec, heightMeasureSpec)
		// val x = (parent as MainActivity).tv01.width / 2
		// setMeasuredDimension(x, x)
//		setMeasuredDimension(200, 200)

		setMeasuredDimension(
			getDefaultSize(0, widthMeasureSpec),
			getDefaultSize(0, heightMeasureSpec)
		)
		val childWidthSize = measuredWidth
		val actualSize = MeasureSpec.makeMeasureSpec(childWidthSize, MeasureSpec.EXACTLY)
		super.onMeasure(actualSize, actualSize)
	}

	override fun onDraw(canvas: Canvas?) {
		super.onDraw(canvas)
	}

}
