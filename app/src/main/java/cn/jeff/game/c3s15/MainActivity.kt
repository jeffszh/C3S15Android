package cn.jeff.game.c3s15

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import cn.jeff.game.c3s15.board.Chess
import cn.jeff.game.c3s15.board.ChessBoardContent
import cn.jeff.game.c3s15.brain.Brain
import cn.jeff.game.c3s15.brain.PlayerType
import cn.jeff.game.c3s15.event.*
import cn.jeff.game.c3s15.net.NetworkGameProcessor
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MainActivity : Activity() {

	companion object {
		private const val LOG_TAG = "MainActivity"
		// var instance: MainActivity? = null
	}

//	val tv01 = findViewById<TextView>(R.id.tv01)

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
//		findViewById<FrameLayout>(R.id.pan01).addView(ChessBoard(this))
//		val tv01 = findViewById<TextView>(R.id.tv01)
		tv01.text = GlobalVars.appConf.mainTitle
		// instance = this
	}

	override fun onStart() {
		super.onStart()
		val fn = getExternalFilesDir(null)!!.path + "/" + GlobalVars.confFilename
		GlobalVars.loadConf(fn)
		GlobalVars.saveConf(fn)
		EventBus.getDefault().register(this)
	}

	override fun onStop() {
		EventBus.getDefault().unregister(this)
		super.onStop()
	}

	override fun onResume() {
		super.onResume()
		chessBoard.reloadChessBoardContent()
		EventBus.getDefault().post(ConfigChangedEvent(LOG_TAG))
	}

//	override fun onPause() {
//		super.onPause()
//		chessBoard.saveChessBoardContent()
//	}

//	override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
//		super.onCreate(savedInstanceState, persistentState)
//		instance = this
//	}
//
//	override fun onDestroy() {
//		super.onDestroy()
//		instance = null
//	}

	@Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
	fun onConfChanged(event: ConfigChangedEvent) {
		Log.d(LOG_TAG, "配置改變，起因=${event.reason}。")
//		findViewById<ChessBoard>(R.id.chessBoard).invalidate()
		updateStatusText1()
		chessBoard.invalidate()
		Brain.restartAiRoutine(GlobalVars.chessBoardContent)
	}

	@Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
	fun onChessBoardContentChanged(event: ChessBoardContentChangedEvent) {
		updateStatusText2(event.chessBoardContent)
	}

	@Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
	fun onMoveChessEvent(event: MoveChessEvent) {
		chessBoard.applyMove(event.move, event.byRemote)
	}

	@Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
	fun onAiTraversalEvent(event: AiTraversalEvent) {
		updateStatusText2(GlobalVars.chessBoardContent)
	}

	@Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
	fun onNetGameStateChangedEvent(event: NetGameStateChangedEvent) {
		// TODO
	}

	fun btnClick(view: View) {
		when (view.id) {
			R.id.btnRestartGame -> {
				chessBoard.restartGame()
				NetworkGameProcessor.restart()
			}
			R.id.btnSettings -> {
				showSettingsDialog()
			}
		}
	}

	private fun updateStatusText1() {
		val status1 = "${Chess.CANNON.text}：${
			GlobalVars.cannonsPlayerType.text
		}  ${Chess.SOLDIER.text}：${
			GlobalVars.soldiersPlayerType.text
		}"
		tv02.text = status1
		tv01.text = GlobalVars.appConf.mainTitle
	}

	private fun updateStatusText2(chessBoardContent: ChessBoardContent) {
		val status2 = if (chessBoardContent.gameOver) {
			"【${chessBoardContent.whoWin.text}】获胜！"
		} else {
			val whoseTurnText = chessBoardContent.whoseTurn.text
			val playerType = when (chessBoardContent.whoseTurn) {
				Chess.EMPTY -> null
				Chess.SOLDIER -> GlobalVars.soldiersPlayerType
				Chess.CANNON -> GlobalVars.cannonsPlayerType
			}
			when (playerType) {
				PlayerType.HUMAN -> "轮到玩家【$whoseTurnText】走棋"
				PlayerType.AI -> "电脑【$whoseTurnText】" +
						"正在思考：${GlobalVars.aiTraversalCount}"
				PlayerType.NET -> "轮到对方【$whoseTurnText】走棋"
				null -> "轮到【$whoseTurnText】走棋"
			}
		}
		tv03.text = status2
	}

	private fun showSettingsDialog() {
		val items = arrayOf(
			"${Chess.CANNON.text}：人腦  ${Chess.SOLDIER.text}：電腦",
			"${Chess.CANNON.text}：電腦  ${Chess.SOLDIER.text}：人腦",
			"${Chess.CANNON.text}：人腦  ${Chess.SOLDIER.text}：人腦",
			"${Chess.CANNON.text}：電腦  ${Chess.SOLDIER.text}：電腦",
		)
		var choice = when (GlobalVars.cannonsPlayerType to GlobalVars.soldiersPlayerType) {
			PlayerType.HUMAN to PlayerType.AI -> 0
			PlayerType.AI to PlayerType.HUMAN -> 1
			PlayerType.HUMAN to PlayerType.HUMAN -> 2
			PlayerType.AI to PlayerType.AI -> 3
			else -> 0
		}
		AlertDialog.Builder(this)
			.setTitle("选项")
			.setSingleChoiceItems(items, choice) { _, which ->
				choice = which
			}.setPositiveButton("确定") { _, _ ->
				when (choice) {
					0 -> PlayerType.HUMAN to PlayerType.AI
					1 -> PlayerType.AI to PlayerType.HUMAN
					2 -> PlayerType.HUMAN to PlayerType.HUMAN
					3 -> PlayerType.AI to PlayerType.AI
					else -> null
				}?.apply {
					GlobalVars.cannonsPlayerType = first
					GlobalVars.soldiersPlayerType = second
					EventBus.getDefault().post(ConfigChangedEvent(LOG_TAG))
				}
			}.show()
	}

}
