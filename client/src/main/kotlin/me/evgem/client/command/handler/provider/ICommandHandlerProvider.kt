package me.evgem.client.command.handler.provider

import me.evgem.client.command.handler.ICommandHandler
import me.evgem.client.model.Command

interface ICommandHandlerProvider {
    fun <C: Command> provideCommandHandler(command: C): ICommandHandler<C>
}
