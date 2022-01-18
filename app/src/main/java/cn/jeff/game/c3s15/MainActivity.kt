package cn.jeff.game.c3s15

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import cn.jeff.game.c3s15.board.Chess
import cn.jeff.game.c3s15.board.ChessBoardContent
import cn.jeff.game.c3s15.brain.Brain
import cn.jeff.game.c3s15.brain.PlayerType
import cn.jeff.game.c3s15.event.*
import cn.jeff.game.c3s15.net.MqttDaemon
import cn.jeff.game.c3s15.net.MqttLink
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
		tv01.keepScreenOn = true
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
	fun onNetGameStateChangedEvent(event: NetStatusChangeEvent) {
		updateStatusText4()
	}

	fun btnClick(view: View) {
		when (view.id) {
			R.id.btnRestartGame -> {
				chessBoard.restartGame()
				// NetworkGameProcessor.restart()
				showConnectDialogIfNeed()
			}
			R.id.btnSettings -> {
				showSettingsDialog()
			}
			R.id.btnChannel -> {
				showChangeChannelDialog()
			}
			R.id.btnAiDepth -> {
				showChangeAiDepthDialog()
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

	/*private fun updateStatusText4(state: NetGameState) {
		val note = when (state) {
			NetGameState.OFFLINE,
			NetGameState.GAME_OVER,
			NetGameState.LOST_CONN -> "离线"
			NetGameState.INVITING,
			NetGameState.WAIT_INV -> "正在寻找网络对手……"
			NetGameState.LOCAL_TURN,
			NetGameState.REMOTE_TURN -> "已连线"
		}
		val txt = "$note  $state"
		tv04.text = txt
	}*/
	private fun updateStatusText4() {
		tv04.text = if (GlobalVars.mqttLink == null)
			"未连线"
		else
			"已连线"
	}

	private fun showSettingsDialog() {
		val items = arrayOf(
			"${Chess.CANNON.text}：人腦  ${Chess.SOLDIER.text}：電腦",
			"${Chess.CANNON.text}：電腦  ${Chess.SOLDIER.text}：人腦",
			"${Chess.CANNON.text}：人腦  ${Chess.SOLDIER.text}：人腦",
			"${Chess.CANNON.text}：電腦  ${Chess.SOLDIER.text}：電腦",
			"${Chess.CANNON.text}：自己  ${Chess.SOLDIER.text}：网友",
			"${Chess.CANNON.text}：网友  ${Chess.SOLDIER.text}：自己",
		)
		var choice = when (GlobalVars.cannonsPlayerType to GlobalVars.soldiersPlayerType) {
			PlayerType.HUMAN to PlayerType.AI -> 0
			PlayerType.AI to PlayerType.HUMAN -> 1
			PlayerType.HUMAN to PlayerType.HUMAN -> 2
			PlayerType.AI to PlayerType.AI -> 3
			PlayerType.HUMAN to PlayerType.NET -> 4
			PlayerType.NET to PlayerType.HUMAN -> 5
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
					4 -> PlayerType.HUMAN to PlayerType.NET
					5 -> PlayerType.NET to PlayerType.HUMAN
					else -> null
				}?.apply {
					GlobalVars.cannonsPlayerType = first
					GlobalVars.soldiersPlayerType = second
					EventBus.getDefault().post(ConfigChangedEvent(LOG_TAG))
				}
			}.show()
	}

	private fun showChangeChannelDialog() {
		val editText = EditText(this)
		editText.inputType = InputType.TYPE_CLASS_NUMBER
		editText.setText(MqttDaemon.channelNum.toString())
		AlertDialog.Builder(this)
			.setTitle("对战通道")
			.setView(editText)
			.setPositiveButton("确定") { _, _ ->
				val txt = editText.text.toString()
				val num = txt.toIntOrNull() ?: -1
				if (num in 0..99999) {
					MqttDaemon.channelNum = num
					GlobalVars.saveConf()
				} else {
					// 安卓真蠢！要点击按钮不关闭，只能通过非常手段才能做到，不理了。
					Toast.makeText(
						this, "无效通道号，对战通道未改变，请输入5位以内的数字。",
						Toast.LENGTH_LONG
					).show()
				}
			}.show()
	}

	private fun showChangeAiDepthDialog() {
		val editText = EditText(this)
		editText.inputType = InputType.TYPE_CLASS_NUMBER
		editText.setText(GlobalVars.appConf.aiDepth.toString())
		AlertDialog.Builder(this)
			.setTitle("AI强度（1-9）")
			.setView(editText)
			.setPositiveButton("确定") { _, _ ->
				val txt = editText.text.toString()
				val num = txt.toIntOrNull() ?: -1
				if (num in 1..9) {
					GlobalVars.appConf.aiDepth = num
					GlobalVars.saveConf()
				} else {
					// 安卓真蠢！要点击按钮不关闭，只能通过非常手段才能做到，不理了。
					Toast.makeText(
						this, "输入无效，AI强度必须是1-9。",
						Toast.LENGTH_LONG
					).show()
				}
			}.show()
	}

	private fun showConnectDialogIfNeed() {
		if (GlobalVars.cannonsPlayerType == PlayerType.NET ||
			GlobalVars.soldiersPlayerType == PlayerType.NET
		) {
			GlobalVars.mqttLink?.close()
			GlobalVars.mqttLink = null
			AlertDialog.Builder(this)
				.setTitle("连接方式")
				.setPositiveButton("互联网") { _, _ ->
					showMqttWaitConnectDialog()
				}
				.setNeutralButton("蓝牙") { _, _ ->
					// TODO
				}
				.show()
		}
	}

	private fun showMqttWaitConnectDialog() {
		val (title, initiative) = if (GlobalVars.cannonsPlayerType == PlayerType.NET) {
			"正在等待网友连接……" to false
		} else {
			"正在连接网友……" to true
		}
		GlobalVars.mqttLink?.close()
		GlobalVars.mqttLink = null
		MqttLink(initiative) {
			val dialog = AlertDialog.Builder(this@MainActivity)
				.setTitle(title)
				.setCancelable(false)
				.setOnDismissListener {
					if (!connected) {
						// 若未连接，必须关掉（否则有bug）。
						close()
					}
				}
				.setNegativeButton("取消") { dialog, _ ->
					dialog.dismiss()
				}
				.show()
			dialog.setCanceledOnTouchOutside(false)
			onConnect {
				runOnUiThread {
					dialog.dismiss()
					Toast.makeText(
						this@MainActivity, "成功连接网友。",
						Toast.LENGTH_SHORT
					).show()
					GlobalVars.mqttLink = this
				}
			}
			onError {
				runOnUiThread {
					dialog.dismiss()
					Toast.makeText(
						this@MainActivity, "出错：${it.message}",
						Toast.LENGTH_SHORT
					).show()
					GlobalVars.mqttLink?.close()
					GlobalVars.mqttLink = null
				}
			}
			onReceive {
				NetworkGameProcessor.onMqttReceived(it)
			}
		}
	}

}
