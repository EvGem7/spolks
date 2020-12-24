package me.evgem.domain.utils

import java.util.*

object Log {

    private const val DEBUG = false

    fun i(msg: String) {
        println("${time()} $msg")
    }

    fun e(t: Throwable) {
        System.err.println("${time()} ${t.stackTraceToString()}")
    }

    fun e(msg: String) {
        System.err.println("${time()} $msg")
    }

    fun d(msg: String) {
        if (DEBUG) {
            println("${time()} $msg")
        }
    }

    private fun time() = "${Date()}"
}
