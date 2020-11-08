package me.evgem.client.connection

import me.evgem.domain.connection.IConnection

interface IConnector {
    suspend fun connect(host: String, port: Int): IConnection?
}
