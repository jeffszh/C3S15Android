package cn.jeff.game.c3s15

import android.app.Activity
import android.os.Bundle
import android.widget.TextView

class MainActivity : Activity() {

//	val tv01 = findViewById<TextView>(R.id.tv01)

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
//		findViewById<FrameLayout>(R.id.pan01).addView(ChessBoard(this))
		val tv01 = findViewById<TextView>(R.id.tv01)
		tv01.text = GlobalVars.appConf.mainTitle
	}

}
