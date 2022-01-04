package cn.jeff.game.c3s15

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import cn.jeff.game.c3s15.event.ConfigChangedEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MainActivity : Activity() {

	companion object {
		private const val LOG_TAG = "MainActivity"
	}

//	val tv01 = findViewById<TextView>(R.id.tv01)

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
//		findViewById<FrameLayout>(R.id.pan01).addView(ChessBoard(this))
		val tv01 = findViewById<TextView>(R.id.tv01)
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

	@Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
	fun onConfChanged(event: ConfigChangedEvent) {
		Log.d(LOG_TAG, "配置改變，起因=${event.reason}。")
		findViewById<ChessBoard>(R.id.chessBoard).invalidate()
	}

}
