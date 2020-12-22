package me.evgem.domain.utils

import java.net.DatagramSocket
import java.net.SocketAddress

data class DatagramSocketWrapper(
    val socket: DatagramSocket,
    val address: SocketAddress,
)
