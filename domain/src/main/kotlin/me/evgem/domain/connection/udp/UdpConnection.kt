package me.evgem.domain.connection.udp

import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeout
import me.evgem.domain.connection.IConnection
import me.evgem.domain.message.IMessageDecoder
import me.evgem.domain.message.IMessageEncoder
import me.evgem.domain.model.Message
import me.evgem.domain.utils.DatagramSocketWrapper
import me.evgem.domain.utils.Log
import me.evgem.domain.utils.doSuspend
import me.evgem.domain.utils.withTimeout
import java.io.IOException
import java.lang.Exception
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.SocketAddress
import java.net.SocketTimeoutException
import java.nio.ByteBuffer
import kotlin.math.min

class UdpConnection(
    private val wrapper: DatagramSocketWrapper,
    private val messageEncoder: IMessageEncoder,
    private val messageDecoder: IMessageDecoder,
) : IConnection {

    companion object {
        private const val BUFFER_SIZE = 200_000
        private const val SEND_THRESHOLD = BUFFER_SIZE * 9 / 10
        private const val SEND_TIMEOUT = 2000L

        private const val DATAGRAM_SIZE = 60_000
        private const val DATA_FIELD_LENGTH = DATAGRAM_SIZE - Int.SIZE_BYTES - Short.SIZE_BYTES
    }

    private var sendCounter = 0L

    private var sendIndex: Int = 0
    private var receiveIndex: Int = 0
    private var firstReceived = false

    private val receiveWaitChannel = BroadcastChannel<Unit>(1)

    init {
        wrapper.socket.receiveBufferSize = BUFFER_SIZE
        wrapper.socket.sendBufferSize = BUFFER_SIZE
    }

    override fun messages(): Flow<Message> = flow {
        var bytes: ByteArray? = byteArrayOf()
        while (bytes != null) {
            var shouldSendReady = false
            do {
                bytes = try {
                    tryReadBytes()
                } catch (e: PacketDropException) {
                    send(Message.Noop)
                    break
                }
                if (bytes == null || bytes.isEmpty()) {
                    break
                }

                shouldSendReady = true

                messageDecoder.decode(bytes)?.let {
                    Log.d("receive ${it::class.java.simpleName}")
                    if (it is Message.ReadyToReceive) {
                        shouldSendReady = false
                        sendCounter = 0
                        receiveWaitChannel.offer(Unit)
                    }
                    emit(it)
                }
            } while (bytes != null && bytes.isNotEmpty())
            if (shouldSendReady) {
                send(Message.ReadyToReceive)
            }
            delay(1L)
        }
        socket.doSuspend {
            close()
        }
        Log.i("socket $address is closed")
    }

    override suspend fun send(message: Message) {
        Log.d("send msg ${message::class.java.simpleName}")
        withTimeout(SEND_TIMEOUT) {
            val encoded = messageEncoder.encode(message)
            val datagrams = ArrayList<DatagramPacket>()
            for (i in encoded.indices step DATA_FIELD_LENGTH) {
                val dataSize = min(DATA_FIELD_LENGTH, encoded.size - i)
                val buffer = ByteBuffer.allocate(DATAGRAM_SIZE)
                buffer.putShort(dataSize.toShort())
                buffer.putInt(sendIndex++)
                buffer.put(encoded, i, dataSize)
                datagrams += DatagramPacket(buffer.array(), DATAGRAM_SIZE, address)
            }
            datagrams.forEach { packet ->
                if (sendCounter > SEND_THRESHOLD) {
                    receiveWaitChannel.asFlow().first()
                }
                socket.doSuspend {
                    send(packet)
                }
                if (message !is Message.ReadyToReceive) {
                    sendCounter += DATAGRAM_SIZE
                }
            }
        }
    }

    override suspend fun close() {
        socket.doSuspend {
            close()
        }
    }

    private suspend fun tryReadBytes(): ByteArray? {
        return socket.doSuspend {
            try {
                withTimeout(1) {
                    val datagram = DatagramPacket(ByteArray(DATAGRAM_SIZE), DATAGRAM_SIZE)
                    receive(datagram)
                    val buffer = ByteBuffer.wrap(datagram.data)
                    val dataSize = buffer.short
                    val index = buffer.int
                    Log.d("tryRead index = $index")
                    receiveIndex = if (!firstReceived) {
                        firstReceived = true
                        index
                    } else {
                        if (receiveIndex + 1 != index) {
                            Log.e("some packets dropped! prev=$receiveIndex cur=$index")
                            reset()
                            throw PacketDropException()
                        }
                        index
                    }
                    val result = ByteArray(dataSize.toInt() and 0xFFFF)
                    buffer.get(result)
                    result
                }
            } catch (e: SocketTimeoutException) {
                byteArrayOf()
            } catch (e: IOException) {
                Log.e(e)
                null
            }
        }
    }

    private fun reset() {
        messageDecoder.clear()
        sendIndex = 0
        receiveIndex = 0
        firstReceived = false
        sendCounter = 0
    }

    private val socket: DatagramSocket get() = wrapper.socket
    private val address: SocketAddress get() = wrapper.address
}
