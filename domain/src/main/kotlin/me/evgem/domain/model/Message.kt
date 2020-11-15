package me.evgem.domain.model

import java.io.Serializable

sealed class Message : Serializable {

    data class Echo(val msg: String) : Message()

    data class Time(val time: Long?) : Message()

    object Close : Message()


    data class DownloadStartRequest(val filename: String) : Message()

    data class DownloadStartResponse(
        val filename: String,
        val length: Long,
        val downloadId: Long?,
    ) : Message()

    data class DownloadRequest(
        val downloadId: Long,
        val downloadedLength: Long,
    ) : Message()

    data class Download(
        val downloadId: Long,
        val data: ByteArray?,
    ) : Message()

    data class DownloadFinished(val downloadId: Long) : Message()


    data class UploadRequest(val filename: String) : Message()

    data class UploadResponse(val uploadId: Long?) : Message()

    data class Upload(
        val uploadId: Long,
        val data: ByteArray
    ) : Message()

    data class UploadFinished(val uploadId: Long) : Message()


    object Ping : Message()
}