package me.evgem.domain.utils

import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors

val singleThreadDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
