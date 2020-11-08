@file:JvmName(name = "MainClass")

package me.evgem.client

import me.evgem.client.command.parser.CommandParser
import me.evgem.client.di.getCommandHandlerProvider
import me.evgem.client.di.getConnector
import me.evgem.client.di.getMessageHandlerProvider
import me.evgem.client.model.Command
import me.evgem.domain.utils.Log
import java.util.*

fun main() {
    val scanner = Scanner(System.`in`)

    val client = Client(
        getCommandHandlerProvider(),
        getConnector(),
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