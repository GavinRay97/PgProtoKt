import domain.FrontendMessage
import io.netty5.channel.ChannelHandlerContext

interface IPostgresFrontendMessageHandler {
    fun handleSSLRequest(ctx: ChannelHandlerContext, msg: FrontendMessage.SSLRequest)
    fun handleStartup(ctx: ChannelHandlerContext, msg: FrontendMessage.Startup)
    fun handleQuery(ctx: ChannelHandlerContext, msg: FrontendMessage.Query)
}
