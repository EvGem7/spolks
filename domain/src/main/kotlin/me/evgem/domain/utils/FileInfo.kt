package me.evgem.domain.utils

import java.io.File
import java.io.RandomAccessFile
import kotlin.math.min

data class FileInfo(
    val file: File,
    val randomAccess: RandomAccessFile,
    val startedAt: Long = System.currentTimeMillis(),
) {
    suspend fun getPart(
        offset: Long,
        bufferSize: Long,
    ): ByteArray = doSuspend {
        randomAccess.seek(offset)
        val buffer = ByteArray(min(bufferSize, file.length() - offset).toInt())
        randomAccess.read(buffer)
        buffer
    }
}

suspend fun File.findFileInfo(filename: String): FileInfo? = doSuspend {
    listFiles()?.find {
        it.name == filename
    }?.let {
        FileInfo(it, RandomAccessFile(it, "r"))
    }
}