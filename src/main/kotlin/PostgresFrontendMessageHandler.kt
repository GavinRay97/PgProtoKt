import domain.FrontendMessage
import io.netty5.channel.ChannelHandlerContext
import io.netty5.channel.SimpleChannelInboundHandler
import org.apache.logging.log4j.LogManager

class PostgresFrontendMessageHandler(private val handler: IPostgresFrontendMessageHandler) :
    SimpleChannelInboundHandler<FrontendMessage>() {

    private val logger = LogManager.getLogger(PostgresFrontendMessageHandler::class.java)

    override fun messageReceived(ctx: ChannelHandlerContext, msg: FrontendMessage) {
        logger.debug("Message received: {}", msg)
        // Dispatch messages to delegate interface handler
        when (msg) {
            is FrontendMessage.Startup -> handler.handleStartup(ctx, msg)
            is FrontendMessage.SSLRequest -> handler.handleSSLRequest(ctx, msg)
            is FrontendMessage.Query -> handler.handleQuery(ctx, msg)
            else -> {
                logger.debug("Unknown message type: {}", msg)
                throw UnsupportedOperationException("Unknown message type: $msg")
            }
        }
    }

}
