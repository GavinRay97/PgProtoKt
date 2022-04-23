package codecs

import domain.BackendMessage
import domain.BackendMessage.CommandComplete.CommandType
import io.netty5.buffer.ByteBuf
import io.netty5.buffer.Unpooled
import io.netty5.channel.ChannelHandler
import io.netty5.channel.ChannelHandlerContext
import io.netty5.handler.codec.MessageToByteEncoder
import org.apache.logging.log4j.LogManager
import utils.ByteBufUtils
import java.nio.charset.StandardCharsets

// Sharable because it's stateless -- we can re-use the same instance for every message
@ChannelHandler.Sharable
object PostgresBackendMessageEncoder : MessageToByteEncoder<BackendMessage>() {

    private val logger = LogManager.getLogger(PostgresBackendMessageEncoder::class.java)

    override fun encode(ctx: ChannelHandlerContext, msg: BackendMessage, out: ByteBuf) {
        out.writeByte(msg.id.code)
        when (msg) {
            is BackendMessage.AuthenticationOk -> {
                logger.debug("AuthenticationOk")
                out.writeInt(8) // length
                out.writeInt(0) // success
            }
            is BackendMessage.BackendKeyData -> {
                logger.debug("BackendKeyData")
                out.writeInt(12) // length
                out.writeInt(msg.processId)
                out.writeInt(msg.secretKey)
            }
            is BackendMessage.ReadyForQuery -> {
                logger.debug("ReadyForQuery")
                out.writeInt(5) // length
                out.writeByte(msg.transactionStatus.value.code)
            }
            is BackendMessage.RowDescription -> {
                logger.debug("RowDescription")
                var length = 0

                out.writeInt(0) // Will be modified after all fields are written
                length += 4

                out.writeShort(msg.fields.size)
                length += 2

                msg.fields.forEach {
                    ByteBufUtils.writeCString(out, it.name)
                    length += it.name.length + 1 // Add 1 for the null terminator

                    out.writeInt(it.tableOid)
                    length += 4

                    out.writeShort(it.columnIdx)
                    length += 2

                    out.writeInt(it.dataTypeOid)
                    length += 4

                    out.writeShort(it.dataTypeSize)
                    length += 2

                    out.writeInt(it.dataTypeModifier)
                    length += 4

                    out.writeShort(it.format.id)
                    length += 2
                }

                // Update length of message with calculated length
                out.setInt(1, length)
            }
            is BackendMessage.DataRow -> {
                logger.debug("DataRow")

                // the following pair of fields appear for each column:
                //
                //  Int32
                //  The length of the column value, in bytes (this count does not include itself). Can be zero. As a special case, -1 indicates a NULL column value. No value bytes follow in the NULL case.
                //
                //  Byten
                //  The value of the column, in the format indicated by the associated format code. n is the above length.
                val rowBuffer = Unpooled.buffer()
                msg.columns.forEach {
                    when (it) {
                        is String -> {
                            val writerIndex = rowBuffer.writerIndex()
                            rowBuffer.writeInt(0)
                            val bytesWritten = rowBuffer.writeCharSequence(it, StandardCharsets.UTF_8)
                            rowBuffer.setInt(writerIndex, bytesWritten)
                        }
                        is Int -> {
                            rowBuffer.writeInt(4)
                            rowBuffer.writeInt(it)
                        }
                        is Long -> {
                            rowBuffer.writeInt(8)
                            rowBuffer.writeLong(it)
                        }
                        is Double -> {
                            rowBuffer.writeInt(8)
                            rowBuffer.writeDouble(it)
                        }
                        is Float -> {
                            rowBuffer.writeInt(4)
                            rowBuffer.writeFloat(it)
                        }
                        is ByteArray -> {
                            rowBuffer.writeInt(it.size)
                            rowBuffer.writeBytes(it)
                        }
                        is Boolean -> {
                            rowBuffer.writeInt(1)
                            rowBuffer.writeByte(if (it) 1 else 0)
                        }
                        else -> {
                            throw IllegalArgumentException("Unsupported type: ${it::class.java.simpleName}")
                        }
                    }
                }

                val rowLength = rowBuffer.readableBytes()
                logger.debug("Row length: $rowLength")

                // Int32 - Length of message contents in bytes, including self.
                val totalLength = rowLength + 4 + 2 // 4 for length, 2 for column count
                logger.debug("DataRow total length: $totalLength")

                out.writeInt(totalLength)
                out.writeShort(msg.columns.size)
                out.writeBytes(rowBuffer)
            }
            is BackendMessage.CommandComplete -> {
                logger.debug("CommandComplete")

                val command = when (msg.type) {
                    CommandType.INSERT -> "INSERT 0 ${msg.affectedRows}"
                    CommandType.UPDATE -> "UPDATE ${msg.affectedRows}"
                    CommandType.DELETE -> "DELETE ${msg.affectedRows}"
                    CommandType.SELECT -> "SELECT ${msg.affectedRows}"
                    CommandType.COPY -> "COPY ${msg.affectedRows}"
                    CommandType.MOVE -> "MOVE ${msg.affectedRows}"
                    CommandType.FETCH -> "FETCH ${msg.affectedRows}"
                }

                val commandStrSize = command.toByteArray(Charsets.UTF_8).size + 1 // +1 for null terminator
                out.writeInt(commandStrSize + 4) // length
                ByteBufUtils.writeCString(out, command)
            }
            else -> throw UnsupportedOperationException("Unsupported message: $msg")
        }
    }

}
