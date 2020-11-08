package me.evgem.client.di

import me.evgem.client.connection.IConnector
import me.evgem.client.connection.socket.SocketConnector

fun getConnector(): IConnector = SocketConnector()
