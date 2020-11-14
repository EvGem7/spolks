package me.evgem.server.message.handler

import me.evgem.domain.connection.IConnection
import me.evgem.domain.model.IMessageHandler
import me.evgem.domain.model.Message
import me.evgem.domain.utils.Log

object TimeServerHandler : IMessageHandler<Message.Time> {
    override suspend fun handle(message: Message.Time, connection: IConnection) {
        Log.i("time request from client")
        connection.send(Message.Time(System.currentTimeMillis()))
    }
}
