package me.evgem.server.di

import me.evgem.domain.model.IMessageHandlerProvider
import me.evgem.server.message.provider.ServerMessageHandlerProvider

private val provider = ServerMessageHandlerProvider()

fun getMessageHandlerProvider(): IMessageHandlerProvider = provider
