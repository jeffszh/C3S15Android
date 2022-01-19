package cn.jeff.game.c3s15.net

import android.app.AlertDialog
import android.bluetooth.*
import android.content.Context
import android.widget.CheckBox
import android.widget.Toast
import cn.jeff.game.c3s15.GlobalVars
import java.io.IOException
import java.util.*
import kotlin.concurrent.thread

class BluetoothLink(
	bluetoothDevice: BluetoothDevice,
	initiative: Boolean, op: BaseNetLink.() -> Unit
) : BaseNetLink(op) {

	private var workThread: Thread? = null
	private var heartBeatThread: Thread? = null
	private var serverSocket: BluetoothServerSocket? = null
	private var socket: BluetoothSocket? = null

	companion object {

		private val bluetoothAdapter
			get() = BluetoothAdapter.getDefaultAdapter()
				?: throw IOException("此手機不支持藍牙！")

		private fun enumBonedPhoneDevices(): List<Pair<String, BluetoothDevice>> {
			val bondedDevices = bluetoothAdapter.bondedDevices
			if (bondedDevices.isNullOrEmpty()) {
				throw IOException("找不到藍牙設備，請確保藍牙已經打開。")
			}
			return bondedDevices.filter { dev ->
				dev.bluetoothClass.majorDeviceClass in listOf(
					BluetoothClass.Device.Major.PHONE,
					BluetoothClass.Device.Major.COMPUTER,
				)
			}.map { dev ->
				dev.name to dev
			}
		}

		fun Context.selectDeviceByDialog(
			confFileName: String,
			onDeviceSelected: (BluetoothDevice) -> Unit
		) {
			try {
				val devices = enumBonedPhoneDevices()
				val items = devices.map {
					it.first
				}.toTypedArray()
				var choice = items.indexOf(GlobalVars.appConf.bluetoothFriend)
				if (choice in devices.indices) {
					onDeviceSelected(devices[choice].second)
					return
				}
				val doNotAskAgain = CheckBox(this).also {
					it.text = "不再询问"
				}
				AlertDialog.Builder(this)
					.setTitle("选择对方设备")
					.setView(doNotAskAgain)
					.setSingleChoiceItems(items, choice) { _, which ->
						choice = which
					}
					.setPositiveButton("确定") { _, _ ->
						if (choice in devices.indices) {
							if (doNotAskAgain.isChecked) {
								// 若要求不再询问，存到配置文件中去。
								GlobalVars.appConf.bluetoothFriend = devices[choice].first
								GlobalVars.saveConf(confFileName)
							}
							onDeviceSelected(devices[choice].second)
						}
					}
					.show()
			} catch (e: Exception) {
				e.printStackTrace()
				Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
			}
		}

	}

	init {
		workThread = thread(name = "BLUETOOTH_LINK_WORK_THREAD") {
			MqttDaemon.clearReceivingQueue()
			try {
				if (initiative) {
					runInitiative(bluetoothDevice)
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

	private fun runInitiative(bluetoothDevice: BluetoothDevice) {
		socket = bluetoothDevice.createRfcommSocketToServiceRecord(
			UUID.fromString(GlobalVars.BLUETOOTH_LINK_UUID)
		)
		socket?.connect()

		runConnected()
	}

	private fun runPassive() {
		serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(
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
		doOnConnect()
		val input = socket?.inputStream ?: return
		val buffer = ByteArray(2048)
		do {
			val recLen = input.read(buffer)
			val txt = String(buffer, 0, recLen, Charsets.UTF_8)
			doOnReceived(txt)
		} while (recLen > 0)
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
