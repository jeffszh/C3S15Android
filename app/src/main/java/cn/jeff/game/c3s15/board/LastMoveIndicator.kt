package cn.jeff.game.c3s15.board

import android.content.Context
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.isVisible
import cn.jeff.game.c3s15.R
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * # 顯示之前一步棋的箭頭
 *
 * 以單獨的控件實現，有別于JavaFX版的做法。
 */
class LastMoveIndicator(context: Context) : AppCompatImageView(context) {

	init {
		// setBackgroundResource(R.color.design_default_color_primary_dark)
		setImageResource(R.drawable.last_move_indicator)
		scaleType = ScaleType.FIT_XY
	}

	/**
	 * # 設置指示器箭頭的位置
	 *
	 * @param fromX
	 * @param fromY
	 * @param toX
	 * @param toY
	 * 所有坐標參數是棋盤格的坐標，範圍0-4。
	 *
	 * @param cellSize 棋盤格的大小，同[cn.jeff.game.c3s15.ChessBoard.cellSize]。
	 */
	fun setPosition(fromX: Int, fromY: Int, toX: Int, toY: Int, cellSize: Int) {
		val alterX: Double
		val alterY: Double
		rotation = if (fromX == toX) {
			alterX = 0.0
			alterY = 0.5
			if (fromY < toY)
				90F
			else
				270F
		} else {
			alterX = 0.5
			alterY = 0.0
			if (fromX < toX)
				0F
			else
				180F
		}
		val minX = min(fromX, toX)
		val minY = min(fromY, toY)
		val dx = abs(toX - fromX)
		val dy = abs(toY - fromY)
		val posLeft = (minX + 0.2 + alterX) * cellSize
		val posTop = (minY + 0.2 + alterY) * cellSize
		val posRight = posLeft + max(1, dx) * cellSize
		val posBottom = posTop + max(1, dy) * cellSize
		layout(posLeft.toInt(), posTop.toInt(), posRight.toInt(), posBottom.toInt())
		isVisible = true
		invalidate()
	}

	fun hide() {
		isVisible = false
	}

}

/*class LastMoveIndicator(context: Context?) : View(context) {

	private val originalPath = Path().apply {
		moveTo(0F,0.1F)
	}
	private var path = Path()
	private val paint = Paint()

	/**
	 * # 設置指示器箭頭的位置
	 *
	 * @param fromX
	 * @param fromY
	 * @param toX
	 * @param toY
	 * 所有坐標參數是棋盤格的坐標，範圍0-4。
	 *
	 * @param cellSize 棋盤格的大小，同[cn.jeff.game.c3s15.ChessBoard.cellSize]。
	 */
	fun setPosition(fromX: Int, fromY: Int, toX: Int, toY: Int, cellSize: Int) {
		this.fromX = fromX
		this.fromY = fromY
		this.toX = toX
		this.toY = toY
		this.cellSize = cellSize
		isVisible = true
		invalidate()
	}

	fun hide() {
		isVisible = false
	}

	override fun onDraw(canvas: Canvas?) {
		super.onDraw(canvas)
		canvas ?: return
		paint.strokeWidth = 3F
		paint.style = Paint.Style.FILL
		paint.color = Color.argb(160, 255, 160, 0)
		canvas.drawPath(path, paint)
		paint.style = Paint.Style.STROKE
		paint.color = Color.argb(160, 80, 250, 0)
		canvas.drawPath(path, paint)
	}

}*/
