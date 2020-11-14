package me.evgem.server.message.handler

import me.evgem.domain.connection.IConnection
import me.evgem.domain.model.IMessageHandler
import me.evgem.domain.model.Message
import me.evgem.domain.utils.Log

object CloseServerHandler : IMessageHandler<Message.Close> {

    override suspend fun handle(message: Message.Close, connection: IConnection) {
        Log.i("close from client")
        connection.close()
    }
}
