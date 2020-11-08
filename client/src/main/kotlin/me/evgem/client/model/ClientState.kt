package me.evgem.client.model

import me.evgem.client.connection.IConnector
import me.evgem.domain.connection.IConnection

data class ClientState(
    val connector: IConnector,
    val connection: IConnection?,
)
