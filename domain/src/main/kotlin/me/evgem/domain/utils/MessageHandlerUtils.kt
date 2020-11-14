package me.evgem.domain.utils

import me.evgem.domain.connection.IConnection
import me.evgem.domain.model.IMessageHandler
import me.evgem.domain.model.Message

inline fun <M : Message> messageHandler(
    crossinline block: suspend (message: M, connection: IConnection) -> Unit
): IMessageHandler<M> = object : IMessageHandler<M> {
    override suspend fun handle(message: M, connection: IConnection) {
        block(message, connection)
    }
}
