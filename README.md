# PgProtoKt - A framework for building Postgres Wire-Protocol backends on the JVM

This library takes care of the difficult work of managing binary serialization/de-serialization and networking
protocols, so that you can focus on implementing business logic.

The goal is to allow users to write new Postgres wire-protocol compatible services with minimum effort, while keeping
the codebase as simple as possible.

## Example

Below is a working example of an interface implementation that can successfully be connected to with `psql` (or any
other client), complete the initial startup + authentication handshake, and then returns a static result of the below
for every query it receives:

```
 id |     email
----+----------------
 1  | user1@site.com
 2  | user2@site.com
 3  | user3@site.com     
```

```kt
object ExamplePostgresFrontendMessageHandler : IPostgresFrontendMessageHandler {

    override fun handleStartup(ctx: ChannelHandlerContext, msg: FrontendMessage.Startup) {
        ctx.write(BackendMessage.AuthenticationOk())
        ctx.write(BackendMessage.BackendKeyData(processId = 1, secretKey = 2))
        ctx.write(BackendMessage.ReadyForQuery(TransactionStatus.IDLE))
        ctx.flush()
    }

    override fun handleQuery(ctx: ChannelHandlerContext, msg: FrontendMessage.Query) {

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

        // Write 3 example rows
        for (i in 1..3) {
            ctx.write(BackendMessage.DataRow(columns = listOf(i, "user$i@site.com")))
        }

        ctx.write(BackendMessage.CommandComplete(affectedRows = 3, type = CommandType.SELECT))
        ctx.write(BackendMessage.ReadyForQuery(TransactionStatus.IDLE))
        ctx.flush()
    }

}
```
