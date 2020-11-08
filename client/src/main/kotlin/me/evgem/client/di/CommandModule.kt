package me.evgem.client.di

import me.evgem.client.command.handler.provider.ICommandHandlerProvider
import me.evgem.client.command.handler.provider.impl.CommandHandlerProvider

fun getCommandHandlerProvider(): ICommandHandlerProvider = CommandHandlerProvider()
