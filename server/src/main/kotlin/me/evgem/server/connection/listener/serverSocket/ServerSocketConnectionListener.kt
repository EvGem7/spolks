package me.evgem.server.connection.listener.serverSocket

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.suspendCancellableCoroutine
import me.evgem.domain.connection.IConnection
import me.evgem.domain.connection.socket.SocketConnection
import me.evgem.domain.di.getMessageDecoder
import me.evgem.domain.di.getMessageEncoder
import me.evgem.domain.utils.Log
import me.evgem.domain.utils.safeResume
import me.evgem.domain.utils.withTimeout
import me.evgem.server.connection.listener.IConnectionListener
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketTimeoutException

@ExperimentalCoroutinesApi
class ServerSocketConnectionListener : IConnectionListener {

    companion object {
        private const val SUSPEND_SOCKET_TIMEOUT = 10
        private const val PORT = 9999
    }

    override fun connections(): Flow<IConnection> = flow<Socket> {
        val serverSocket = getServerSocket()
        while (true) {
            delay(1L)
            val socket = serverSocket.suspendAccept() ?: continue
            emit(socket)
        }
    }.map {
        SocketConnection(
            it,
            getMessageDecoder(),
            getMessageEncoder(),
        )
    }

    private suspend fun getServerSocket(): ServerSocket = suspendCancellableCoroutine { cont ->
        val serverSocket = ServerSocket(PORT)
        Log.i("bind server socket ${serverSocket.localSocketAddress}")
        cont.safeResume(serverSocket)
    }

    private suspend fun ServerSocket.suspendAccept(): Socket? = suspendCancellableCoroutine { cont ->
        val socket = try {
            withTimeout(SUSPEND_SOCKET_TIMEOUT) {
                accept()
            }
        } catch (e: SocketTimeoutException) {
            null
        }
        if (socket != null) {
            Log.i("socket accepted ${socket.inetAddress}")
        }
        cont.safeResume(socket)
    }
}
