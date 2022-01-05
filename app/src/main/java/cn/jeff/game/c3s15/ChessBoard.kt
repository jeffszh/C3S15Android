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
import cn.jeff.game.c3s15.board.Chess
import cn.jeff.game.c3s15.board.ChessCell
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

	private val chessArr = mutableListOf<ChessCell>()

	init {
		repeat(25) {
			chessArr.add(ChessCell(context).also {
				addView(it)
			})
		}
		repeat(15) { ind ->
			chessArr[ind].chess = Chess.SOLDIER
		}
		chessArr[20].chess = Chess.CANNON
		chessArr[22].chess = Chess.CANNON
		chessArr[24].chess = Chess.CANNON
		chessArr[13].isSelected = true
		chessArr[22].isSelected = true
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
		for (cellY in 0..4) {
			for (cellX in 0..4) {
				chessArr[cellY * 5 + cellX].layout(
						(cellSize * (cellX + 0.1)).toInt(),
						(cellSize * (cellY + 0.1)).toInt(),
						(cellSize * (cellX + 0.9)).toInt(),
						(cellSize * (cellY + 0.9)).toInt()
				)
			}
		}
//		chessArr[0].layout((cellSize * 1.2).toInt(), (cellSize * 0.2).toInt(),
//				(cellSize * 1.8).toInt(), (cellSize * 0.8).toInt())
//		chessArr[1].layout(0, cellSize, cellSize, cellSize * 2)
//		chessArr[2].layout(cellSize * 2, cellSize, cellSize * 3, cellSize * 2)
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
