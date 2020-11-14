package me.evgem.client.message.provider

import me.evgem.client.message.handler.EchoClientHandler
import me.evgem.client.message.handler.TimeClientHandler
import me.evgem.domain.model.IMessageHandler
import me.evgem.domain.model.IMessageHandlerProvider
import me.evgem.domain.model.Message

@Suppress("UNCHECKED_CAST")
class ClientMessageHandlerProvider : IMessageHandlerProvider {

    override fun <M : Message> provide(message: M): IMessageHandler<M> = when (message as Message) {
        is Message.Echo -> EchoClientHandler
        is Message.Time -> TimeClientHandler
        is Message.Close -> TODO()
        is Message.DownloadRequest -> TODO()
        is Message.DownloadResponse -> TODO()
        is Message.Download -> TODO()
        is Message.DownloadFinished -> TODO()
        is Message.UploadRequest -> TODO()
        is Message.UploadResponse -> TODO()
        is Message.Upload -> TODO()
        is Message.UploadFinished -> TODO()
    } as IMessageHandler<M>
}
