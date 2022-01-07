package cn.jeff.game.c3s15.board

import cn.jeff.game.c3s15.GlobalVars

enum class Chess {

	EMPTY {
		override val oppositeSide = EMPTY
		override val text = " "
	},
	SOLDIER {
		override val oppositeSide get() = CANNON
		override val text get() = GlobalVars.appConf.soldierText
	},
	CANNON {
		override val oppositeSide = SOLDIER
		override val text get() = GlobalVars.appConf.cannonText
	},
	;

//	fun oppositeSide() {
//		when (this) {
//			EMPTY -> EMPTY
//			SOLDIER -> CANNON
//			CANNON -> SOLDIER
//		}
//	}

	abstract val oppositeSide: Chess
	abstract val text: String

}
