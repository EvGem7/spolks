package me.evgem.server.di

import me.evgem.server.connection.listener.IConnectionListener
import me.evgem.server.connection.listener.impl.TcpConnectionListener
import me.evgem.server.connection.listener.impl.UdpConnectionListener

fun getConnectionListener(isTcp: Boolean): IConnectionListener = if (isTcp) TcpConnectionListener() else UdpConnectionListener()
