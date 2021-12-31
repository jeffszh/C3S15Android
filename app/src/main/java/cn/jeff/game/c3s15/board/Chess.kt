package cn.jeff.game.c3s15.board

enum class Chess{

	EMPTY {
		override val oppositeSide = EMPTY
	},
	SOLDIER{
		override val oppositeSide get() = CANNON
	},
	CANNON{
		override val oppositeSide = SOLDIER
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

}
