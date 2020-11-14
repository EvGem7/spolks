package me.evgem.server.message.provider

import me.evgem.domain.model.IMessageHandler
import me.evgem.domain.model.IMessageHandlerProvider
import me.evgem.domain.model.Message
import me.evgem.server.message.handler.CloseServerHandler
import me.evgem.server.message.handler.EchoServerHandler
import me.evgem.server.message.handler.TimeServerHandler

@Suppress("UNCHECKED_CAST")
class ServerMessageHandlerProvider : IMessageHandlerProvider {

    override fun <M : Message> provide(message: M): IMessageHandler<M> = when (message as Message) {
        is Message.Echo -> EchoServerHandler
        is Message.Time -> TimeServerHandler
        is Message.Close -> CloseServerHandler

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
