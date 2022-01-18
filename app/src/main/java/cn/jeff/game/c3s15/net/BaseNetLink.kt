package cn.jeff.game.c3s15.net

import com.google.gson.GsonBuilder

abstract class BaseNetLink : AutoCloseable {

	companion object {
		internal const val LINK_TIMEOUT = 8000L
		internal val gson = GsonBuilder().setPrettyPrinting().create()
	}

	protected var onConnectFunc: () -> Unit = {}
	protected var onReceiveFunc: (String) -> Unit = {}
	protected var onErrorFunc: (Exception) -> Unit = {}

	var connected = false
		protected set

	fun onConnect(func: () -> Unit) {
		onConnectFunc = func
	}

	fun onReceive(func: (data: String) -> Unit) {
		onReceiveFunc = func
	}

	fun onError(func: (e: Exception) -> Unit) {
		onErrorFunc = func
	}

	abstract fun sendData(data: String)

}
