package me.evgem.domain.message.impl

import me.evgem.domain.message.IMessageEncoder
import me.evgem.domain.model.Message
import java.io.ByteArrayOutputStream
import java.io.ObjectOutputStream

object MessageEncoder : IMessageEncoder {

    override fun encode(message: Message): List<Byte> = ByteArrayOutputStream().use { byteOutput ->
        ObjectOutputStream(byteOutput).use { objectOutput ->
            objectOutput.writeObject(message)
            objectOutput.flush()
        }
        byteOutput.toByteArray().toList()
    }
}
