package me.evgem.server.connection.listener.serverSocket

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.suspendCancellableCoroutine
import me.evgem.domain.di.getMessageDecoder
import me.evgem.domain.di.getMessageEncoder
import me.evgem.domain.connection.IConnection
import me.evgem.domain.connection.socket.SocketConnection
import me.evgem.domain.utils.Log
import me.evgem.domain.utils.safeResume
import me.evgem.server.connection.listener.IConnectionListener
import java.net.ServerSocket
import java.net.Socket

@ExperimentalCoroutinesApi
class ServerSocketConnectionListener : IConnectionListener {

    override fun connections(): Flow<IConnection> = flow<Socket> {
        val serverSocket = getServerSocket()
        while (true) {
            val socket = serverSocket.suspendAccept()
            emit(socket)
        }
    }.map {
        SocketConnection(
            it,
            getMessageDecoder(),
            getMessageEncoder(),
        )
    }.flowOn(Dispatchers.IO)

    private suspend fun getServerSocket(): ServerSocket = suspendCancellableCoroutine { cont ->
        val serverSocket = ServerSocket()
        serverSocket.bind(null)
        Log.i("bind server socket ${serverSocket.localSocketAddress}")

        cont.safeResume(serverSocket)
    }

    private suspend fun ServerSocket.suspendAccept(): Socket = suspendCancellableCoroutine { cont ->
        val socket = accept()
        Log.i("socket accepted ${socket.inetAddress}")
        cont.safeResume(socket)
    }
}
