package me.evgem.domain.utils

import kotlinx.coroutines.suspendCancellableCoroutine

suspend inline fun <T, R> T.doSuspend(crossinline block: T.() -> R): R = suspendCancellableCoroutine { cont ->
    cont.safeResume(block())
}