package me.evgem.client.di

import me.evgem.client.connection.IConnector
import me.evgem.client.connection.socket.TcpConnector

fun getConnector(): IConnector = TcpConnector()
