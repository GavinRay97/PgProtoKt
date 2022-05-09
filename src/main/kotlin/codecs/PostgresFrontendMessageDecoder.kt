package codecs

import domain.FrontendMessage
import domain.FrontendMessageType
import io.netty5.buffer.ByteBuf
import io.netty5.channel.ChannelHandlerContext
import io.netty5.handler.codec.MessageToMessageDecoder
import org.apache.logging.log4j.LogManager
import utils.ByteBufUtils

class PostgresFrontendMessageDecoder : MessageToMessageDecoder<ByteBuf>() {

    private val logger = LogManager.getLogger(PostgresFrontendMessageDecoder::class.java)

    private var startupMessageSeen = false
    private var sslNegotiationMessageSeen = false


    override fun decode(ctx: ChannelHandlerContext, msg: ByteBuf) {
        if (!startupMessageSeen) {
            val length = msg.readInt()
            val protocolVersion = msg.readInt()
            if (protocolVersion == 80877103) {
                // This is an SSL Negotiation message
                sslNegotiationMessageSeen = true
                logger.debug("SSL Negotiation message seen")
                ctx.fireChannelRead(FrontendMessage.SSLRequest())
            } else {
                // This is the actual startup message
                startupMessageSeen = true
                val properties = readStartupMessage(msg)
                val startupMessage = FrontendMessage.Startup(protocolVersion, properties)
                logger.debug("Startup message seen: {}", startupMessage)
                ctx.fireChannelRead(startupMessage)
            }
        } else {
            // This is a regular message
            val messageId = msg.readByte().toInt().toChar()
            logger.debug("Message id: {}", messageId)

            val type = FrontendMessageType.fromId(messageId)
            when (type) {
                FrontendMessageType.Query -> {
                    val length = msg.readInt()
                    val query = ByteBufUtils.readCString(msg)
                    logger.debug("Query: {}", query)
                    if (query != null) {
                        ctx.fireChannelRead(FrontendMessage.Query(query))
                    }
                }
                else -> {
                    logger.debug("Unknown message type: {}", type)
                    throw UnsupportedOperationException("Unknown message type: $type")
                }
            }
        }
    }

    private fun readStartupMessage(buffer: ByteBuf): Map<String, String> {
        val properties = mutableMapOf<String, String>()
        while (true) {
            val key = ByteBufUtils.readCString(buffer) ?: break
            val value = ByteBufUtils.readCString(buffer)
            logger.trace("payload: key={} value={}", key, value)
            if ("" != key && "" != value) {
                properties[key] = value!!
            }
        }
        return properties
    }

}
