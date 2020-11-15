package me.evgem.client.command.handler.impl

import me.evgem.client.message.handler.DownloadClientHandler
import me.evgem.client.model.Command
import me.evgem.domain.model.Message

class ReDownloadCommandHandler(
    private val downloadClientHandler: DownloadClientHandler
) : MessageCommandHandler<Command.ReDownload>() {

    override fun getMessage(command: Command.ReDownload): Message = Message.DownloadWait(
        downloadId = command.downloadId,
        downloadedLength = downloadClientHandler.getDownloadedLength(command.downloadId),
    )
}
