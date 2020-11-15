package me.evgem.client.command.handler.impl

import me.evgem.client.model.ClientState
import me.evgem.client.model.Command
import me.evgem.domain.model.Message
import me.evgem.domain.utils.Log
import me.evgem.domain.utils.doSuspend
import java.io.File

class UploadCommandHandler(private val filesDir: File) : MessageCommandHandler<Command.Upload>() {

    override fun getMessage(command: Command.Upload): Message = Message.UploadStartRequest(command.filename)

    override suspend fun handle(command: Command.Upload, clientState: ClientState): ClientState {
        val exists = filesDir.doSuspend {
            listFiles()?.find {
                it.name == command.filename
            }?.exists() ?: false
        }
        return if (exists) {
            super.handle(command, clientState)
        } else {
            Log.i("cannot find file ${command.filename}")
            clientState
        }
    }
}
