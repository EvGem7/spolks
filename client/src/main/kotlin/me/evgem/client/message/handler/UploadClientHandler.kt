package me.evgem.client.message.handler

import me.evgem.domain.model.Message
import me.evgem.domain.utils.*
import java.io.File

class UploadClientHandler(private val filesDir: File) {

    companion object {
        private const val BUFFER_SIZE = 65536
    }

    private val uploadingFiles = HashMap<Long, FileInfo>()

    fun getUploadStartResponseHandler() = messageHandler<Message.UploadStartResponse> { message, _ ->
        val id = message.uploadId ?: kotlin.run {
            Log.i("cannot upload file ${message.filename}")
            return@messageHandler
        }
        uploadingFiles[id] = filesDir.findFileInfo(message.filename) ?: kotlin.run {
            Log.e("cannot find file ${message.filename} to upload")
            return@messageHandler
        }
    }

    fun getUploadWaitHandler() = messageHandler<Message.UploadWait> { message, connection ->
        val info = uploadingFiles[message.uploadId]
        if (info != null) {
            val part = info.getPart(
                offset = message.uploadedLength,
                bufferSize = BUFFER_SIZE.toLong(),
            )
            if (part.isNotEmpty()) {
                connection.send(
                    Message.Upload(
                        uploadId = message.uploadId,
                        data = part,
                    )
                )
                val uploaded = message.uploadedLength + part.size
                Log.i("uploading id = ${message.uploadId} ${uploaded * 100 / info.file.length()}")
            } else {
                connection.send(Message.UploadFinished(message.uploadId))
                val speed = humanReadableByteCountBin(
                    info.file.length() * 1000 / (System.currentTimeMillis() - info.startedAt)
                )
                Log.i("upload ${message.uploadId} finished. $speed / s")
            }
        } else {
            connection.send(
                Message.Upload(
                    uploadId = message.uploadId,
                    data = null,
                )
            )
            Log.e("cannot find to upload, id = ${message.uploadId}")
        }
    }
}
