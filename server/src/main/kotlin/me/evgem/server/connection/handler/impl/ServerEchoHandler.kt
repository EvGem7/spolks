package me.evgem.server.connection.handler.impl

import me.evgem.domain.connection.IConnection
import me.evgem.domain.model.IMessageHandler
import me.evgem.domain.model.Message

object ServerEchoHandler : IMessageHandler<Message.Echo> {

    override suspend fun handle(message: Message.Echo, connection: IConnection) {
        connection.send(message)
    }
}
