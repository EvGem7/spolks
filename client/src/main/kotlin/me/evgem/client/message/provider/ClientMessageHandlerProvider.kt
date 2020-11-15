package me.evgem.client.message.provider

import me.evgem.client.message.handler.*
import me.evgem.domain.model.IMessageHandler
import me.evgem.domain.model.IMessageHandlerProvider
import me.evgem.domain.model.Message
import me.evgem.domain.utils.NothingMessageHandler
import me.evgem.domain.utils.PingMessageHandler

@Suppress("UNCHECKED_CAST")
class ClientMessageHandlerProvider(
    private val downloadHandler: DownloadClientHandler,
    private val uploadHandler: UploadClientHandler,
) : IMessageHandlerProvider {

    override fun <M : Message> provide(message: M): IMessageHandler<M> = when (message as Message) {
        is Message.Echo -> EchoClientHandler
        is Message.Time -> TimeClientHandler
        is Message.Close -> CloseClientHandler

        is Message.DownloadStartRequest -> NothingMessageHandler
        is Message.DownloadStartResponse -> downloadHandler.getDownloadStartResponseHandler()
        is Message.DownloadWait -> NothingMessageHandler
        is Message.Download -> downloadHandler.getDownloadHandler()
        is Message.DownloadFinished -> downloadHandler.getDownloadFinishedHandler()

        is Message.UploadStartRequest -> NothingMessageHandler
        is Message.UploadStartResponse -> uploadHandler.getUploadStartResponseHandler()
        is Message.UploadWait -> uploadHandler.getUploadWaitHandler()
        is Message.Upload -> NothingMessageHandler
        is Message.UploadFinished -> NothingMessageHandler

        is Message.Ping -> PingMessageHandler
    } as IMessageHandler<M>
}
