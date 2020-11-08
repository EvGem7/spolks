package me.evgem.domain.model

import kotlinx.coroutines.flow.Flow

interface IConnection {

    fun messages(): Flow<Message>

    suspend fun send(message: Message)
}