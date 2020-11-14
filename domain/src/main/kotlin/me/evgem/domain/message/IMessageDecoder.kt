package me.evgem.domain.message

import me.evgem.domain.model.Message

interface IMessageDecoder {
    fun decode(bytes: ByteArray): Message?
}
