package cn.jeff.game.c3s15.net

import kotlin.concurrent.thread

class BluetoothLink(initiative: Boolean, op: BaseNetLink.() -> Unit) : BaseNetLink(op) {

	private var workThread: Thread? = null
	private var heartBeatThread: Thread? = null

	init {
		workThread = thread(name = "BLUETOOTH_LINK_WORK_THREAD") {
			MqttDaemon.clearReceivingQueue()
			try {
				if (initiative) {
					runInitiative()
				} else {
					runPassive()
				}
			} catch (e: InterruptedException) {
				// do nothing
			} catch (e: Exception) {
				onErrorFunc(e)
			}
		}
	}

	private fun runInitiative() {
	}

	private fun runPassive() {}

	override fun sendData(data: String) {
		TODO("Not yet implemented")
	}

	override fun close() {
		TODO("Not yet implemented")
	}

}
