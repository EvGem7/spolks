package me.evgem.domain.utils

import java.util.*

object Log {

    private const val DEBUG = true

    fun i(msg: String) {
        println("${time()} $msg")
    }

    fun e(t: Throwable) {
        System.err.println("${time()} ${t.stackTraceToString()}")
    }

    fun d(msg: String) {
        if (DEBUG) {
            println("${time()} $msg")
        }
    }

    private fun time() = "${Date()}"
}
