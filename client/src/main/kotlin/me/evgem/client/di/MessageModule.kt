package me.evgem.client.di

import me.evgem.client.message.handler.DownloadClientHandler
import me.evgem.client.message.provider.ClientMessageHandlerProvider
import me.evgem.domain.model.IMessageHandlerProvider

private val downloadHandler = DownloadClientHandler()

private val provider = ClientMessageHandlerProvider(getDownloadMessageHandler())

fun getMessageHandlerProvider(): IMessageHandlerProvider = provider

fun getDownloadMessageHandler(): DownloadClientHandler = downloadHandler
