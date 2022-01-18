package cn.jeff.game.c3s15.net

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import cn.jeff.game.c3s15.GlobalVars
import java.io.IOException
import java.util.*
import kotlin.concurrent.thread

class BluetoothLink(initiative: Boolean, op: BaseNetLink.() -> Unit) : BaseNetLink(op) {

	private var workThread: Thread? = null
	private var heartBeatThread: Thread? = null
	private var serverSocket: BluetoothServerSocket? = null
	private var socket: BluetoothSocket? = null

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
				doOnError(e)
			}
		}
	}

	private fun runInitiative() {
		val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
			?: throw IOException("此手機不支持藍牙！")
		val bondedDevices = bluetoothAdapter.bondedDevices
		if (bondedDevices.isNullOrEmpty()) {
			throw IOException("找不到藍牙設備，請確保藍牙已經打開。")
		}
		bondedDevices.forEach { dev ->
			println("----------------------------------------------------------")
			dev.uuids.forEach {
				println(it)
			}
			println("----------------------------------------------------------")
			println(dev.uuids.find {
				it.uuid == UUID.fromString(GlobalVars.BLUETOOTH_LINK_UUID)
			})
			println("----------------------------------------------------------")
		}
	}

	private fun runPassive() {
		val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
			?: throw IOException("此手機不支持藍牙！")
		serverSocket = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(
			"C3S15",
			UUID.fromString(GlobalVars.BLUETOOTH_LINK_UUID)
		)
		socket = serverSocket?.accept()

		// 馬上可以關掉，不會影響已連接好的socket。
		serverSocket?.close()
		serverSocket = null

		runConnected()
	}

	private fun runConnected() {
	}

	override fun sendData(data: String) {
		socket?.outputStream?.write(data.toByteArray())
	}

	override fun close() {
		workThread?.interrupt()
		workThread = null
		heartBeatThread?.interrupt()
		heartBeatThread = null
		serverSocket?.close()
		serverSocket = null
	}

}
