package me.evgem.domain.utils

import java.util.*

object Log {

    fun i(msg: String) {
        println("${time()} $msg")
    }

    fun e(t: Throwable) {
        System.err.println("${time()} ${t.stackTraceToString()}")
    }

    private fun time() = "${Date()}"
}
