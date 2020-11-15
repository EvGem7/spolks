package me.evgem.client.command.handler.impl

import me.evgem.client.model.Command
import me.evgem.domain.model.Message

object DownloadCommandHandler : MessageCommandHandler<Command.Download>() {
    override fun getMessage(command: Command.Download): Message = Message.DownloadStartRequest(command.filename)
}
