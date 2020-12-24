package me.evgem.client.connection.impl

import me.evgem.client.connection.IConnector
import me.evgem.domain.connection.IConnection
import me.evgem.domain.connection.udp.UdpConnection
import me.evgem.domain.di.getMessageDecoder
import me.evgem.domain.di.getMessageEncoder
import me.evgem.domain.model.Message
import me.evgem.domain.utils.DatagramSocketWrapper
import me.evgem.domain.utils.Log
import me.evgem.domain.utils.doSuspend
import me.evgem.domain.utils.withTimeout
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetSocketAddress

class UdpConnector : IConnector {

    companion object {
        private const val CONNECT_TIMEOUT = 5000
    }

    override suspend fun connect(host: String, port: Int): IConnection? {
        return try {
            Log.i("connecting $host:$port")
            val wrapper: DatagramSocketWrapper = doSuspend {
                val address = InetSocketAddress(host, port)
                val socket = DatagramSocket()
                val receivePacket = DatagramPacket(byteArrayOf(0), 1)
                socket.withTimeout(CONNECT_TIMEOUT) {
                    val connectPacket = DatagramPacket(byteArrayOf(0), 1, address)
                    socket.send(connectPacket)
                    socket.receive(receivePacket)
                }
                DatagramSocketWrapper(socket, receivePacket.socketAddress)
            }
            Log.i("connected $host:$port")
            UdpConnection(
                wrapper = wrapper,
                messageDecoder = getMessageDecoder(),
                messageEncoder = getMessageEncoder(),
            ).also {
                it.send(Message.Ping)
            }
        } catch (e: IOException) {
            Log.d(e.stackTraceToString())
            Log.i("cannot connect to $host:$port")
            null
        }
    }
}
