package me.evgem.server.message.handler

import me.evgem.domain.model.Message
import me.evgem.domain.utils.Log
import me.evgem.domain.utils.doSuspend
import me.evgem.domain.utils.messageHandler
import java.io.File
import java.io.FileOutputStream

class UploadServerHandler(private val filesDir: File) {

    private val uploadingFiles = HashMap<Long, File>()

    private var idCounter = System.currentTimeMillis()

    fun getUploadStartRequestHandler() = messageHandler<Message.UploadStartRequest> { message, connection ->
        Log.i("upload request from client")
        val isUploading = uploadingFiles.values.any {
            it.name == message.filename
        }
        if (isUploading) {
            connection.send(
                Message.UploadStartResponse(
                    filename = message.filename,
                    uploadId = null,
                )
            )
            Log.i("try to upload already uploading file")
        } else {
            val file = File(filesDir, message.filename)
            file.doSuspend {
                if (exists()) {
                    delete()
                }
                createNewFile()
            }

            val id = generateId()
            uploadingFiles[id] = file

            connection.send(
                Message.UploadStartResponse(
                    filename = message.filename,
                    uploadId = id,
                )
            )
            connection.send(
                Message.UploadWait(
                    uploadId = id,
                    uploadedLength = 0L,
                )
            )
        }
    }

    fun getUploadWaitHandler() = messageHandler<Message.UploadWait> { message, connection ->
        uploadingFiles[message.uploadId]?.let {
            connection.send(
                Message.UploadWait(
                    uploadId = message.uploadId,
                    it.length(),
                )
            )
        } ?: kotlin.run {
            Log.i("got upload wait from client when there is no uploading with id = ${message.uploadId}")
        }
    }

    fun getUploadHandler() = messageHandler<Message.Upload> { message, connection ->
        val file = uploadingFiles[message.uploadId] ?: kotlin.run {
            Log.i("got upload from client when there is no upload with id = ${message.data}")
            return@messageHandler
        }
        val data = message.data
        if (data == null) {
            Log.i("error occurred while uploading, id = ${message.uploadId}")
            return@messageHandler
        }
        file.doSuspend {
            FileOutputStream(this, true).use {
                it.write(data)
                it.flush()
            }
        }
        connection.send(
            Message.UploadWait(
                uploadId = message.uploadId,
                file.length(),
            )
        )
    }

    fun getUploadFinishedHandler() = messageHandler<Message.UploadFinished> { message, _ ->
        uploadingFiles.remove(message.uploadId) ?: kotlin.run {
            Log.i("upload finished but there is no upload with id = ${message.uploadId}")
        }
        Log.i("upload finished, id = ${message.uploadId}")
    }

    private fun generateId(): Long = idCounter++
}
