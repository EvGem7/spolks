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

    companion object {
        private const val DATAGRAM_SIZE = 1024
        private const val DATA_SIZE_FIELD_LENGTH = 2
        private const val DATA_SIZE_FIELD_INDEX = 1
        private const val DATA_FIELD_OFFSET =  DATA_SIZE_FIELD_LENGTH + DATA_SIZE_FIELD_INDEX
        private const val DATA_FIELD_LENGTH = DATAGRAM_SIZE - DATA_FIELD_OFFSET
    }

    private val receiveBuffer = ByteArray(DATAGRAM_SIZE)

    private var sendIndex: Byte = 0

    override fun messages(): Flow<Message> = flow {
        var bytes = tryReadBytes()
        while (bytes != null) {
            delay(1L)
            messageDecoder.decode(bytes)?.let {
                emit(it)
            }
            bytes = tryReadBytes()
        }
        socket.doSuspend {
            close()
        }
        Log.i("socket ${socket.inetAddress} is closed")
    }

    private suspend fun tryReadBytes(): ByteArray? {
        val packet = DatagramPacket(receiveBuffer, DATAGRAM_SIZE)
        return socket.doSuspend {
            try {
                withTimeout(1) {
                    receive(packet)
                    val data = packet.data
                    val size = getDataSize(data)
                    val index = getIndex(data)
                    getData(data, size)
                }
            } catch (e: SocketTimeoutException) {
                byteArrayOf()
            } catch (e: IOException) {
                Log.d(e.stackTraceToString())
                null
            }
        }
    }

    override suspend fun send(message: Message) {
        val encoded = messageEncoder.encode(message)
        val datagrams = ArrayList<DatagramPacket>()
        for (i in encoded.indices step DATA_FIELD_LENGTH) {
            val dataSize = min(DATA_FIELD_LENGTH, encoded.size - i)
            val buffer = ByteArray(DATAGRAM_SIZE)
            buffer[0] = (dataSize shr 8).toByte()
            buffer[1] = dataSize.toByte()
            buffer[2] = sendIndex++
            encoded.copyInto(
                destination = buffer,
                destinationOffset = DATA_FIELD_OFFSET,
                startIndex = i,
                endIndex = i + dataSize,
            )
            datagrams += DatagramPacket(buffer, DATAGRAM_SIZE, address)
        }
        socket.doSuspend {
            datagrams.forEach { packet ->
                send(packet)
            }
        }
    }

    override suspend fun close() {
        socket.doSuspend {
            close()
        }
    }

    private fun getIndex(data: ByteArray): Byte {
        return data[2]
    }

    private fun getDataSize(datagramData: ByteArray): Int {
        var result = 0
        result += (datagramData[0].toInt() and 0xFF) shl 8
        result += datagramData[1].toInt() and 0xFF
        return result
    }

    private fun getData(datagramData: ByteArray, dataSize: Int): ByteArray {
        val from = DATA_FIELD_OFFSET
        val to = min(from + dataSize, datagramData.size)
        return datagramData.copyOfRange(from, to)
    }

    private val socket: DatagramSocket get() = wrapper.socket
    private val address: SocketAddress get() = wrapper.address
}
