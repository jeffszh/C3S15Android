package cn.jeff.game.c3s15.brain

import cn.jeff.game.c3s15.GlobalVars
import cn.jeff.game.c3s15.board.Chess
import cn.jeff.game.c3s15.board.ChessBoardContent
import cn.jeff.game.c3s15.event.AiTraversalEvent
import cn.jeff.game.c3s15.event.MoveChessEvent
import org.greenrobot.eventbus.EventBus
import kotlin.concurrent.thread
import kotlin.random.Random

object Brain {

	private val brainThread = thread {
		brainMain()
	}
	private val chessBoardContent = ChessBoardContent()
	private val maxDepth get() = GlobalVars.appConf.aiDepth

	private fun brainMain() {
		try {
			Thread.sleep(1000)
		} catch (e: InterruptedException) {
			// do nothing
		}
		while (true) {
			try {
				aiRoutine()
				Thread.sleep(Long.MAX_VALUE)
			} catch (e: InterruptedException) {
				// do nothing
			}
		}
	}

	private fun aiRoutine() {
		val chessBoardContent = this.chessBoardContent.clone()
		if (when (chessBoardContent.whoseTurn) {
				Chess.EMPTY -> null
				Chess.SOLDIER -> GlobalVars.soldiersPlayerType
				Chess.CANNON -> GlobalVars.cannonsPlayerType
			} != PlayerType.AI
		) return

		GlobalVars.aiTraversalCount = 0
		Thread.sleep(300)
		val bestMove = findBestMove(chessBoardContent.clone(), 0).first
		if (bestMove != null) {
			EventBus.getDefault().post(MoveChessEvent(bestMove))
		}
	}

	private fun findBestMove(
		content: ChessBoardContent,
		currentDepth: Int
	): Pair<ChessBoardContent.Move?, Int> {
		Thread.sleep(0)
		GlobalVars.aiTraversalCount++
		if (GlobalVars.aiTraversalCount % 8192 == 16) {
			EventBus.getDefault().post(AiTraversalEvent())
		}
		// 到达最大深度了，直接评估并返回。
		if (currentDepth >= maxDepth) {
			return null to evaluateSituation(content, currentDepth)
		}
		val allPossibleMove = findAllPossibleMove(content)
		// 若没有可以走的棋了，直接评估返回。
		if (allPossibleMove.isEmpty() || content.gameOver) {
			// 实际上若是空集，则一定已经gameOver了。
			return null to evaluateSituation(content, currentDepth)
		}
		val allPossibleMoveAndEval = allPossibleMove.map { move ->
			move to findBestMove(content.clone().apply {
				applyMove(move)
			}, currentDepth + 1).second
		}
		val allEval = allPossibleMoveAndEval.map { it.second }
		val bestEval = if (content.whoseTurn == Chess.CANNON) {
			// 若轮到炮走，求对炮最有利的局面，反之亦然。
			allEval.minOrNull()
		} else {
			allEval.maxOrNull()
		} ?: error("不可能！前面已经检查过空集了。")
		val bestMoveList = allPossibleMoveAndEval.filter {
			it.second == bestEval
		}
		// 只有深度為零的時候才有必要計算隨機
		return if (currentDepth == 0 && bestMoveList.count() > 1) {
			bestMoveList[Random.nextInt(bestMoveList.count())]
		} else {
			bestMoveList[0]
		}
	}

	private fun findAllPossibleMove(content: ChessBoardContent) =
		// 很粗暴的方法，直接无脑穷举。
		(0..4).flatMap { y ->
			(0..4).flatMap { x ->
				val delta = listOf(
					0, 1, 0, -1, 1, 0, -1, 0,
					0, 2, 0, -2, 2, 0, -2, 0,
				)
				(0 until delta.count() / 2).map { i ->
					val dx = delta[i * 2]
					val dy = delta[i * 2 + 1]
					ChessBoardContent.Move(x, y, x + dx, y + dy)
				}
				/*listOf(
					ChessBoardContent.Move(x, y, x + 1, y),
					ChessBoardContent.Move(x, y, x - 1, y),
					ChessBoardContent.Move(x, y, x, y + 1),
					ChessBoardContent.Move(x, y, x, y - 1),
					ChessBoardContent.Move(x, y, x + 2, y),
					ChessBoardContent.Move(x, y, x - 2, y),
					ChessBoardContent.Move(x, y, x, y + 2),
					ChessBoardContent.Move(x, y, x, y - 2),
				)*/
			}
		}.filter {
			content.isMoveValid(it)
		}

	/**
	 * # 局面评估
	 *
	 * 数值越大对兵方越有利。
	 */
//	private fun evaluateSituation(content: ChessBoardContent) =
//		content.livingSoldierCount() * 256 - content.cannonBreathCount()
	private fun evaluateSituation(content: ChessBoardContent, currentDepth: Int) = run {
		val livingSoldierCount = content.livingSoldierCount()
		val cannonBreathCount = content.cannonBreathCount()
		when {
			// 加大分出勝負的分值，讓AI敢於棄兵贏棋。
			cannonBreathCount == 0 -> 0x10000
			livingSoldierCount == 0 -> -0x10000
			else -> livingSoldierCount * 256 - cannonBreathCount * 16
		} + if ((content.whoseTurn == Chess.CANNON) == (currentDepth / 2 == 0)) {
			// 改進一下，讓AI傾向於用較少的步數取得勝利。
			// 若當前深度是偶數，頂層就跟當前層是同一方，
			// 所以這條分支是正在計算炮方的最佳走法，將會求評估值的最小值，
			// 步數多應該使評估值大些，所以加上正的[currentDepth]。
			currentDepth
		} else {
			// 反之，兵方的評估值應該跟步數負相關。
			-currentDepth
		}
	}

	fun restartAiRoutine(chessBoardContent: ChessBoardContent) {
		chessBoardContent.cloneTo(this.chessBoardContent)
		brainThread.interrupt()
	}

}
