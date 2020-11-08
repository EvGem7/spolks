package me.evgem.server.message.handler

import me.evgem.domain.connection.IConnection
import me.evgem.domain.model.IMessageHandler
import me.evgem.domain.model.Message
import me.evgem.domain.utils.Log

object EchoServerHandler : IMessageHandler<Message.Echo> {

    override suspend fun handle(message: Message.Echo, connection: IConnection) {
        Log.i("echo from client: ${message.msg}")
        connection.send(message)
    }
}
