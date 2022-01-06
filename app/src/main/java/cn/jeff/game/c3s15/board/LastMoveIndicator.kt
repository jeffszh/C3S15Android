package cn.jeff.game.c3s15.board

import android.content.Context
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.isVisible
import cn.jeff.game.c3s15.R
import kotlin.math.abs
import kotlin.math.max

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
	 * @param cellSize 棋盤格的大小，同[ChessBoard.cellSize]。
	 */
	fun setPosition(fromX: Int, fromY: Int, toX: Int, toY: Int, cellSize: Int) {
		// 先計算箭頭的長度
		val arrowLength = max(abs(fromX - toX), abs(fromY - toY))
		// 然後計算中心位置
		val pivotX = (fromX + toX) / 2.0
		val pivotY = (fromY + toY) / 2.0

		// 計算旋轉角度
		rotation = if (fromX == toX) {
			if (fromY < toY)
				90F
			else
				270F
		} else {
			if (fromX < toX)
				0F
			else
				180F
		}

		// 實際上總是定好位置后，再圍繞中心旋轉的，所以是計算旋轉前的位置才正確。
		val posLeft = (pivotX - arrowLength / 2.0 + 0.7) * cellSize
		val posTop = (pivotY - 0.5 + 0.7) * cellSize
		val posRight = (pivotX + arrowLength / 2.0 + 0.7) * cellSize
		val posBottom = (pivotY + 0.5 + 0.7) * cellSize
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
