package me.evgem.client.message.provider

import me.evgem.client.message.handler.CloseClientHandler
import me.evgem.client.message.handler.DownloadClientHandler
import me.evgem.client.message.handler.EchoClientHandler
import me.evgem.client.message.handler.TimeClientHandler
import me.evgem.domain.model.IMessageHandler
import me.evgem.domain.model.IMessageHandlerProvider
import me.evgem.domain.model.Message
import me.evgem.domain.utils.NothingMessageHandler
import me.evgem.domain.utils.PingMessageHandler

@Suppress("UNCHECKED_CAST")
class ClientMessageHandlerProvider(
    private val downloadHandler: DownloadClientHandler
) : IMessageHandlerProvider {

    override fun <M : Message> provide(message: M): IMessageHandler<M> = when (message as Message) {
        is Message.Echo -> EchoClientHandler
        is Message.Time -> TimeClientHandler
        is Message.Close -> CloseClientHandler
        is Message.DownloadRequest -> NothingMessageHandler
        is Message.DownloadResponse -> downloadHandler.getDownloadResponseHandler()
        is Message.Download -> downloadHandler.getDownloadHandler()
        is Message.DownloadFinished -> downloadHandler.getDownloadFinishedHandler()
        is Message.UploadRequest -> NothingMessageHandler
        is Message.UploadResponse -> NothingMessageHandler
        is Message.Upload -> NothingMessageHandler
        is Message.UploadFinished -> NothingMessageHandler
        is Message.Ping -> PingMessageHandler
    } as IMessageHandler<M>
}
