@file:JvmName(name = "MainClass")

package me.evgem.server

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() {
    GlobalScope.launch {
        println("server 1")
        delay(2000L)
        println("server 2")
    }
    runBlocking {
        delay(3000L)
    }
}