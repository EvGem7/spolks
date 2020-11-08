package me.evgem.domain.model

enum class MessageType(val id: Byte) {
    Echo(0),
    Time(1),
    Close(2),
    DownloadRequest(3),
    DownloadResponse(4),
    Download(5),
    DownloadFinished(6),
    UploadRequest(7),
    UploadResponse(8),
    Upload(9),
    UploadFinished(10);

    fun from(message: Message): MessageType = when (message) {
        is Message.Echo -> Echo
        is Message.Time -> Time
        is Message.Close -> Close
        is Message.DownloadRequest -> DownloadRequest
        is Message.DownloadResponse -> DownloadResponse
        is Message.Download -> Download
        is Message.DownloadFinished -> DownloadFinished
        is Message.UploadRequest -> UploadRequest
        is Message.UploadResponse -> UploadResponse
        is Message.Upload -> Upload
        is Message.UploadFinished -> UploadFinished
    }
}
