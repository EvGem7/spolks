package me.evgem.client.command.handler.impl

import me.evgem.client.command.handler.ICommandHandler
import me.evgem.client.model.ClientState
import me.evgem.client.model.Command

object ConnectCommandHandler : ICommandHandler<Command.Connect> {

    override suspend fun handle(command: Command.Connect, clientState: ClientState): ClientState {
        val connection = clientState.connector.connect(command.host, command.port)
        return clientState.copy(connection = connection)
    }
}
