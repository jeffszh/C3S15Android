package cn.jeff.game.c3s15.brain

import cn.jeff.game.c3s15.GlobalVars
import cn.jeff.game.c3s15.board.Chess
import cn.jeff.game.c3s15.board.ChessBoardContent
import kotlin.concurrent.thread

object Brain {

	private val brainThread = thread {
		brainMain()
	}
	private val chessBoardContent = ChessBoardContent()

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
		) {
			return
		}
	}

	fun restartAiRoutine(chessBoardContent: ChessBoardContent) {
		chessBoardContent.cloneTo(this.chessBoardContent)
		brainThread.interrupt()
	}

}
