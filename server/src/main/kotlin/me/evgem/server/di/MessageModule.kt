package me.evgem.server.di

import me.evgem.domain.model.IMessageHandlerProvider
import me.evgem.server.message.handler.DownloadServerHandler
import me.evgem.server.message.handler.UploadServerHandler
import me.evgem.server.message.provider.ServerMessageHandlerProvider
import java.io.File

private val filesDir = File("filesServer/").apply {
    mkdirs()
}

private val downloadHandler = DownloadServerHandler(getFilesDir())
private val uploadHandler = UploadServerHandler(getFilesDir())

private val provider = ServerMessageHandlerProvider(
    getDownloadMessageHandler(),
    getUploadMessageHandler(),
)

fun getFilesDir(): File = filesDir

fun getMessageHandlerProvider(): IMessageHandlerProvider = provider

fun getDownloadMessageHandler(): DownloadServerHandler = downloadHandler

fun getUploadMessageHandler(): UploadServerHandler = uploadHandler
