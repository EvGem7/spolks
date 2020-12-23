package me.evgem.client.message.handler

import kotlinx.coroutines.suspendCancellableCoroutine
import me.evgem.domain.model.Message
import me.evgem.domain.utils.Log
import me.evgem.domain.utils.humanReadableByteCountBin
import me.evgem.domain.utils.messageHandler
import me.evgem.domain.utils.safeResume
import java.io.File
import java.io.FileOutputStream

class DownloadClientHandler(private val filesDir: File) {

    private data class DownloadingInfo(
        val file: File,
        val length: Long,
        val startedAt: Long = System.currentTimeMillis(),
    )


    private val downloadingFiles = HashMap<Long, DownloadingInfo>()

    fun getDownloadStartResponseHandler() = messageHandler<Message.DownloadStartResponse> { message, connection ->
        Log.i("server download start response id = ${message.downloadId}")
        val id = message.downloadId ?: kotlin.run {
            Log.i("cannot download file ${message.filename}")
            return@messageHandler
        }
        val info = downloadingFiles.getOrPut(id) {
            val file = createFile(message.filename)
            DownloadingInfo(
                file = file,
                length = message.length,
            )
        }
        connection.send(Message.DownloadWait(id, info.file.length()))
    }

    fun getDownloadHandler() = messageHandler<Message.Download> { message, connection ->
        val info = downloadingFiles[message.downloadId] ?: kotlin.run {
            Log.e("got download message with unknown id ${message.downloadId}")
            return@messageHandler
        }

        val data = message.data
        if (data == null) {
            Log.i("error occurred while downloading, id = ${message.downloadId}")
            return@messageHandler
        }
        info.file.suspendWriteBytes(data)
//        connection.send(Message.DownloadWait(message.downloadId, info.file.length()))
        Log.i("downloading id=${message.downloadId} ${info.file.length() * 100 / info.length}%")
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

    fun getDownloadedLength(downloadId: Long): Long = downloadingFiles[downloadId]?.file?.length() ?: 0L

    private suspend fun createFile(filename: String): File = suspendCancellableCoroutine { cont ->
        val file = File(filesDir, filename)
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
