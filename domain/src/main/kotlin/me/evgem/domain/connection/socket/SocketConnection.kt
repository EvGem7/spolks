package me.evgem.domain.connection.socket

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import me.evgem.domain.message.IMessageDecoder
import me.evgem.domain.message.IMessageEncoder
import me.evgem.domain.connection.IConnection
import me.evgem.domain.model.Message
import me.evgem.domain.utils.Log
import me.evgem.domain.utils.doSuspend
import me.evgem.domain.utils.singleThreadDispatcher
import me.evgem.domain.utils.withTimeout
import java.io.IOException
import java.net.Socket
import java.net.SocketTimeoutException

class SocketConnection(
    private val socket: Socket,
    private val messageDecoder: IMessageDecoder,
    private val messageEncoder: IMessageEncoder,
) : IConnection {

    companion object {
        private const val CONNECTION_LOST_TIMEOUT = 30_000L
    }

    private val scope = CoroutineScope(singleThreadDispatcher)

    private var checkPingJob: Job? = null

    override fun messages(): Flow<Message> = flow<Message> {
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

    override suspend fun send(message: Message) {
        socket.doSuspend {
            val arr = messageEncoder.encode(message)
            socket.getOutputStream().write(arr)
        }
    }

    override suspend fun close() {
        socket.doSuspend {
            close()
        }
    }

    private suspend fun tryReadBytes(): ByteArray? = socket.doSuspend {
        try {
            val input = getInputStream()
            if (input.available() > 0) {
                input.readNBytes(input.available())
            } else {
                try {
                    val byte = withTimeout(1) {
                        input.read()
                    }
                    if (byte != -1) {
                        if (input.available() > 0) {
                            byteArrayOf(byte.toByte()) + input.readNBytes(input.available())
                        } else {
                            byteArrayOf(byte.toByte())
                        }
                    } else {
                        null
                    }
                } catch (e: SocketTimeoutException) {
                    byteArrayOf()
                }
            }
        } catch (e: IOException) {
            null
        }
    }.also {
        if (it == null || it.isEmpty()) {
            startCheckPingTimer()
        } else {
            stopStopTimer()
        }
    }

    private fun startCheckPingTimer() {
        if (checkPingJob != null) {
            return
        }
        checkPingJob = scope.launch {
            delay(CONNECTION_LOST_TIMEOUT)
            socket.doSuspend {
                close()
            }
        }
    }

    private fun stopStopTimer() {
        checkPingJob?.cancel()
        checkPingJob = null
    }
}
