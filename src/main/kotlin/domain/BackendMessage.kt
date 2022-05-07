package domain

import utils.PgTypeTable

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
        open class Field(
            val name: String,
            val dataTypeOid: Int,
            val dataTypeSize: Int,
            val dataTypeModifier: Int = -1,
            val tableOid: Int = 0,
            val columnIdx: Int = 0,
            val format: PostgresDataFormat = PostgresDataFormat.TEXT
        )

        class TextField(
            name: String,
            tableOid: Int = 0,
            columnIdx: Int = 0,
            format: PostgresDataFormat = PostgresDataFormat.TEXT
        ) : Field(name, dataTypeOid, dataTypeSize, dataTypeModifier, tableOid, columnIdx, format) {
            companion object {
                val dataTypeOid: Int = PgTypeTable.text.oid
                val dataTypeSize: Int = PgTypeTable.text.byteLength
                val dataTypeModifier: Int = -1
            }
        }

        class CharField(
            name: String,
            tableOid: Int = 0,
            columnIdx: Int = 0,
            format: PostgresDataFormat = PostgresDataFormat.TEXT
        ) : Field(name, dataTypeOid, dataTypeSize, dataTypeModifier, tableOid, columnIdx, format) {
            companion object {
                val dataTypeOid: Int = PgTypeTable.char.oid
                val dataTypeSize: Int = PgTypeTable.char.byteLength
                val dataTypeModifier: Int = -1
            }
        }

        class DateField(
            name: String,
            tableOid: Int = 0,
            columnIdx: Int = 0,
            format: PostgresDataFormat = PostgresDataFormat.TEXT
        ) : Field(name, dataTypeOid, dataTypeSize, dataTypeModifier, tableOid, columnIdx, format) {
            companion object {
                val dataTypeOid: Int = PgTypeTable.date.oid
                val dataTypeSize: Int = PgTypeTable.date.byteLength
                val dataTypeModifier: Int = -1
            }
        }

        class TimeField(
            name: String,
            tableOid: Int = 0,
            columnIdx: Int = 0,
            format: PostgresDataFormat = PostgresDataFormat.TEXT
        ) : Field(name, dataTypeOid, dataTypeSize, dataTypeModifier, tableOid, columnIdx, format) {
            companion object {
                val dataTypeOid: Int = PgTypeTable.time.oid
                val dataTypeSize: Int = PgTypeTable.time.byteLength
                val dataTypeModifier: Int = -1
            }
        }

        class TimestampField(
            name: String,
            tableOid: Int = 0,
            columnIdx: Int = 0,
            format: PostgresDataFormat = PostgresDataFormat.TEXT
        ) : Field(name, dataTypeOid, dataTypeSize, dataTypeModifier, tableOid, columnIdx, format) {
            companion object {
                val dataTypeOid: Int = PgTypeTable.timestamp.oid
                val dataTypeSize: Int = PgTypeTable.timestamp.byteLength
                val dataTypeModifier: Int = -1
            }
        }

        class TimestamptzField(
            name: String,
            tableOid: Int = 0,
            columnIdx: Int = 0,
            format: PostgresDataFormat = PostgresDataFormat.TEXT
        ) : Field(name, dataTypeOid, dataTypeSize, dataTypeModifier, tableOid, columnIdx, format) {
            companion object {
                val dataTypeOid: Int = PgTypeTable.timestamptz.oid
                val dataTypeSize: Int = PgTypeTable.timestamptz.byteLength
                val dataTypeModifier: Int = -1
            }
        }

        class IntervalField(
            name: String,
            tableOid: Int = 0,
            columnIdx: Int = 0,
            format: PostgresDataFormat = PostgresDataFormat.TEXT
        ) : Field(name, dataTypeOid, dataTypeSize, dataTypeModifier, tableOid, columnIdx, format) {
            companion object {
                val dataTypeOid: Int = PgTypeTable.interval.oid
                val dataTypeSize: Int = PgTypeTable.interval.byteLength
                val dataTypeModifier: Int = -1
            }
        }


        class NumericField(
            name: String,
            tableOid: Int = 0,
            columnIdx: Int = 0,
            format: PostgresDataFormat = PostgresDataFormat.TEXT
        ) : Field(name, dataTypeOid, dataTypeSize, dataTypeModifier, tableOid, columnIdx, format) {
            companion object {
                val dataTypeOid: Int = PgTypeTable.numeric.oid
                val dataTypeSize: Int = PgTypeTable.numeric.byteLength
                val dataTypeModifier: Int = -1
            }
        }

        class Int4Field(
            name: String,
            tableOid: Int = 0,
            columnIdx: Int = 0,
            format: PostgresDataFormat = PostgresDataFormat.TEXT
        ) : Field(name, dataTypeOid, dataTypeSize, dataTypeModifier, tableOid, columnIdx, format) {
            companion object {
                val dataTypeOid: Int = PgTypeTable.int4.oid
                val dataTypeSize: Int = PgTypeTable.int4.byteLength
                val dataTypeModifier: Int = -1
            }
        }

        class BooleanField(
            name: String,
            tableOid: Int = 0,
            columnIdx: Int = 0,
            format: PostgresDataFormat = PostgresDataFormat.TEXT
        ) : Field(name, dataTypeOid, dataTypeSize, dataTypeModifier, tableOid, columnIdx, format) {
            companion object {
                val dataTypeOid: Int = PgTypeTable.bool.oid
                val dataTypeSize: Int = PgTypeTable.bool.byteLength
                val dataTypeModifier: Int = -1
            }
        }

    }

}
