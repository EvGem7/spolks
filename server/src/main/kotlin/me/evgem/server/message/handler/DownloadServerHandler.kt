package me.evgem.server.message.handler

import me.evgem.domain.model.Message
import me.evgem.domain.utils.FileInfo
import me.evgem.domain.utils.Log
import me.evgem.domain.utils.findFileInfo
import me.evgem.domain.utils.messageHandler
import java.io.File

class DownloadServerHandler(private val filesDir: File) {

    companion object {
        private const val BUFFER_SIZE = 59_000L
    }

    private var downloadIdCounter = System.currentTimeMillis()

    private val downloadingFiles = HashMap<Long, FileInfo>()

    fun getDownloadStartRequestHandler() = messageHandler<Message.DownloadStartRequest> { message, connection ->
        Log.i("download request for ${message.filename}")
        val info = filesDir.findFileInfo(message.filename)
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

    fun getDownloadWaitHandler() = messageHandler<Message.DownloadWait> { message, connection ->
        val info = downloadingFiles[message.downloadId]
        if (info != null) {
            for (i in message.downloadedLength until info.file.length() step BUFFER_SIZE) {
                val part = info.getPart(
                    offset = i,
                    bufferSize = BUFFER_SIZE,
                )
                if (part.isNotEmpty()) {
                    try {
                        connection.send(Message.Download(message.downloadId, part))
                    } catch (e: Exception) {
                        return@messageHandler
                    }
                } else {
                    break
                }
            }
            connection.send(Message.DownloadFinished(message.downloadId))
            downloadingFiles.remove(message.downloadId)
            Log.i("finish download ${info.file.name}")
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

    private fun getDownloadId(): Long = downloadIdCounter++
}
