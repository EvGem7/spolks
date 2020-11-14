package me.evgem.client.message.handler

import kotlinx.coroutines.suspendCancellableCoroutine
import me.evgem.domain.model.Message
import me.evgem.domain.utils.Log
import me.evgem.domain.utils.messageHandler
import me.evgem.domain.utils.safeResume
import java.io.File

class DownloadClientHandler {

    private val dir = File("downloads/").apply {
        mkdirs()
    }

    private val downloadingFiles = HashMap<Long, File>()

    suspend fun isAbleDownload(filename: String): Boolean = suspendCancellableCoroutine { cont ->
        val result = dir.listFiles()?.all {
            it.name != filename
        } ?: false
        cont.safeResume(result)
    }

    fun getDownloadResponseHandler() = messageHandler<Message.DownloadResponse> { message, _ ->
        val id = message.downloadId ?: return@messageHandler
        val file = createFile(message.filename)
        downloadingFiles[id] = file
    }

    fun getDownloadHandler() = messageHandler<Message.Download> { message, _ ->
        val file = downloadingFiles[message.downloadId] ?: kotlin.run {
            Log.e("got download message with unknown id ${message.downloadId}")
            return@messageHandler
        }
        file.suspendWriteBytes(message.data)
    }

    fun getDownloadFinishedHandler() = messageHandler<Message.DownloadFinished> { message, _ ->
        downloadingFiles.remove(message.downloadId) ?: kotlin.run {
            Log.e("download finished but there is no downloading with id = ${message.downloadId}")
        }
    }

    private suspend fun createFile(filename: String): File = suspendCancellableCoroutine { cont ->
        val file = File(dir, filename)
        if (!file.exists()) {
            file.createNewFile()
        }
        cont.safeResume(file)
    }

    private suspend fun File.suspendWriteBytes(data: ByteArray) = suspendCancellableCoroutine<Unit> { cont ->
        outputStream().use {
            it.write(data)
            it.flush()
        }
        cont.safeResume(Unit)
    }
}
