package me.evgem.client.command.handler.impl

import me.evgem.client.command.handler.ICommandHandler
import me.evgem.client.model.ClientState
import me.evgem.client.model.Command
import me.evgem.domain.model.Message
import me.evgem.domain.utils.Log

abstract class MessageCommandHandler<C : Command> : ICommandHandler<C> {

    abstract fun getMessage(command: C): Message

    override suspend fun handle(command: C, clientState: ClientState): ClientState {
        if (clientState.connection != null) {
            clientState.connection.send(getMessage(command))
        } else {
            Log.i("no connection to perform command ${command::class.java.simpleName}")
        }
        return clientState
    }
}