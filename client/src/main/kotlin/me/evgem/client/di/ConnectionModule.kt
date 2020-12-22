package me.evgem.client.di

import me.evgem.client.connection.IConnector
import me.evgem.client.connection.impl.TcpConnector
import me.evgem.client.connection.impl.UdpConnector

fun getConnector(isTcp: Boolean): IConnector = if (isTcp) TcpConnector() else UdpConnector()
