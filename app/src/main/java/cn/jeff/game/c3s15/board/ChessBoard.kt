package cn.jeff.game.c3s15.board

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ViewGroup
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
	private val lastMoveIndicator: LastMoveIndicator
	private var lastMove: ChessBoardContent.Move? = null

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

		lastMoveIndicator = LastMoveIndicator(context)
		addView(lastMoveIndicator)
		lastMove = ChessBoardContent.Move(2, 2, 2, 4)
		updateLastMove()
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
		cellSize = (min(measuredWidth, measuredHeight) / 5.4).toInt()
		updateLastMove()
		val childWidthSize = (cellSize * 5.4).toInt()
		val actualSize = MeasureSpec.makeMeasureSpec(childWidthSize, MeasureSpec.EXACTLY)
		super.onMeasure(actualSize, actualSize)
	}

	override fun onDraw(canvas: Canvas?) {
		canvas ?: return
		super.onDraw(canvas)
		for (i in 0..5) {
			gridPen.strokeWidth = if (i == 0 || i == 5) 5F else 3F
			val d = ((i + 0.2) * cellSize).toFloat()
			canvas.drawLine(d, cellSize * 0.2F,
					d, width.toFloat() - cellSize * 0.2F, gridPen)
			canvas.drawLine(cellSize * 0.2F, d,
					width.toFloat() - cellSize * 0.2F, d, gridPen)
		}
	}

	override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
		rearrangeChildren()
	}

	private fun rearrangeChildren() {
		for (cellY in 0..4) {
			for (cellX in 0..4) {
				chessArr[cellY * 5 + cellX].layout(
						(cellSize * (cellX + 0.3)).toInt(),
						(cellSize * (cellY + 0.3)).toInt(),
						(cellSize * (cellX + 1.1)).toInt(),
						(cellSize * (cellY + 1.1)).toInt()
				)
			}
		}
//		chessArr[0].layout((cellSize * 1.2).toInt(), (cellSize * 0.2).toInt(),
//				(cellSize * 1.8).toInt(), (cellSize * 0.8).toInt())
//		chessArr[1].layout(0, cellSize, cellSize, cellSize * 2)
//		chessArr[2].layout(cellSize * 2, cellSize, cellSize * 3, cellSize * 2)
	}

	private fun updateLastMove() {
		lastMove?.apply {
			lastMoveIndicator.setPosition(fromX, fromY, toX, toY, cellSize)
		} ?: lastMoveIndicator.hide()
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
