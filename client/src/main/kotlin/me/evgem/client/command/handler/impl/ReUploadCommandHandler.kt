package me.evgem.client.command.handler.impl

import me.evgem.client.model.Command
import me.evgem.domain.model.Message

object ReUploadCommandHandler : MessageCommandHandler<Command.ReUpload>() {

    override fun getMessage(command: Command.ReUpload): Message = Message.UploadWait(
        command.uploadId,
        uploadedLength = 0, // don't care
    )
}
