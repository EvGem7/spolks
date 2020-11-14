package me.evgem.domain.message.impl

import me.evgem.domain.message.IMessageDecoder
import me.evgem.domain.message.MessageDecodeException
import me.evgem.domain.model.Message
import me.evgem.domain.utils.Log
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.ObjectInputStream

class MessageDecoder : IMessageDecoder {

    private val byteArrayOutputStream = ByteArrayOutputStream()

    override fun decode(bytes: ByteArray): Message? {
        byteArrayOutputStream.writeBytes(bytes)
        val inputBytes = ByteArrayInputStream(byteArrayOutputStream.toByteArray())
        return try {
            ObjectInputStream(inputBytes).use { inputObjects ->
                val message = inputObjects.readObject() ?: return null
                if (message is Message) {
                    val remaining = inputBytes.readBytes()
                    byteArrayOutputStream.reset()
                    byteArrayOutputStream.writeBytes(remaining)
                    message
                } else {
                    throw MessageDecodeException()
                }
            }
        } catch (e: ClassNotFoundException) {
            Log.e(e)
            null
        } catch (e: IOException) {
            null
        } finally {
            inputBytes.close()
        }
    }
}
