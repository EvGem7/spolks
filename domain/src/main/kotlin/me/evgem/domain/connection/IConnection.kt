package me.evgem.domain.connection

import kotlinx.coroutines.flow.Flow
import me.evgem.domain.model.Message

interface IConnection {

    fun messages(): Flow<Message>

    suspend fun send(message: Message)
}