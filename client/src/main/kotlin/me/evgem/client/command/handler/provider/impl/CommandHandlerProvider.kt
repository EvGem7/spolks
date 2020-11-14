package me.evgem.client.command.handler.provider.impl

import me.evgem.client.command.handler.ICommandHandler
import me.evgem.client.command.handler.impl.*
import me.evgem.client.command.handler.provider.ICommandHandlerProvider
import me.evgem.client.di.getDownloadMessageHandler
import me.evgem.client.model.Command

@Suppress("UNCHECKED_CAST")
class CommandHandlerProvider : ICommandHandlerProvider {

    override fun <C : Command> provideCommandHandler(command: C): ICommandHandler<C> = when (command as Command) {
        is Command.Echo -> EchoCommandHandler
        is Command.Time -> TimeCommandHandler
        is Command.Close -> CloseCommandHandler
        is Command.Download -> DownloadCommandHandler()
        is Command.Upload -> NothingCommandHandler
        is Command.Stop -> NothingCommandHandler
        is Command.Connect -> ConnectCommandHandler
    } as ICommandHandler<C>
}
