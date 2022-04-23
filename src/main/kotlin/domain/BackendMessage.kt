package domain

sealed class BackendMessage(val id: Char) {
    class AuthenticationOk : BackendMessage('R')
    class AuthenticationKerberosV5 : BackendMessage('R')
    class AuthenticationCleartextPassword : BackendMessage('R')
    class AuthenticationMD5Password(val salt: ByteArray) : BackendMessage('R')
    class AuthenticationSCMCredential : BackendMessage('R')
    class AuthenticationGSS : BackendMessage('R')
    class AuthenticationSSPI : BackendMessage('R')
    class AuthenticationGSSContinue : BackendMessage('R')
    class AuthenticationSASL : BackendMessage('R')
    class AuthenticationSASLContinue : BackendMessage('R')
    class AuthenticationSASLFinal : BackendMessage('R')
    class BackendKeyData(val processId: Int, val secretKey: Int) : BackendMessage('K')
    class BindComplete : BackendMessage('2')
    class CloseComplete : BackendMessage('3')
    class CommandComplete(val affectedRows: Int, val type: CommandType) : BackendMessage('C') {
        enum class CommandType {
            SELECT,
            INSERT,
            UPDATE,
            DELETE,
            MOVE,
            FETCH,
            COPY
        }
    }

    class CopyData(val data: ByteArray) : BackendMessage('d')
    class CopyDone : BackendMessage('c')
    class CopyInResponse : BackendMessage('G')
    class CopyOutResponse : BackendMessage('H')
    class CopyBothResponse : BackendMessage('W')
    class DataRow(val columns: List<Any>) : BackendMessage('D')
    class EmptyQueryResponse : BackendMessage('I')
    class ErrorResponse(val data: String) : BackendMessage('E')
    class FunctionCallResponse(val data: String) : BackendMessage('V')
    class NegotiateProtocolVersion(val data: Int) : BackendMessage('v')
    class NoData : BackendMessage('n')
    class NoticeResponse(val data: String) : BackendMessage('N')
    class NotificationResponse(val data: String) : BackendMessage('A')
    class ParameterDescription(val data: List<String>) : BackendMessage('t')
    class ParameterStatus(val data: String) : BackendMessage('S')
    class ParseComplete : BackendMessage('1')
    class PortalSuspended : BackendMessage('s')
    class ReadyForQuery(val transactionStatus: TransactionStatus) : BackendMessage('Z') {
        enum class TransactionStatus(val value: Char) {
            IDLE('I'),
            STARTED('T'),
            FAILED('E')
        }
    }

    class RowDescription(val fields: List<Field>) : BackendMessage('T') {
        class Field(
            val name: String,
            val tableOid: Int,
            val columnIdx: Int,
            val dataTypeOid: Int,
            val dataTypeSize: Int,
            val dataTypeModifier: Int,
            val format: PostgresDataFormat
        )
    }
}
