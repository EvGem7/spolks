package me.evgem.domain.connection.udp

import kotlinx.coroutines.flow.Flow
import me.evgem.domain.connection.IConnection
import me.evgem.domain.message.IMessageDecoder
import me.evgem.domain.message.IMessageEncoder
import me.evgem.domain.model.Message
import java.net.DatagramSocket

class UdpConnection(
    private val socket: DatagramSocket,
    private val messageEncoder: IMessageEncoder,
    private val messageDecoder: IMessageDecoder,
) : IConnection {

    override fun messages(): Flow<Message> {
        TODO("Not yet implemented")
    }

    override suspend fun send(message: Message) {
        TODO("Not yet implemented")
    }

    override suspend fun close() {
        TODO("Not yet implemented")
    }
}
