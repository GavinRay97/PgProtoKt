package domain

sealed class FrontendMessage(val id: Char?) {
    class Startup(val version: Int, val parameters: Map<String, String>) : FrontendMessage(null)
    class SSLRequest : FrontendMessage(null)
    class Bind(val name: String, val parameters: Map<String, String>) : FrontendMessage('B')
    class Close : FrontendMessage('C')
    class CopyData(val data: ByteArray) : FrontendMessage('d')
    class CopyDone : FrontendMessage('c')
    class CopyFail : FrontendMessage('f')
    class Describe(val name: String) : FrontendMessage('D')
    class Execute(val name: String, val parameters: Map<String, String>) : FrontendMessage('E')
    class Flush : FrontendMessage('H')
    class FunctionCall(val name: String, val parameters: Map<String, String>) : FrontendMessage('F')
    class Parse(val name: String, val parameters: Map<String, String>) : FrontendMessage('P')
    class Password(val password: String) : FrontendMessage('p')
    class Query(val query: String) : FrontendMessage('Q')
    class Sync : FrontendMessage('S')
    class Terminate : FrontendMessage('X')
}
