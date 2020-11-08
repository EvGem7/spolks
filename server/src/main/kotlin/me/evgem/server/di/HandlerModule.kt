package me.evgem.server.di

import me.evgem.domain.model.IMessageHandlerProvider
import me.evgem.server.message.provider.ServerMessageHandlerProvider

fun getMessageHandlerProvider(): IMessageHandlerProvider = ServerMessageHandlerProvider()
