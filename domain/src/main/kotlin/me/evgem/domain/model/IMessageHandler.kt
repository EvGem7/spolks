package me.evgem.domain.model

interface IMessageHandler <M : Message> {
    suspend fun handle(message: M, connection: IConnection)
}
