package me.evgem.client

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import me.evgem.client.command.handler.provider.ICommandHandlerProvider
import me.evgem.client.connection.IConnector
import me.evgem.client.model.ClientState
import me.evgem.client.model.Command
import me.evgem.domain.connection.IConnection
import me.evgem.domain.model.IMessageHandlerProvider
import me.evgem.domain.utils.singleThreadDispatcher

class Client(
    private val commandHandlerProvider: ICommandHandlerProvider,
    connector: IConnector,
    private val messageHandlerProvider: IMessageHandlerProvider
) {

    private lateinit var coroutineScope: CoroutineScope

    private var clientState: ClientState = ClientState(connector, null)

    fun performCommand(command: Command) {
        coroutineScope.launch {
            clientState = commandHandlerProvider.provideCommandHandler(command).handle(command, clientState)
            clientState.connection?.let {
                if (command is Command.Connect) {
                    collectConnection(it)
                }
            }
        }
    }

    fun start() {
        initScope()
    }

    fun stop() {
        coroutineScope.cancel()
    }

    private fun collectConnection(connection: IConnection) {
        coroutineScope.launch {
            connection.messages().collect {
                messageHandlerProvider.provide(it).handle(it, connection)
            }
        }
    }

    private fun initScope() {
        coroutineScope = CoroutineScope(singleThreadDispatcher) + CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
            throw throwable
        }
    }
}
