package me.evgem.client.connection.socket

import me.evgem.client.connection.IConnector
import me.evgem.domain.connection.IConnection
import me.evgem.domain.connection.tcp.TcpConnection
import me.evgem.domain.di.getMessageDecoder
import me.evgem.domain.di.getMessageEncoder
import me.evgem.domain.model.Message
import me.evgem.domain.utils.Log
import me.evgem.domain.utils.doSuspend
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket

class TcpConnector : IConnector {

    override suspend fun connect(host: String, port: Int): IConnection? {
        return try {
            Log.i("connecting $host:$port")
            val socket = Socket().doSuspend {
                connect(InetSocketAddress(host, port))
                this
            }
            Log.i("connected $host:$port")
            TcpConnection(
                socket = socket,
                messageDecoder = getMessageDecoder(),
                messageEncoder = getMessageEncoder(),
            ).also {
                it.send(Message.Ping)
            }
        } catch (e: IOException) {
            Log.i("cannot connect to $host:$port")
            null
        }
    }
}
