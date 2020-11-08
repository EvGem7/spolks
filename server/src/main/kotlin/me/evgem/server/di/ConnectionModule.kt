package me.evgem.server.di

import me.evgem.server.connection.listener.IConnectionListener
import me.evgem.server.connection.listener.serverSocket.ServerSocketConnectionListener

fun getConnectionListener(): IConnectionListener = ServerSocketConnectionListener()
