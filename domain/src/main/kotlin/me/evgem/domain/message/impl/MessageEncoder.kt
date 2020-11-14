package me.evgem.domain.message.impl

import me.evgem.domain.message.IMessageEncoder
import me.evgem.domain.model.Message
import java.io.ByteArrayOutputStream
import java.io.ObjectOutputStream

object MessageEncoder : IMessageEncoder {

    override fun encode(message: Message): ByteArray = ByteArrayOutputStream().use { byteOutput ->
        ObjectOutputStream(byteOutput).use { objectOutput ->
            objectOutput.writeObject(message)
            objectOutput.flush()
        }
        byteOutput.toByteArray()
    }
}
