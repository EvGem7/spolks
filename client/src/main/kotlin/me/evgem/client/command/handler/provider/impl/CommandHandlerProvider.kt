package me.evgem.client.command.handler.provider.impl

import me.evgem.client.command.handler.ICommandHandler
import me.evgem.client.command.handler.impl.ConnectCommandHandler
import me.evgem.client.command.handler.impl.EchoCommandHandler
import me.evgem.client.command.handler.impl.NothingCommandHandler
import me.evgem.client.command.handler.provider.ICommandHandlerProvider
import me.evgem.client.model.Command

@Suppress("UNCHECKED_CAST")
class CommandHandlerProvider : ICommandHandlerProvider {

    override fun <C : Command> provideCommandHandler(command: C): ICommandHandler<C> = when (command as Command) {
        is Command.Echo -> EchoCommandHandler
        is Command.Time -> NothingCommandHandler
        is Command.Close -> NothingCommandHandler
        is Command.Download -> NothingCommandHandler
        is Command.Upload -> NothingCommandHandler
        is Command.Stop -> NothingCommandHandler
        is Command.Connect -> ConnectCommandHandler
    } as ICommandHandler<C>
}
