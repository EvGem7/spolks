package me.evgem.client.command.handler

import me.evgem.client.model.ClientState
import me.evgem.client.model.Command

interface ICommandHandler <C : Command> {
    suspend fun handle(command: C, clientState: ClientState): ClientState
}
