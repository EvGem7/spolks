package me.evgem.client.command.handler.impl

import me.evgem.client.model.Command
import me.evgem.domain.model.Message

object TimeCommandHandler : MessageCommandHandler<Command.Time>() {
    override fun getMessage(command: Command.Time): Message = Message.Time(null)
}
