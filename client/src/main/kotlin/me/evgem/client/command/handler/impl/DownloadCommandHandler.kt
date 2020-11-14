package me.evgem.client.command.handler.impl

import me.evgem.client.message.handler.DownloadClientHandler
import me.evgem.client.model.ClientState
import me.evgem.client.model.Command
import me.evgem.domain.model.Message
import me.evgem.domain.utils.Log

class DownloadCommandHandler(
    private val downloadClientHandler: DownloadClientHandler
) : MessageCommandHandler<Command.Download>() {

    override fun getMessage(command: Command.Download): Message = Message.DownloadRequest(command.filename)

    override suspend fun handle(command: Command.Download, clientState: ClientState): ClientState {
        return if (downloadClientHandler.isAbleDownload(command.filename)) {
            super.handle(command, clientState)
        } else {
            Log.i("cannot download file ${command.filename}")
            clientState
        }
    }
}
