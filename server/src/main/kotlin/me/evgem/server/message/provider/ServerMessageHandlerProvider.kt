package me.evgem.server.message.provider

import me.evgem.domain.model.IMessageHandler
import me.evgem.domain.model.IMessageHandlerProvider
import me.evgem.domain.model.Message
import me.evgem.domain.utils.NothingMessageHandler
import me.evgem.domain.utils.PingMessageHandler
import me.evgem.server.message.handler.CloseServerHandler
import me.evgem.server.message.handler.DownloadServerHandler
import me.evgem.server.message.handler.EchoServerHandler
import me.evgem.server.message.handler.TimeServerHandler

@Suppress("UNCHECKED_CAST")
class ServerMessageHandlerProvider : IMessageHandlerProvider {

    private val downloadHandler = DownloadServerHandler()

    override fun <M : Message> provide(message: M): IMessageHandler<M> = when (message as Message) {
        is Message.Echo -> EchoServerHandler
        is Message.Time -> TimeServerHandler
        is Message.Close -> CloseServerHandler

        is Message.DownloadStartRequest -> downloadHandler.getDownloadStartRequestHandler()
        is Message.DownloadStartResponse -> NothingMessageHandler
        is Message.Download -> NothingMessageHandler
        is Message.DownloadFinished -> NothingMessageHandler
        is Message.DownloadWait -> downloadHandler.getDownloadWaitHandler()

        is Message.UploadRequest -> NothingMessageHandler
        is Message.UploadResponse -> NothingMessageHandler
        is Message.Upload -> NothingMessageHandler
        is Message.UploadFinished -> NothingMessageHandler

        is Message.Ping -> PingMessageHandler
    } as IMessageHandler<M>
}
