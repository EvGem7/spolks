package me.evgem.client.message.handler

import kotlinx.coroutines.suspendCancellableCoroutine
import me.evgem.domain.model.Message
import me.evgem.domain.utils.Log
import me.evgem.domain.utils.humanReadableByteCountBin
import me.evgem.domain.utils.messageHandler
import me.evgem.domain.utils.safeResume
import java.io.File
import java.io.FileOutputStream

class DownloadClientHandler {

    private data class DownloadingInfo(
        val file: File,
        val length: Long,
        val startedAt: Long = System.currentTimeMillis()
    )

    private val dir = File("clientDownloads/").apply {
        mkdirs()
    }

    private val downloadingFiles = HashMap<Long, DownloadingInfo>()

    fun getDownloadResponseHandler() = messageHandler<Message.DownloadResponse> { message, _ ->
        Log.i("server download response $message")
        val id = message.downloadId ?: return@messageHandler
        val file = createFile(message.filename)
        downloadingFiles[id] = DownloadingInfo(
            file = file,
            length = message.length,
        )
    }

    fun getDownloadHandler() = messageHandler<Message.Download> { message, _ ->
        val info = downloadingFiles[message.downloadId] ?: kotlin.run {
            Log.e("got download message with unknown id ${message.downloadId}")
            return@messageHandler
        }
        info.file.suspendWriteBytes(message.data)
        Log.i("downloading ${info.file.length() * 100 / info.length}%")
    }

    fun getDownloadFinishedHandler() = messageHandler<Message.DownloadFinished> { message, _ ->
        Log.i("download finished ${
            downloadingFiles[message.downloadId]?.let {
                humanReadableByteCountBin(it.length / (System.currentTimeMillis() - it.startedAt) * 1000)
            }
        } / s")
        downloadingFiles.remove(message.downloadId) ?: kotlin.run {
            Log.e("download finished but there is no downloading with id = ${message.downloadId}")
        }
    }

    private suspend fun createFile(filename: String): File = suspendCancellableCoroutine { cont ->
        val file = File(dir, filename)
        if (file.exists()) {
            file.delete()
        }
        file.createNewFile()
        cont.safeResume(file)
    }

    private suspend fun File.suspendWriteBytes(data: ByteArray) = suspendCancellableCoroutine<Unit> { cont ->
        FileOutputStream(this, true).use {
            it.write(data)
            it.flush()
        }
        cont.safeResume(Unit)
    }
}