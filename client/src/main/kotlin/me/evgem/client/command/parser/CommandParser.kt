package me.evgem.client.command.parser

import me.evgem.client.model.Command
import me.evgem.client.model.CommandType
import me.evgem.domain.utils.Log

object CommandParser {

    fun parse(cmd: String): Command? {
        val parts = cmd.split(' ')
        if (parts.isEmpty()) {
            return null
        }
        val type = CommandType.fromKey(parts[0]) ?: return null
        return try {
            when (type) {
                CommandType.Echo -> Command.Echo(parts[1])
                CommandType.Time -> Command.Time
                CommandType.Close -> Command.Close
                CommandType.Download -> Command.Download(parts[1])
                CommandType.Upload -> Command.Upload(parts[1])
                CommandType.Stop -> Command.Stop
                CommandType.Connect -> Command.Connect(parts[1], parts[2].toInt())
            }.also {
                Log.i("${it::class.java.simpleName} command parsed")
            }
        } catch (e: Exception) {
            null
        }
    }
}
