package me.evgem.server.message.provider

import me.evgem.domain.model.IMessageHandler
import me.evgem.domain.model.IMessageHandlerProvider
import me.evgem.domain.model.Message
import me.evgem.domain.utils.NothingMessageHandler
import me.evgem.domain.utils.PingMessageHandler
import me.evgem.server.message.handler.*

@Suppress("UNCHECKED_CAST")
class ServerMessageHandlerProvider(
    private val downloadHandler:DownloadServerHandler,
    private val uploadHandler: UploadServerHandler,
) : IMessageHandlerProvider {


    override fun <M : Message> provide(message: M): IMessageHandler<M> = when (message as Message) {
        is Message.Echo -> EchoServerHandler
        is Message.Time -> TimeServerHandler
        is Message.Close -> CloseServerHandler

        is Message.DownloadStartRequest -> downloadHandler.getDownloadStartRequestHandler()
        is Message.DownloadWait -> downloadHandler.getDownloadWaitHandler()

        is Message.UploadStartRequest -> uploadHandler.getUploadStartRequestHandler()
        is Message.UploadWait -> uploadHandler.getUploadWaitHandler()
        is Message.Upload -> uploadHandler.getUploadHandler()
        is Message.UploadFinished -> uploadHandler.getUploadFinishedHandler()

        is Message.Ping -> PingMessageHandler

        else -> NothingMessageHandler
    } as IMessageHandler<M>
}
