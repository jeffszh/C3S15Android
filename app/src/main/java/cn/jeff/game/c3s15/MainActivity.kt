package cn.jeff.game.c3s15

import android.app.Activity
import android.os.Bundle

class MainActivity : Activity() {

//	val tv01 = findViewById<TextView>(R.id.tv01)

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
//		findViewById<FrameLayout>(R.id.pan01).addView(ChessBoard(this))
	}

}
