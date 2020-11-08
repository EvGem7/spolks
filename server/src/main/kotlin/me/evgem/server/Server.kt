package me.evgem.server

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import me.evgem.domain.model.IConnection
import me.evgem.domain.model.IMessageHandlerProvider
import me.evgem.server.connection.listener.IConnectionListener

class Server (
    private val connectionListener: IConnectionListener,
    private val messageHandlerProvider: IMessageHandlerProvider
) {

    private lateinit var coroutineScope: CoroutineScope

    fun start() {
        initScope()
        coroutineScope.launch {
            connectionListener.connections().collect {
                collectConnection(it)
            }
        }
    }

    private fun collectConnection(connection: IConnection) {
        coroutineScope.launch {
            connection.messages().collect {
                messageHandlerProvider.provide(it).handle(it, connection)
            }
        }
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