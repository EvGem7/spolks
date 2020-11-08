package me.evgem.domain.model

import me.evgem.domain.connection.IConnection

interface IMessageHandler <M : Message> {
    suspend fun handle(message: M, connection: IConnection)
}
