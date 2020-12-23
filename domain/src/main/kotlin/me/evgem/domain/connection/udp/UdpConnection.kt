package me.evgem.domain.connection.udp

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import me.evgem.domain.connection.IConnection
import me.evgem.domain.message.IMessageDecoder
import me.evgem.domain.message.IMessageEncoder
import me.evgem.domain.model.Message
import me.evgem.domain.utils.DatagramSocketWrapper
import me.evgem.domain.utils.Log
import me.evgem.domain.utils.doSuspend
import me.evgem.domain.utils.withTimeout
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.SocketAddress
import java.net.SocketTimeoutException
import kotlin.math.min

class UdpConnection(
    private val wrapper: DatagramSocketWrapper,
    private val messageEncoder: IMessageEncoder,
    private val messageDecoder: IMessageDecoder,
) : IConnection {

    override fun messages(): Flow<Message> = flow {
        var bytes: ByteArray? = byteArrayOf()
        while (bytes != null) {
            do {
                bytes = tryReadBytes()
                if (bytes == null) {
                    continue
                }
                messageDecoder.decode(bytes)?.let {
                    Log.d("receive ${it::class.java.simpleName}")
                    emit(it)
                }
            } while (bytes != null && bytes.isNotEmpty())
            delay(1L)
        }
        socket.doSuspend {
            close()
        }
        Log.i("socket ${socket.inetAddress} is closed")
    }

    private suspend fun tryReadBytes(): ByteArray? {
        return socket.doSuspend {
            try {
                val size = withTimeout(1) {
                    val sizeDatagram = DatagramPacket(ByteArray(2), 2)
                    receive(sizeDatagram)
                    sizeDatagram.data.let {
                        ((it[0].toInt() and 0xFF) shl 8) +
                                (it[1].toInt() and 0xFF)
                    }
                }
                val contentDatagram = DatagramPacket(ByteArray(size), size)
                receive(contentDatagram)
                contentDatagram.data
            } catch (e: SocketTimeoutException) {
                byteArrayOf()
            } catch (e: IOException) {
                Log.d(e.stackTraceToString())
                null
            }
        }
    }

    override suspend fun send(message: Message) {
        Log.d("send ${message::class.java.simpleName}")
        val encoded = messageEncoder.encode(message)
        val size = kotlin.run {
            val s = encoded.size
            val h = (s shr 8).toByte()
            val l = s.toByte()
            byteArrayOf(h, l)
        }
        val sizeDatagram = DatagramPacket(size, size.size, address)
        val contentDatagram = DatagramPacket(encoded, encoded.size, address)
        socket.doSuspend {
            send(sizeDatagram)
            send(contentDatagram)
        }
    }

    override suspend fun close() {
        socket.doSuspend {
            close()
        }
    }

    private val socket: DatagramSocket get() = wrapper.socket
    private val address: SocketAddress get() = wrapper.address
}
