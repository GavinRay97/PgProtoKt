import calcite.CalciteProvider
import domain.BackendMessage
import domain.BackendMessage.CommandComplete.CommandType
import domain.BackendMessage.ReadyForQuery.TransactionStatus
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
        logger.debug("Query message seen: {}", msg.query)

        val sanitizedQuery = msg.query.removeSuffix(";")
        val rows = mutableListOf<Map<String, Any?>>()

        CalciteProvider.connection.createStatement().executeQuery(sanitizedQuery).use { rs ->
            val md = rs.metaData
            while (rs.next()) {
                val row = mutableMapOf<String, Any?>()
                for (i in 1..md.columnCount) {
                    // HUGE NOTE!!!!
                    // I am casting every value toString() here just to get this to work
                    val asString = rs.getObject(i).toString()
                    row[md.getColumnLabel(i)] = asString
                }
                rows.add(row)
            }
        }

        println("Rows: $rows")

        val columns = rows.first().keys.toList()
        val rd = BackendMessage.RowDescription(
            fields = columns.map { TextField(name = it) }
        )

        ctx.write(rd)
        for (row in rows) {
            ctx.write(BackendMessage.DataRow(row.values.toList()))
        }

        ctx.write(BackendMessage.CommandComplete(affectedRows = 1, type = CommandType.SELECT))
        ctx.write(BackendMessage.ReadyForQuery(TransactionStatus.IDLE))
        ctx.flush()
    }

}
