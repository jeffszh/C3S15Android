package cn.jeff.game.c3s15.board

import kotlin.math.abs

/**
 * # 棋盘的内容
 *
 * 之所以单独将此类抽出来，是为了方便日后做AI计算。
 * 此类包含棋盘的当前局面和上一步棋，这两个信息在[ChessBoard]上面也必然要有，
 * 但[ChessBoard]上的内容会在界面上显示出来，
 * 而单独的[ChessBoardContent]，则可以用于安静的计算。
 */
class ChessBoardContent {

	private val chessList = MutableList(25) { Chess.EMPTY }
	var lastMove: Move? = null
	private var moveCount = 0
	val isCannonsTurn get() = (moveCount % 2) == 0
	var gameOver = false

	val whoseTurn
		get() = when {
			gameOver -> Chess.EMPTY
			isCannonsTurn -> Chess.CANNON
			else -> Chess.SOLDIER
		}
	val whoWin
		get() = when {
			!gameOver -> Chess.EMPTY
			isCannonsTurn -> Chess.SOLDIER
			else -> Chess.CANNON
		}

	operator fun get(x: Int, y: Int) = if (x in 0..4 && y in 0..4) chessList[x + y * 5] else null

	operator fun set(x: Int, y: Int, chess: Chess) {
		if (x in 0..4 && y in 0..4) {
			chessList[x + y * 5] = chess
		}
	}

	/**
	 * # 棋步
	 */
	class Move(val fromX: Int, val fromY: Int, val toX: Int, val toY: Int)

	/**
	 * 设置成开局局面。
	 */
	fun setInitialContent() {
		val str = """
			11111
			11111
			11111
			00000
			20202
		""".trimIndent().lines().joinToString("")
		println("[$str]")
		val content = str.map { c ->
			Chess.values()[c.toString().toInt()]
		}
		chessList.clear()
		chessList.addAll(content)
		lastMove = null
		moveCount = 0
		gameOver = false

		// 试验
		val compress = compressToInt64(chessList)
		println("压缩=$compress")
		val decompress = decompressFromInt64(compress)
		decompress.forEachIndexed { index, chess ->
			print(
				when (chess) {
					Chess.EMPTY -> "　"
					Chess.SOLDIER -> "兵"
					Chess.CANNON -> "炮"
				}
			)
			if (index % 5 == 4) {
				println()
			}
		}
	}

//	/**
//	 * 关联到界面上的棋盘格
//	 *
//	 * @param chessCells 用于在界面上显示的棋盘格
//	 */
//	fun attachToChessCells(chessCells: List<ChessCell>) {
//		chessList.onChange {
//			chessCells.forEachIndexed { index, chessCell ->
//				chessCell.chessProperty.value = chessList[index]
//			}
//		}
//	}

//	/**
//	 * 关联到界面上的“上一步棋”
//	 *
//	 * @param uiLastMove 在界面上显示的“上一步棋”
//	 */
//	fun attachToLastMove(uiLastMove: ObjectProperty<Move>) {
//		lastMove.bindBidirectional(uiLastMove)
//	}

	fun compressToInt64() = compressToInt64(chessList)

	private fun compressToInt64(chessList: List<Chess>): Long {
		var result = 0L
		for (i in chessList.indices) {
			result = result.shl(2)
			result += chessList[i].ordinal
		}
		return result
	}

	private fun decompressFromInt64(int64: Long): List<Chess> {
		val result = Array(25) { Chess.EMPTY }
		var i64 = int64
		for (i in result.count() - 1 downTo 0) {
			result[i] = Chess.values()[(i64 and 0x03).toInt()]
			i64 = i64.shr(2)
		}
		return result.toList()
	}

	fun isMoveValid(move: Move) = with(move) {
		val fromChess = this@ChessBoardContent[fromX, fromY] ?: return@with false
		val toChess = this@ChessBoardContent[toX, toY] ?: return@with false
		val dx = abs(toX - fromX)
		val dy = abs(toY - fromY)
		when {
			// 若同行或同列，dx或dy為零。
			dx * dy != 0 -> false
			isCannonsTurn -> {
				when {
					// 移动一格的情形
					fromChess == Chess.CANNON && toChess == Chess.EMPTY ->
						// 同行或同列距离1格
						dx + dy == 1
					// 吃的情形
					fromChess == Chess.CANNON && toChess == Chess.SOLDIER ->
						// 同行或同列距离2格
						dx + dy == 2 &&
								// 中间必须是空格
								this@ChessBoardContent[(fromX + toX) / 2, (fromY + toY) / 2
								] == Chess.EMPTY
					else -> false
				}
			}
			else -> {
				// 兵只有移动一格
				fromChess == Chess.SOLDIER && toChess == Chess.EMPTY &&
						dx + dy == 1
			}
		}
	}

	/**
	 * # 走棋
	 *
	 * 把[move]应用到棋盘内容，棋盘的内容会改变，
	 * 一系列属性（[moveCount]、[gameOver]等）也随之改变。
	 *
	 * @param move 欲应用的棋步
	 * @return 若成功（棋步合法），返回true。
	 */
	fun applyMove(move: Move): Boolean {
		return if (isMoveValid(move)) {
			this[move.toX, move.toY] = this[move.fromX, move.fromY]!!
			this[move.fromX, move.fromY] = Chess.EMPTY
			lastMove = move
			moveCount++
			gameOver = livingSoldierCount() == 0 || cannonBreathCount() == 0
			true
		} else {
			false
		}
	}

	/**
	 * 剩余的【兵】的数量
	 */
	fun livingSoldierCount() =
		chessList.count { it == Chess.SOLDIER }

	/**
	 * 【炮】的“气”的数量
	 */
	fun cannonBreathCount(): Int =
		// 找出所有空位的坐标
		chessList.mapIndexedNotNull { index, chess ->
			if (chess == Chess.EMPTY) {
				listOf(index % 5, index / 5)
			} else {
				null
			}
		}.filter { (x, y) ->
			// 找相邻有炮的
			listOf(
				this[x + 1, y],
				this[x - 1, y],
				this[x, y + 1],
				this[x, y - 1],
			).contains(Chess.CANNON)
		}.count()

	fun clone() = ChessBoardContent().also {
		cloneTo(it)
	}

	fun cloneTo(dst: ChessBoardContent) {
		dst.chessList.clear()
		dst.chessList.addAll(chessList)
		dst.moveCount = moveCount
		dst.lastMove = lastMove
		dst.gameOver = gameOver
	}

}
