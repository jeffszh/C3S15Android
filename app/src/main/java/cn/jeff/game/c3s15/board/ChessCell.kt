package cn.jeff.game.c3s15.board

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import cn.jeff.game.c3s15.R

class ChessCell : View {

	constructor(context: Context) : super(context)
	constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
	constructor(context: Context, attrs: AttributeSet, defStyle: Int) :
			super(context, attrs, defStyle)

	var chess = Chess.EMPTY
		set(value) {
			field = value
			updateBackground()
			invalidate()
		}

	private var cellSize = 100
	private val paint = Paint()
	private val widthArray = FloatArray(1)

	init {
		updateBackground()
	}

	private fun updateBackground(){
		setBackgroundResource(when (chess) {
			Chess.EMPTY -> 0
			Chess.SOLDIER -> R.drawable.chess_soldier_outline
			Chess.CANNON -> R.drawable.chess_cannon_outline2
		})
	}

	override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
		super.onLayout(changed, left, top, right, bottom)
		if (changed) {
			cellSize = right - left
			paint.textSize = 120F
			paint.getTextWidths("炮", widthArray)
			paint.textSize = cellSize * 0.6F * 120F / widthArray[0]
		}
	}

	override fun onDraw(canvas: Canvas?) {
		super.onDraw(canvas)
		canvas ?: return
//		resources.getDrawable(R.drawable.chess_cannon_outline, null).draw(canvas)
		canvas.drawText("炮", cellSize * 0.2F, cellSize * 0.72F, paint)
	}

/*
	companion object {
		/** 边界留白 */
		private const val borderPadding = 10.0

		private val fontRefSize = SimpleDoubleProperty(0.0)

		private var fontSize = 0.0

		init {
			fontRefSize.onChange { refSize ->
				println("字体改变")
				val text = Text("字")
				text.font = Font(120.0)
				val fontWidth = text.boundsInLocal.width
				fontSize = text.font.size / fontWidth * refSize
				println("字体大小：$fontSize")
			}
		}
	}

	/** 棋盘格的尺寸 */
	val cellSizeProperty = SimpleDoubleProperty(0.0)

	/** 棋子 */
	val chessProperty = SimpleObjectProperty(Chess.CANNON)

	private var needRepaint = false

	init {
		cellSizeProperty.onChange {
			triggerRepaint()
		}
		chessProperty.onChange {
			triggerRepaint()
		}
	}

	private fun triggerRepaint() {
		needRepaint = true
		runLater {
			if (needRepaint) {
				repaint()
				needRepaint = false
			}
		}
	}

	private fun repaint() {
		width = cellSizeProperty.value
		height = width
		fontRefSize.value = width * .75 - borderPadding - 4.0
		graphicsContext2D.apply {
			clearRect(0.0, 0.0, width, height)
			if (chessProperty.value.text.isNotBlank()) {
				stroke = chessProperty.value.color
				lineWidth = 3.0
				fill = Color.WHITE
				val internalWidth = width - 2 * borderPadding
				fillOval(borderPadding, borderPadding, internalWidth, internalWidth)
				strokeOval(borderPadding, borderPadding, internalWidth, internalWidth)
				font = Font(fontSize)
				fill = stroke
				fillText(
					chessProperty.value.text,
					(cellSizeProperty.value - fontRefSize.value) / 2,
					cellSizeProperty.value - (cellSizeProperty.value - fontRefSize.value) / 2 -
							fontSize * .16
				)
			}
		}
	}
*/
}
