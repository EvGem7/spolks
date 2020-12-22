@file:JvmName(name = "MainClass")

package me.evgem.server

import me.evgem.domain.utils.Log
import me.evgem.domain.utils.forceUseUdp
import me.evgem.server.di.getConnectionListener
import me.evgem.server.di.getMessageHandlerProvider
import java.util.*

fun main(args: Array<String>) {
    val isTcp = args.getOrElse(0) {
        if (forceUseUdp) "udp" else "tcp"
    }.let {
        Log.i("using $it")
        it == "tcp"
    }

    val server = Server(
        getConnectionListener(isTcp),
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