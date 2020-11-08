@file:JvmName(name = "MainClass")

package me.evgem.client

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() {
    GlobalScope.launch {
        println("client 1")
        delay(2000L)
        println("client 2")
    }
    runBlocking {
        delay(3000L)
    }
}