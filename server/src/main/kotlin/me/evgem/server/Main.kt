@file:JvmName(name = "MainClass")

package me.evgem.server

import me.evgem.domain.utils.Log
import me.evgem.server.di.getConnectionListener
import me.evgem.server.di.getMessageHandlerProvider
import java.util.*

fun main() {
    val server = Server(
        getConnectionListener(),
        getMessageHandlerProvider(),
    )
    server.start()
    Log.i("server started")

    Log.i("print \"stop\" to stop the server")
    val scanner = Scanner(System.`in`)
    for (cmd in scanner) {
        if (cmd == "stop") {
            break
        }
    }

    server.stop()
    Log.i("server stopped")
}