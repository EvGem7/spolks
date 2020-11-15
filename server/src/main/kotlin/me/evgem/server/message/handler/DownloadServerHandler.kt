package me.evgem.server.message.handler

import kotlinx.coroutines.suspendCancellableCoroutine
import me.evgem.domain.model.Message
import me.evgem.domain.utils.Log
import me.evgem.domain.utils.messageHandler
import me.evgem.domain.utils.safeResume
import java.io.File
import java.io.RandomAccessFile
import kotlin.math.min

class DownloadServerHandler {

    companion object {
        private const val BUFFER_SIZE = 65536
    }

    private data class FileInfo(
        val file: File,
        val randomAccess: RandomAccessFile,
    )

    private val dir = File("serverFiles/").apply {
        mkdirs()
    }

    private var downloadIdCounter = System.currentTimeMillis()

    private val downloadingFiles = HashMap<Long, FileInfo>()

    fun getDownloadStartRequestHandler() = messageHandler<Message.DownloadStartRequest> { message, connection ->
        Log.i("download request for ${message.filename}")
        val info = findFileInfo(message.filename)
        if (info != null) {
            val id = getDownloadId()
            downloadingFiles[id] = info
            connection.send(
                Message.DownloadStartResponse(
                    filename = message.filename,
                    length = info.file.length(),
                    downloadId = id,
                )
            )
        } else {
            Log.i("cannot find file ${message.filename}")
            connection.send(
                Message.DownloadStartResponse(
                    filename = message.filename,
                    length = 0L,
                    downloadId = null,
                )
            )
        }
    }

    fun getDownloadRequestHandler() = messageHandler<Message.DownloadRequest> { message, connection ->
        val info = downloadingFiles[message.downloadId]
        if (info != null) {
            val part = info.getPart(message.downloadedLength)
            if (part.isNotEmpty()) {
                connection.send(Message.Download(message.downloadId, part))
            } else {
                connection.send(Message.DownloadFinished(message.downloadId))
                downloadingFiles.remove(message.downloadId)
                Log.i("finish download ${info.file.name}")
            }

        } else {
            Log.i("cannot find downloadId ${message.downloadId}")
            connection.send(
                Message.Download(
                    downloadId = message.downloadId,
                    data = null,
                )
            )
        }
    }

    private suspend fun findFileInfo(filename: String, canWrite: Boolean = false): FileInfo? =
        suspendCancellableCoroutine { cont ->
            val file = dir.listFiles()?.find {
                it.name == filename
            }
            val info = file?.let {
                FileInfo(it, RandomAccessFile(file, if (canWrite) "rw" else "r"))
            }
            cont.safeResume(info)
        }

    private suspend fun FileInfo.getPart(
        offset: Long,
    ): ByteArray = suspendCancellableCoroutine { cont ->
        randomAccess.seek(offset)
        val buffer = ByteArray(min(BUFFER_SIZE.toLong(), file.length() - offset).toInt())
        randomAccess.read(buffer)
        cont.safeResume(buffer)
    }

    private fun getDownloadId(): Long = downloadIdCounter++
}
