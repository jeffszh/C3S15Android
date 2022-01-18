package cn.jeff.game.c3s15.board

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.Toast
import cn.jeff.game.c3s15.GlobalVars
import cn.jeff.game.c3s15.brain.Brain
import cn.jeff.game.c3s15.brain.PlayerType
import cn.jeff.game.c3s15.event.ChessBoardContentChangedEvent
import cn.jeff.game.c3s15.net.NetworkGameProcessor
import org.greenrobot.eventbus.EventBus
import kotlin.concurrent.thread
import kotlin.math.floor
import kotlin.math.min

/**
 * # 棋盘
 *
 * 用于下棋的场所，在主窗口的中部显示。
 */
class ChessBoard : ViewGroup {

	constructor(context: Context) : super(context)
	constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
	constructor(context: Context, attributeSet: AttributeSet, defStyle: Int) : super(
		context, attributeSet, defStyle
	)

	companion object {
		private val LOG_TAG = ChessBoard::class.simpleName
	}

//	private val mainActivity
//		get() = MainActivity.instance!!
//	private val cannonText
//		get() = GlobalVars.appConf.cannonText
//	private val soldierText
//		get() = GlobalVars.appConf.soldierText

	private var cellSize = 20
	private val gridPen = Paint().apply {
		color = Color.BLACK
		style = Paint.Style.STROKE
		strokeWidth = 3F
	}

	private val chessArr = mutableListOf<ChessCell>()
	private val lastMoveIndicator: LastMoveIndicator
	private val chessBoardContent get() = GlobalVars.chessBoardContent
	// private var lastMove: ChessBoardContent.Move? = null

	/** 鼠标按下的棋盘格位置（X和Y都是0至4）。 */
	private var mouseDownCellPos: Point? = null

	/** 鼠标操作符合点击风格。 */
	private var isClickBehavior = false
		set(value) {
			mouseDownCellPos?.also { mdp ->
				if (!field && value) {
					// 从false变true，设置选择棋子。
					chessArr[mdp.x + mdp.y * 5].isSelected = true
				} else if (field && !value) {
					// 从true变false，取消选择棋子。
					chessArr[mdp.x + mdp.y * 5].isSelected = false
				}
			}
			field = value
		}

	init {
		repeat(25) {
			chessArr.add(ChessCell(context).also {
				addView(it)
			})
		}
//		repeat(15) { ind ->
//			chessArr[ind].chess = Chess.SOLDIER
//		}
//		chessArr[20].chess = Chess.CANNON
//		chessArr[22].chess = Chess.CANNON
//		chessArr[24].chess = Chess.CANNON
//		chessArr[13].isSelected = true
//		chessArr[22].isSelected = true

//		chessBoardContent.setInitialContent()
//		applyChessboardContent()

		lastMoveIndicator = LastMoveIndicator(context)
		addView(lastMoveIndicator)
		// chessBoardContent.lastMove = ChessBoardContent.Move(2, 2, 2, 4)
		updateLastMove()
	}

	fun restartGame() {
		isClickBehavior = false
		mouseDownCellPos = null
		chessBoardContent.setInitialContent()
		applyChessboardContent()
	}

	private fun applyChessboardContent() {
		for (cellY in 0..4) {
			for (cellX in 0..4) {
				chessArr[cellX + cellY * 5].chess =
					chessBoardContent[cellX, cellY] ?: error("不可能！")
			}
		}
		thread {
			Thread.sleep(100)
			EventBus.getDefault().post(ChessBoardContentChangedEvent(chessBoardContent))
		}
		invalidate()
		Brain.restartAiRoutine(chessBoardContent)
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
			canvas.drawLine(
				d, cellSize * 0.2F,
				d, width.toFloat() - cellSize * 0.2F, gridPen
			)
			canvas.drawLine(
				cellSize * 0.2F, d,
				width.toFloat() - cellSize * 0.2F, d, gridPen
			)
		}
	}

	override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
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
	}

	private fun rearrangeChildren() {
		for (cellY in 0..4) {
			for (cellX in 0..4) {
				chessArr[cellY * 5 + cellX].apply {
					x = cellSize * (cellX + 0.3F)
					y = cellSize * (cellY + 0.3F)
				}
			}
		}
//		chessArr[0].layout((cellSize * 1.2).toInt(), (cellSize * 0.2).toInt(),
//				(cellSize * 1.8).toInt(), (cellSize * 0.8).toInt())
//		chessArr[1].layout(0, cellSize, cellSize, cellSize * 2)
//		chessArr[2].layout(cellSize * 2, cellSize, cellSize * 3, cellSize * 2)
	}

	private fun updateLastMove() {
		chessBoardContent.lastMove?.apply {
			lastMoveIndicator.setPosition(fromX, fromY, toX, toY, cellSize)
		} ?: lastMoveIndicator.hide()
	}

	@SuppressLint("ClickableViewAccessibility")
	override fun onTouchEvent(event: MotionEvent?): Boolean {
		event ?: return false
		// 若不是轮到玩家操作，直接返回。
		if (when (chessBoardContent.whoseTurn) {
				Chess.EMPTY -> true
				Chess.SOLDIER -> GlobalVars.soldiersPlayerType != PlayerType.HUMAN
				Chess.CANNON -> GlobalVars.cannonsPlayerType != PlayerType.HUMAN
			}
		) return false
		val (cellX, cellY) = mouseXyToChessBoardXy(event.x, event.y)
		when (event.action) {
			MotionEvent.ACTION_DOWN -> {
				Log.d(LOG_TAG, "=============down====${cellX}====${cellY}=====")
				if (isClickBehavior) {
					isClickBehavior = false
					mouseDownCellPos?.also { mdp ->
						applyMove(ChessBoardContent.Move(mdp.x, mdp.y, cellX, cellY))
					}
					mouseDownCellPos = null
				} else {
					if (chessBoardContent[cellX, cellY] == chessBoardContent.whoseTurn) {
						mouseDownCellPos = Point(cellX, cellY)
						isClickBehavior = true
					}
				}
			}
			MotionEvent.ACTION_MOVE -> {
				Log.d(LOG_TAG, "=============move====${cellX}====${cellY}=====")
				mouseDownCellPos?.also { mdp ->
					if (isClickBehavior) {
						if (mdp.x != cellX || mdp.y != cellY) {
							isClickBehavior = false
						}
					}
					val draggingCell = chessArr[mdp.x + mdp.y * 5]
					draggingCell.x = event.x - cellSize * 0.4F
					draggingCell.y = event.y - cellSize * 0.4F
					draggingCell.bringToFront()
//					draggingCell.invalidate()
//					invalidate()
				}
			}
			MotionEvent.ACTION_UP -> {
				Log.d(LOG_TAG, "=============up====${cellX}====${cellY}=====")
				mouseDownCellPos?.also { mdp ->
					if (!isClickBehavior) {
						applyMove(ChessBoardContent.Move(mdp.x, mdp.y, cellX, cellY))
						mouseDownCellPos = null
					}
					rearrangeChildren()
				}
			}
			else -> return super.onTouchEvent(event)
		}
		return true
	}

	fun applyMove(move: ChessBoardContent.Move, byRemote: Boolean = false) {
		if ((GlobalVars.soldiersPlayerType == PlayerType.NET ||
					GlobalVars.cannonsPlayerType == PlayerType.NET) &&
			!byRemote && !GlobalVars.isNetConnected
		//NetworkGameProcessor.state != NetGameState.LOCAL_TURN
		) {
			return
		}
		val moveSuccess = chessBoardContent.applyMove(move)
		applyChessboardContent()
		lastMoveIndicator.bringToFront()
		updateLastMove()
		rearrangeChildren()
		showDialogIfGameOver()
		Brain.restartAiRoutine(chessBoardContent)
		if (moveSuccess && !byRemote) {
			NetworkGameProcessor.applyLocalMove(chessBoardContent.compressToInt64(), move)
		}
	}

	private fun mouseXyToChessBoardXy(mX: Float, mY: Float) = listOf(
		floor(mX / cellSize - 0.2F).toInt(),
		floor(mY / cellSize - 0.2F).toInt()
	)

	private fun showDialogIfGameOver() {
		if (chessBoardContent.gameOver) {
			val winSideText = chessBoardContent.whoWin.text
			// 安卓中顯示對話框不容易，改為Toast。
			Toast.makeText(context, "【$winSideText】获胜！", Toast.LENGTH_LONG).show()
		}
	}

//	fun saveChessBoardContent() {
//		chessBoardContent.cloneTo(GlobalVars.savedChessBoardContent)
//		Log.d(LOG_TAG, "save=${GlobalVars.savedChessBoardContent}")
//		Log.d(LOG_TAG, "saveLastMove=${chessBoardContent.lastMove}")
//	}

	fun reloadChessBoardContent() {
//		Log.d(LOG_TAG, "load=${GlobalVars.savedChessBoardContent}")
//		GlobalVars.savedChessBoardContent.cloneTo(chessBoardContent)
		applyChessboardContent()
//		Log.d(LOG_TAG, "lastMove=${chessBoardContent.lastMove}")
//		updateLastMove()
	}

}
