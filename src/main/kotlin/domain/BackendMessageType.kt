package domain

enum class BackendMessageType(val id: Char) {
    AuthenticationOk('R'),
    AuthenticationKerberosV5('R'),
    AuthenticationCleartextPassword('R'),
    AuthenticationMD5Password('R'),
    AuthenticationSCMCredential('R'),
    AuthenticationGSS('R'),
    AuthenticationSSPI('R'),
    AuthenticationGSSContinue('R'),
    AuthenticationSASL('R'),
    AuthenticationSASLContinue('R'),
    AuthenticationSASLFinal('R'),
    BackendKeyData('K'),
    BindComplete('2'),
    CloseComplete('3'),
    CommandComplete('4'),
    CopyData('d'),
    CopyDone('c'),
    CopyInResponse('G'),
    CopyOutResponse('H'),
    CopyBothResponse('W'),
    DataRow('D'),
    EmptyQueryResponse('I'),
    ErrorResponse('E'),
    FunctionCallResponse('V'),
    NegotiateProtocolVersion('v'),
    NoData('n'),
    NoticeResponse('N'),
    NotificationResponse('A'),
    ParameterDescription('t'),
    ParameterStatus('S'),
    ParseComplete('1'),
    PortalSuspended('s'),
    ReadyForQuery('Z'),
    RowDescription('T');

    companion object {
        fun fromId(id: Char): BackendMessageType {
            return values().first { it.id == id }
        }
    }
}
