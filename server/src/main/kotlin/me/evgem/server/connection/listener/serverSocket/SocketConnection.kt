package me.evgem.server.connection.listener.serverSocket

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import me.evgem.domain.message.IMessageDecoder
import me.evgem.domain.message.IMessageEncoder
import me.evgem.domain.model.IConnection
import me.evgem.domain.model.Message
import me.evgem.domain.utils.Log
import me.evgem.domain.utils.doSuspend
import me.evgem.domain.utils.withTimeout
import java.net.Socket
import java.net.SocketTimeoutException

class SocketConnection(
    private val socket: Socket,
    private val messageDecoder: IMessageDecoder,
    private val messageEncoder: IMessageEncoder,
) : IConnection {

    companion object {
        private const val SUSPEND_READ_TIMEOUT = 1000
    }

    override fun messages(): Flow<Message> = flow<Message> {
        var bytes = tryReadBytes()
        while (bytes != null) {
            messageDecoder.decode(bytes)?.let {
                emit(it)
            }
            bytes = tryReadBytes()
        }
        socket.doSuspend {
            close()
        }
        Log.i("socket ${socket.inetAddress} is closed")
    }.flowOn(Dispatchers.IO)

    override suspend fun send(message: Message) {
        withContext(Dispatchers.IO) {
            socket.doSuspend {
                val arr = messageEncoder.encode(message).toByteArray()
                socket.getOutputStream().write(arr)
            }
        }
    }

    private suspend fun tryReadBytes(): List<Byte>? = socket.doSuspend {
        val input = getInputStream()
        if (input.available() > 0) {
            input.readNBytes(input.available()).toList()
        } else {
            try {
                val byte = withTimeout(SUSPEND_READ_TIMEOUT) {
                    input.read()
                }
                if (byte != -1) {
                    if (input.available() > 0) {
                        listOf(byte.toByte()) + input.readNBytes(input.available()).toList()
                    } else {
                        listOf(byte.toByte())
                    }
                } else {
                    null
                }
            } catch (e: SocketTimeoutException) {
                emptyList()
            }
        }
    }
}
