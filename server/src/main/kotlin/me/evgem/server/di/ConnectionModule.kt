package me.evgem.server.di

import me.evgem.server.connection.listener.IConnectionListener
import me.evgem.server.connection.listener.serverSocket.TcpConnectionListener

fun getConnectionListener(): IConnectionListener = TcpConnectionListener()
