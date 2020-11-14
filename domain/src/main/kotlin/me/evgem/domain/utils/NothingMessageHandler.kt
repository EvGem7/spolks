package me.evgem.domain.utils

import me.evgem.domain.connection.IConnection
import me.evgem.domain.model.IMessageHandler
import me.evgem.domain.model.Message

object NothingMessageHandler : IMessageHandler<Message> {
    override suspend fun handle(message: Message, connection: IConnection) = Unit
}
