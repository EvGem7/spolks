package me.evgem.domain.utils

import kotlinx.coroutines.Dispatchers
import java.security.Permission

val singleThreadDispatcher = run {
    System.setSecurityManager(object : SecurityManager() {
        override fun checkPermission(perm: Permission?) {

        }

        override fun checkPermission(perm: Permission?, context: Any?) {

        }
    })
    System.setProperty("kotlinx.coroutines.scheduler", "off")
    System.setProperty("kotlinx.coroutines.default.parallelism", "1")
    Dispatchers.Default
}
