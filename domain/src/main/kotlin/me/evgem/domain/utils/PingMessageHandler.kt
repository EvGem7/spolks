package me.evgem.domain.utils

import kotlinx.coroutines.delay
import me.evgem.domain.connection.IConnection
import me.evgem.domain.model.IMessageHandler
import me.evgem.domain.model.Message

object PingMessageHandler : IMessageHandler<Message.Ping> {

    private const val PING_DELAY = 1000L

    override suspend fun handle(message: Message.Ping, connection: IConnection) {
        delay(PING_DELAY)
        connection.send(Message.Ping)
    }
}
