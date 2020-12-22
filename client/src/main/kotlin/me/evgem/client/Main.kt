@file:JvmName(name = "MainClass")

package me.evgem.client

import me.evgem.client.command.parser.CommandParser
import me.evgem.client.di.getCommandHandlerProvider
import me.evgem.client.di.getConnector
import me.evgem.client.di.getMessageHandlerProvider
import me.evgem.client.model.Command
import me.evgem.domain.utils.Log
import me.evgem.domain.utils.forceUseUdp
import java.util.*

fun main(args: Array<String>) {
    val scanner = Scanner(System.`in`)

    val isTcp = args.getOrElse(0) {
        if (forceUseUdp) "udp" else "tcp"
    }.let {
        Log.i("using $it")
        it == "tcp"
    }

    val client = Client(
        getCommandHandlerProvider(),
        getConnector(isTcp),
        getMessageHandlerProvider()
    )
    client.start()
    Log.i("client started")

    var command = CommandParser.parse(scanner.nextLine())
    while (command != Command.Stop) {
        if (command != null) {
            client.performCommand(command)
        }
        command = CommandParser.parse(scanner.nextLine())
    }

    client.stop()
    Log.i("client stopped")
}