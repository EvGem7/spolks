package me.evgem.client

import kotlinx.coroutines.*
import me.evgem.client.command.handler.provider.ICommandHandlerProvider
import me.evgem.client.connection.IConnector
import me.evgem.client.model.ClientState
import me.evgem.client.model.Command

class Client(
    private val commandHandlerProvider: ICommandHandlerProvider,
    connector: IConnector,
) {

    private lateinit var coroutineScope: CoroutineScope

    private var clientState: ClientState = ClientState(connector, null)

    fun performCommand(command: Command) {
        coroutineScope.launch {
            clientState = commandHandlerProvider.provideCommandHandler(command).handle(command, clientState)
        }
    }

    fun start() {
        initScope()
    }

    fun stop() {
        coroutineScope.cancel()
    }

    private fun initScope() {
        coroutineScope = CoroutineScope(Dispatchers.IO) + CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
            throw throwable
        }
    }
}
