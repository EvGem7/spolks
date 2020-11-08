package me.evgem.client.command.handler.impl

import me.evgem.client.command.handler.ICommandHandler
import me.evgem.client.model.ClientState
import me.evgem.client.model.Command
import me.evgem.domain.model.Message
import me.evgem.domain.utils.Log

object EchoCommandHandler : ICommandHandler<Command.Echo> {

    override suspend fun handle(command: Command.Echo, clientState: ClientState): ClientState {
        if (clientState.connection != null) {
            val message = Message.Echo(command.msg)
            clientState.connection.send(message)
        } else {
            Log.i("try echo when no connection")
        }
        return clientState
    }
}
