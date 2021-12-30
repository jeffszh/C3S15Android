package cn.jeff.game.c3s15

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.ImageView
import kotlin.math.min

class ChessBoard : ViewGroup {

	constructor(context: Context) : super(context)
	constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
	constructor(context: Context, attributeSet: AttributeSet, defStyle: Int) : super(
		context, attributeSet, defStyle
	)

	companion object {
		private val LOG_TAG = ChessBoard::class.simpleName
	}

	private var cellSize = 20
	private val gridPen = Paint().apply {
		color = Color.BLACK
		style = Paint.Style.STROKE
		strokeWidth = 3F
	}

	private val chessArr = mutableListOf<ImageView>()

	init {
		repeat(3) {
			chessArr.add(ImageView(context).also {
				it.setImageResource(R.drawable.chess_cannon_outline)
				addView(it)
			})
		}
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

	override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
		rearrangeChildren()
	}

	private fun rearrangeChildren() {
		chessArr[0].layout(cellSize, 0, cellSize * 2, cellSize)
		chessArr[1].layout(0, cellSize, cellSize, cellSize * 2)
		chessArr[2].layout(cellSize * 2, cellSize, cellSize * 3, cellSize * 2)
	}

	@SuppressLint("ClickableViewAccessibility")
	override fun onTouchEvent(event: MotionEvent?): Boolean {
		when (event?.action) {
			MotionEvent.ACTION_DOWN -> {
				Log.d(LOG_TAG, "=============down====${event.x}====${event.y}=====")
			}
			MotionEvent.ACTION_MOVE -> {
				Log.d(LOG_TAG, "=============move====${event.x}====${event.y}=====")
			}
			MotionEvent.ACTION_UP -> {
				Log.d(LOG_TAG, "=============up====${event.x}====${event.y}=====")
			}
			else -> return super.onTouchEvent(event)
		}
		return true
	}

}
