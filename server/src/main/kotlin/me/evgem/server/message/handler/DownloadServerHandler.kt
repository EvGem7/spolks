package me.evgem.server.message.handler

import kotlinx.coroutines.suspendCancellableCoroutine
import me.evgem.domain.connection.IConnection
import me.evgem.domain.model.IMessageHandler
import me.evgem.domain.model.Message
import me.evgem.domain.utils.Log
import me.evgem.domain.utils.safeResume
import java.io.File
import java.io.InputStream
import kotlin.math.min

class DownloadServerHandler : IMessageHandler<Message.DownloadRequest> {

    companion object {
                private const val BUFFER_SIZE = 1024 * 1024
//        private const val BUFFER_SIZE = 2
    }

    private val dir = File("serverFiles/").apply {
        mkdirs()
    }

    private var downloadIdCounter = System.currentTimeMillis()

    private val inputStreams = HashMap<Long, InputStream>()

    override suspend fun handle(message: Message.DownloadRequest, connection: IConnection) {
        Log.i("download request for ${message.filename}")
        val file = findFile(message.filename)
        if (file != null) {
            val id = getDownloadId()
            connection.send(
                Message.DownloadResponse(
                    filename = message.filename,
                    length = file.length(),
                    downloadId = id
                )
            )
            sendFile(id, file, connection)
            connection.send(Message.DownloadFinished(id))
        } else {
            connection.send(
                Message.DownloadResponse(
                    filename = message.filename,
                    length = 0L,
                    downloadId = null
                )
            )
        }
    }

    private suspend fun findFile(filename: String): File? = suspendCancellableCoroutine { cont ->
        val file = dir.listFiles()?.find {
            it.name == filename
        }
        cont.safeResume(file)
    }

    private suspend fun sendFile(
        id: Long,
        file: File,
        connection: IConnection,
    ) {
        val partsCount = file.length().let { length ->
            length / BUFFER_SIZE + if (length % BUFFER_SIZE != 0L) 1 else 0
        }
        for (i in 0L until partsCount) {
            val part = file.getPart(id)
            connection.send(Message.Download(id, part))
        }

        suspendCancellableCoroutine<Unit> {
            inputStreams.remove(id)?.close()
            it.safeResume(Unit)
        }
    }

    private suspend fun File.getPart(
        id: Long,
    ): ByteArray = suspendCancellableCoroutine { cont ->
        val stream = inputStreams.getOrPut(id) {
            inputStream()
        }
        val buffer = ByteArray(min(BUFFER_SIZE, stream.available()))
        stream.read(buffer)
        cont.safeResume(buffer)
    }

    private fun getDownloadId(): Long = downloadIdCounter++
}
