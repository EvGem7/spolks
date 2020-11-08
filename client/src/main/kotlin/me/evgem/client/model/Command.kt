package me.evgem.client.model

sealed class Command {

    data class Echo(val msg: String) : Command()

    object Time : Command()

    object Close : Command()

    data class Download(val filename: String) : Command()

    data class Upload(val filename: String) : Command()

    object Stop : Command()

    data class Connect(
        val host: String,
        val port: Int
    ) : Command()
}
