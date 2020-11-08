package me.evgem.client.message.handler

import me.evgem.domain.connection.IConnection
import me.evgem.domain.model.IMessageHandler
import me.evgem.domain.model.Message
import me.evgem.domain.utils.Log

object EchoClientHandler : IMessageHandler<Message.Echo> {

    override suspend fun handle(message: Message.Echo, connection: IConnection) {
        Log.i("server echo: ${message.msg}")
    }
}
