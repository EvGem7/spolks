package me.evgem.server.connection.listener.impl

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.suspendCancellableCoroutine
import me.evgem.domain.connection.IConnection
import me.evgem.domain.connection.udp.UdpConnection
import me.evgem.domain.di.getMessageDecoder
import me.evgem.domain.di.getMessageEncoder
import me.evgem.domain.utils.DatagramSocketWrapper
import me.evgem.domain.utils.Log
import me.evgem.domain.utils.safeResume
import me.evgem.domain.utils.withTimeout
import me.evgem.server.connection.listener.IConnectionListener
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.SocketAddress
import java.net.SocketTimeoutException

class UdpConnectionListener : IConnectionListener {

    companion object {
        private const val PORT = 9999
    }

    override fun connections(): Flow<IConnection> = flow<DatagramSocketWrapper> {
        val serverSocket = getServerSocket()
        while (true) {
            delay(1000L)
            val socket = serverSocket.suspendAccept() ?: continue
            emit(socket)
        }
    }.map { wrapper ->
        UdpConnection(
            wrapper = wrapper,
            messageEncoder = getMessageEncoder(),
            messageDecoder = getMessageDecoder(),
        )
    }

    private suspend fun DatagramSocket.suspendAccept(): DatagramSocketWrapper? = suspendCancellableCoroutine { cont ->
        val connectPacket = DatagramPacket(ByteArray(1), 1)
        val socket: DatagramSocketWrapper? = try {
            withTimeout(1) {
                receive(connectPacket)
                val socket = DatagramSocket()
                socket.send(connectPacket)
                DatagramSocketWrapper(socket, connectPacket.socketAddress)
            }
        } catch (e: SocketTimeoutException) {
            null
        }
        cont.safeResume(socket)
    }


    private suspend fun getServerSocket(): DatagramSocket = suspendCancellableCoroutine { cont ->
        val serverSocket = DatagramSocket(PORT)
        Log.i("bind server socket ${serverSocket.localSocketAddress}")
        cont.safeResume(serverSocket)
    }
}
