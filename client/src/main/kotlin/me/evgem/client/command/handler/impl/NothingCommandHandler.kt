package me.evgem.client.command.handler.impl

import me.evgem.client.command.handler.ICommandHandler
import me.evgem.client.model.ClientState
import me.evgem.client.model.Command

object NothingCommandHandler : ICommandHandler<Command> {
    override suspend fun handle(command: Command, clientState: ClientState): ClientState {
        return clientState
    }
}
