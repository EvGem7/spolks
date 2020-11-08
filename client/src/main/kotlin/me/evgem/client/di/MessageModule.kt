package me.evgem.client.di

import me.evgem.client.message.provider.ClientMessageHandlerProvider
import me.evgem.domain.model.IMessageHandlerProvider

fun getMessageHandlerProvider(): IMessageHandlerProvider = ClientMessageHandlerProvider()
