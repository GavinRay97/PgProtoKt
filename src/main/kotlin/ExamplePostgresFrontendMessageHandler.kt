import domain.BackendMessage
import domain.BackendMessage.CommandComplete.CommandType
import domain.BackendMessage.ReadyForQuery.TransactionStatus
import domain.FrontendMessage
import domain.PostgresDataFormat
import io.netty5.channel.ChannelHandlerContext
import org.apache.logging.log4j.LogManager
import utils.PgTypeTable

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
                BackendMessage.RowDescription.Field(
                    name = "id",
                    tableOid = 0,
                    columnIdx = 1,
                    dataTypeOid = PgTypeTable.int4.oid,
                    dataTypeSize = PgTypeTable.int4.byteLength,
                    dataTypeModifier = -1,
                    format = PostgresDataFormat.TEXT
                ),
                BackendMessage.RowDescription.Field(
                    name = "email",
                    tableOid = 0,
                    columnIdx = 2,
                    dataTypeOid = PgTypeTable.text.oid,
                    dataTypeSize = PgTypeTable.text.byteLength,
                    dataTypeModifier = -1,
                    format = PostgresDataFormat.TEXT
                )
            )
        )
        ctx.write(rd)

        // Create 3 rows
        for (i in 1..3) {
            ctx.write(BackendMessage.DataRow(columns = listOf(i, "user$i@site.com")))
        }

        ctx.write(BackendMessage.CommandComplete(affectedRows = 1, type = CommandType.SELECT))
        ctx.write(BackendMessage.ReadyForQuery(TransactionStatus.IDLE))
        ctx.flush()
    }

}
