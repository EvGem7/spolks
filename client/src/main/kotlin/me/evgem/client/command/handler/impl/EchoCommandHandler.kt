package me.evgem.client.command.handler.impl

import me.evgem.client.model.Command
import me.evgem.domain.model.Message

object EchoCommandHandler : MessageCommandHandler<Command.Echo>() {
    override fun getMessage(command: Command.Echo): Message = Message.Echo(command.msg)
}
