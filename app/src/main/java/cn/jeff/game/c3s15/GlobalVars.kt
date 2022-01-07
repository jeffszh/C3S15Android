package cn.jeff.game.c3s15

import android.os.Environment
import cn.jeff.game.c3s15.board.ChessBoardContent
import cn.jeff.game.c3s15.brain.PlayerType
import cn.jeff.game.c3s15.event.ConfigChangedEvent
import com.google.gson.GsonBuilder
import org.greenrobot.eventbus.EventBus
import java.io.FileReader
import java.io.IOException
import kotlin.concurrent.thread

/**
 * # 全局变量
 *
 * 无视安卓的无理要求，就是直接用全局变量。
 */
object GlobalVars {

	private const val LOG_TAG = "GlobalVars"
	private const val confFilename = "c3s15.conf.txt"
	private val gson = GsonBuilder().setPrettyPrinting().create()
	var appConf = AppConf()
		private set

	init {
		loadConf()
//		saveConf()
		thread {
			Thread.sleep(500)
			EventBus.getDefault().post(ConfigChangedEvent(LOG_TAG))
		}
	}

	private fun loadConf(filename: String = confFilename) {
		try {
			@Suppress("DEPRECATION")
			FileReader(
				Environment.getExternalStorageDirectory().absolutePath
						+ "/" + filename
			).use { reader ->
				appConf = gson.fromJson(reader, AppConf::class.java)
			}
		} catch (e: IOException) {
			e.printStackTrace()
		}
	}

//	private fun saveConf(filename: String = confFilename) {
//		@Suppress("DEPRECATION")
//		FileWriter(
//			Environment.getExternalStorageDirectory().absolutePath
//					+ "/" + filename
//		).use { writer ->
//			gson.toJson(appConf, writer)
//		}
//	}

	var cannonsPlayerType = PlayerType.HUMAN
	var soldiersPlayerType = PlayerType.HUMAN
	var aiTraversalCount = 0

	val savedChessBoardContent = ChessBoardContent().apply { setInitialContent() }

//	val cannonsUseAIProperty = SimpleBooleanProperty(false)
//	var cannonsUseAI: Boolean
//		get() = cannonsUseAIProperty.value
//		set(value) {
//			cannonsUseAIProperty.value = value
//		}
//
//	val soldiersUseAIProperty = SimpleBooleanProperty(true)
//	var soldiersUseAI: Boolean
//		get() = soldiersUseAIProperty.value
//		set(value) {
//			soldiersUseAIProperty.value = value
//		}
//
//	val aiTraversalCountProperty = SimpleIntegerProperty(0)
//	val aiTraversalCount: Int get() = aiTraversalCountProperty.value

}
