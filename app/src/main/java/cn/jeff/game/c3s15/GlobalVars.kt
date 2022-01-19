package cn.jeff.game.c3s15

import cn.jeff.game.c3s15.board.ChessBoardContent
import cn.jeff.game.c3s15.brain.PlayerType
import cn.jeff.game.c3s15.event.ConfigChangedEvent
import cn.jeff.game.c3s15.event.NetStatusChangeEvent
import cn.jeff.game.c3s15.net.BaseNetLink
import com.google.gson.GsonBuilder
import org.greenrobot.eventbus.EventBus
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import kotlin.concurrent.thread

/**
 * # 全局变量
 *
 * 无视安卓的无理要求，就是直接用全局变量。
 */
object GlobalVars {

	private const val LOG_TAG = "GlobalVars"
	const val confFilename = "conf.txt"
	private val gson = GsonBuilder().setPrettyPrinting().create()
	var appConf = AppConf()
		private set

	init {
//		loadConf()
//		saveConf()
		thread {
			Thread.sleep(500)
			EventBus.getDefault().post(ConfigChangedEvent(LOG_TAG))
		}
	}

	fun loadConf(filename: String) {
		try {
//			@Suppress("DEPRECATION")
//			FileReader(
//				Environment.getExternalStorageDirectory().absolutePath
//						+ "/" + filename
//			).use { reader ->
			FileReader(filename).use { reader ->
				appConf = gson.fromJson(reader, AppConf::class.java)
			}
		} catch (e: IOException) {
			e.printStackTrace()
		}
	}

	fun saveConf(filename: String) {
//		@Suppress("DEPRECATION")
//		FileWriter(
//			Environment.getExternalStorageDirectory().absolutePath
//					+ "/" + filename
//		).use { writer ->
		FileWriter(filename).use { writer ->
			gson.toJson(appConf, writer)
		}
	}

	var cannonsPlayerType = PlayerType.HUMAN
	var soldiersPlayerType = PlayerType.AI
	var aiTraversalCount = 0

	val chessBoardContent = ChessBoardContent().apply { setInitialContent() }

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

	var netLink: BaseNetLink? = null
		set(value) {
			if (field != value) {
				field = value
				EventBus.getDefault().post(NetStatusChangeEvent())
			}
		}

	val isNetConnected get() = netLink?.connected ?: false

	const val BLUETOOTH_LINK_UUID = "b35835a0-d88d-4d70-836b-ecab87a5a11b"

}
