package me.evgem.server.di

import me.evgem.domain.model.IMessageHandlerProvider
import me.evgem.server.connection.handler.ServerMessageHandlerProvider

fun getMessageHandlerProvider(): IMessageHandlerProvider = ServerMessageHandlerProvider()
