package me.evgem.server.connection.listener

import kotlinx.coroutines.flow.Flow
import me.evgem.domain.model.IConnection

interface IConnectionListener {
    fun connections(): Flow<IConnection>
}
