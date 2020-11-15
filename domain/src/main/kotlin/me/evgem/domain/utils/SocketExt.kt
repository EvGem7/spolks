package me.evgem.domain.utils

import java.net.ServerSocket
import java.net.Socket

inline fun <T> Socket.withTimeout(timeout: Int, block: (Socket) -> T):T {
    val cur = soTimeout
    try {
        soTimeout = timeout
        return block(this)
    } finally {
        soTimeout = cur
    }
}

inline fun <T> ServerSocket.withTimeout(timeout: Int, block: (ServerSocket) -> T):T {
    val cur = soTimeout
    try {
        soTimeout = timeout
        return block(this)
    } finally {
        soTimeout = cur
    }
}
