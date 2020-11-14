package me.evgem.domain.message

import me.evgem.domain.model.Message

interface IMessageEncoder {
    fun encode(message: Message): ByteArray
}
