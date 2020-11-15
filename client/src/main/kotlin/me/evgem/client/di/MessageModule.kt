package me.evgem.client.di

import me.evgem.client.message.handler.DownloadClientHandler
import me.evgem.client.message.handler.UploadClientHandler
import me.evgem.client.message.provider.ClientMessageHandlerProvider
import me.evgem.domain.model.IMessageHandlerProvider
import java.io.File

private val filesDir = File("filesClient/").apply {
    mkdirs()
}

private val downloadHandler = DownloadClientHandler(getFilesDir())

private val uploadHandler = UploadClientHandler(getFilesDir())

private val provider = ClientMessageHandlerProvider(
    getDownloadMessageHandler(),
    getUploadMessageHandler(),
)

fun getFilesDir(): File = filesDir

fun getMessageHandlerProvider(): IMessageHandlerProvider = provider

fun getDownloadMessageHandler(): DownloadClientHandler = downloadHandler

fun getUploadMessageHandler(): UploadClientHandler = uploadHandler
