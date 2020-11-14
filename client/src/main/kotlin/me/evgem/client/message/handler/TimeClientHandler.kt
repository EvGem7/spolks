package me.evgem.client.message.handler

import me.evgem.domain.connection.IConnection
import me.evgem.domain.model.IMessageHandler
import me.evgem.domain.model.Message
import me.evgem.domain.utils.Log
import java.util.*

object TimeClientHandler : IMessageHandler<Message.Time> {
    override suspend fun handle(message: Message.Time, connection: IConnection) {
        Log.i("time from server: ${message.time?.let { Date(it) }}")
    }
}