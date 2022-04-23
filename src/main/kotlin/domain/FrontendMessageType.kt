package domain

enum class FrontendMessageType(val id: Char?) {
    Startup(null),
    Bind('B'),
    Close('C'),
    CopyData('d'),
    CopyDone('c'),
    CopyFail('f'),
    Describe('D'),
    Execute('E'),
    Flush('H'),
    FunctionCall('F'),
    Parse('P'),
    Password('p'),
    Query('Q'),
    Sync('S'),
    Terminate('X');

    companion object {
        fun fromId(id: Char): FrontendMessageType {
            return values().first { it.id == id }
        }
    }
}
