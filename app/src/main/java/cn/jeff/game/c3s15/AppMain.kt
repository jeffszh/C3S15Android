package cn.jeff.game.c3s15

import android.app.Application
import cn.jeff.game.c3s15.net.MqttDaemon

@Suppress("unused")
class AppMain : Application() {

	override fun onCreate() {
		super.onCreate()
		MqttDaemon.start()
		// NetworkGameProcessor.start()
	}

	override fun onTerminate() {
		super.onTerminate()
		MqttDaemon.stop()
		// NetworkGameProcessor.stop()
		GlobalVars.mqttLink?.close()
		GlobalVars.mqttLink = null
	}

}
