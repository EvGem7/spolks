package me.evgem.domain.di

import me.evgem.domain.message.IMessageDecoder
import me.evgem.domain.message.IMessageEncoder
import me.evgem.domain.message.impl.MessageDecoder
import me.evgem.domain.message.impl.MessageEncoder

fun getMessageDecoder(): IMessageDecoder = MessageDecoder()

fun getMessageEncoder(): IMessageEncoder = MessageEncoder
