package me.evgem.domain.utils

import kotlinx.coroutines.suspendCancellableCoroutine
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

suspend inline fun <T> Socket.doSuspend(crossinline block: Socket.() -> T): T = suspendCancellableCoroutine { cont ->
    cont.safeResume(block())
}
