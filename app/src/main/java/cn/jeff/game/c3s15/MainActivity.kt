package cn.jeff.game.c3s15

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import cn.jeff.game.c3s15.board.ChessBoardContent
import cn.jeff.game.c3s15.event.ChessBoardContentChangedEvent
import cn.jeff.game.c3s15.event.ConfigChangedEvent
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MainActivity : Activity() {

	companion object {
		private const val LOG_TAG = "MainActivity"
//		var instance: MainActivity? = null
	}

	private val cannonText
		get() = GlobalVars.appConf.cannonText
	private val soldierText
		get() = GlobalVars.appConf.soldierText

//	val tv01 = findViewById<TextView>(R.id.tv01)

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
//		findViewById<FrameLayout>(R.id.pan01).addView(ChessBoard(this))
//		val tv01 = findViewById<TextView>(R.id.tv01)
		tv01.text = GlobalVars.appConf.mainTitle
	}

	override fun onStart() {
		super.onStart()
		EventBus.getDefault().register(this)
	}

	override fun onStop() {
		EventBus.getDefault().unregister(this)
		super.onStop()
	}

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
	}

	@Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
	fun onChessBoardContentChanged(event: ChessBoardContentChangedEvent) {
		updateStatusText2(event.chessBoardContent)
	}

	fun btnClick(view: View) {
		when (view.id) {
			R.id.btnRestartGame -> {
			}
			R.id.btnSettings -> {
			}
		}
	}

	private fun updateStatusText1() {
		val status1 = "$cannonText：${
			if (GlobalVars.cannonsUseAI) "電腦" else "人腦"
		}  $soldierText：${
			if (GlobalVars.soldiersUseAI) "電腦" else "人腦"
		}"
		tv02.text = status1
	}

	private fun updateStatusText2(chessBoardContent: ChessBoardContent) {
		val status2 = if (chessBoardContent.gameOver) {
			if (chessBoardContent.isCannonsWin)
				"【$cannonText】获胜！"
			else
				"【$soldierText】获胜！"
		} else {
			if (chessBoardContent.isCannonsTurn) {
				if (GlobalVars.cannonsUseAI)
					"电脑【$cannonText】" +
							"正在思考：${GlobalVars.aiTraversalCount}"
				else
					"轮到玩家【$cannonText】走棋"
			} else {
				if (GlobalVars.soldiersUseAI)
					"电脑【$soldierText】" +
							"正在思考：${GlobalVars.aiTraversalCount}"
				else
					"轮到玩家【$soldierText】走棋"
			}
		}
		tv03.text = status2
	}

}
