package me.evgem.domain.model

interface IMessageHandlerProvider {
    fun <M : Message> provide(message: M): IMessageHandler<M>
}
