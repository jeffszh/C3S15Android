package cn.jeff.game.c3s15

import android.app.Application
import cn.jeff.game.c3s15.net.MqttDaemon
import cn.jeff.game.c3s15.net.NetworkGameProcessor

class AppMain : Application() {

	override fun onCreate() {
		super.onCreate()
		MqttDaemon.start()
		NetworkGameProcessor.start()
	}

	override fun onTerminate() {
		super.onTerminate()
		MqttDaemon.stop()
		NetworkGameProcessor.stop()
	}

}
