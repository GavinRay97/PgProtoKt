package utils

import io.netty5.buffer.ByteBuf
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

object ByteBufUtils {
    fun readCString(buffer: ByteBuf): String? {
        val bytes = ByteArray(buffer.bytesBefore(0.toByte()) + 1)
        println("bytes: ${bytes.size}")
        if (bytes.isEmpty()) {
            return null
        }
        buffer.readBytes(bytes)
        return String(bytes, 0, bytes.size - 1, StandardCharsets.UTF_8)
    }

    fun readCharArray(buffer: ByteBuf): CharArray? {
        val bytes = ByteArray(buffer.bytesBefore(0.toByte()) + 1)
        if (bytes.isEmpty()) {
            return null
        }
        buffer.readBytes(bytes)
        return StandardCharsets.UTF_8.decode(ByteBuffer.wrap(bytes)).array()
    }

    fun writeCString(buffer: ByteBuf, str: String) {
        buffer.writeBytes(str.toByteArray(StandardCharsets.UTF_8))
        buffer.writeByte(0)
    }
}
