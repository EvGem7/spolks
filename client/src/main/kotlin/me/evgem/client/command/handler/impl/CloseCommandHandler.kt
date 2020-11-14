package me.evgem.client.command.handler.impl

import me.evgem.client.model.Command
import me.evgem.domain.model.Message

object CloseCommandHandler : MessageCommandHandler<Command.Close>() {

    override fun getMessage(command: Command.Close): Message = Message.Close
}
