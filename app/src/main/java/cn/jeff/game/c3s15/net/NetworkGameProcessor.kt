package cn.jeff.game.c3s15.net

import cn.jeff.game.c3s15.GlobalVars
import cn.jeff.game.c3s15.board.ChessBoardContent
import cn.jeff.game.c3s15.event.MoveChessEvent
import com.google.gson.GsonBuilder
import org.greenrobot.eventbus.EventBus

object NetworkGameProcessor {

	private val gson = GsonBuilder().setPrettyPrinting().create()
	private val localChessBoard get() = GlobalVars.chessBoardContent

	/** 底層連接收到消息时，从这里通知进来。 */
	fun onDataReceived(txtPayload: String) {
		val msg = try {
			gson.fromJson(txtPayload, GameMessage::class.java)
		} catch (e: Exception) {
			e.printStackTrace()
			return
		}
		// 判断跟本地棋盘是否一致。
		val newChessBoardContent = localChessBoard.clone()
		newChessBoardContent.applyMove(msg.lastMove)
		if (newChessBoardContent.compressToInt64() == msg.packedChessCells) {
			// 棋盘一致，确认走棋。
			EventBus.getDefault().post(MoveChessEvent(msg.lastMove, true))
		}
	}

	/** 本地走棋，从这里通知进来。 */
	fun applyLocalMove(packedChessCells: Long, move: ChessBoardContent.Move) {
		GlobalVars.netLink?.sendData(
			gson.toJson(
				GameMessage(
					packedChessCells, move
				)
			)
		)
	}

}
