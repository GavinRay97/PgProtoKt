import domain.BackendMessage
import domain.BackendMessage.CommandComplete.CommandType
import domain.BackendMessage.ReadyForQuery.TransactionStatus
import domain.BackendMessage.RowDescription.BooleanField
import domain.BackendMessage.RowDescription.Int4Field
import domain.BackendMessage.RowDescription.NumericField
import domain.BackendMessage.RowDescription.TextField
import domain.FrontendMessage
import io.netty5.channel.ChannelHandlerContext
import org.apache.logging.log4j.LogManager

object ExamplePostgresFrontendMessageHandler : IPostgresFrontendMessageHandler {
    private val logger = LogManager.getLogger(ExamplePostgresFrontendMessageHandler::class.java)

    // TODO: Refactor to use "BackendStatus.SSLSupported"/"BackendStatus.SSLNotSupported" classes
    override fun handleSSLRequest(ctx: ChannelHandlerContext, msg: FrontendMessage.SSLRequest) {
        logger.debug("SSLRequest")
        val buffer = ctx.alloc().buffer()
        buffer.writeByte('N'.code)
        ctx.writeAndFlush(buffer)
    }

    override fun handleStartup(ctx: ChannelHandlerContext, msg: FrontendMessage.Startup) {
        logger.debug("Startup message seen: {}", msg)
        ctx.write(BackendMessage.AuthenticationOk())
        ctx.write(BackendMessage.BackendKeyData(processId = 1, secretKey = 2))
        ctx.write(BackendMessage.ReadyForQuery(TransactionStatus.IDLE))
        ctx.flush()
    }

    override fun handleQuery(ctx: ChannelHandlerContext, msg: FrontendMessage.Query) {
        logger.debug("Query message seen: {}", msg)

        // Fake "user" rows which have "id" and "email" columns
        val rd = BackendMessage.RowDescription(
            fields = listOf(
                Int4Field("id"),
                TextField("email"),
                NumericField("some_float"),
                BooleanField("some_bool")
            )
        )

        ctx.write(rd)

        // Create 3 rows
        for (i in 1..3) {
            ctx.write(
                BackendMessage.DataRow(
                    listOf(i, "user$i@site.com", i * 1.23, i % 2 == 0)
                )
            )
        }

        ctx.write(BackendMessage.CommandComplete(affectedRows = 1, type = CommandType.SELECT))
        ctx.write(BackendMessage.ReadyForQuery(TransactionStatus.IDLE))
        ctx.flush()
    }

}
