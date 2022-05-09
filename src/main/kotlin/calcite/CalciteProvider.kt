package calcite

import org.apache.calcite.adapter.csv.CsvSchemaFactory
import org.apache.calcite.avatica.util.Casing
import org.apache.calcite.config.CalciteConnectionProperty
import org.apache.calcite.jdbc.CalciteConnection
import org.apache.calcite.jdbc.Driver
import org.apache.calcite.schema.SchemaPlus
import org.apache.calcite.sql.parser.SqlParser
import org.apache.calcite.tools.FrameworkConfig
import org.apache.calcite.tools.Frameworks
import org.moditect.jfranalytics.JfrSchemaFactory
import java.sql.DriverManager
import java.util.Properties

object CalciteProvider {
    val connection = initCalciteConnection()
    val rootSchema: SchemaPlus = connection.rootSchema

    val frameworkConfig: FrameworkConfig = Frameworks.newConfigBuilder()
        .defaultSchema(connection.rootSchema)
        .parserConfig(SqlParser.config().withCaseSensitive(false))
        .build()

    init {
        val csvSchema = CsvSchemaFactory.INSTANCE.create(
            rootSchema, "csv",
            mapOf("directory" to "src/main/resources/csv")
        )
        rootSchema.add("csv", csvSchema)

        val jfrSchema = JfrSchemaFactory().create(
            rootSchema, "jfr",
            mapOf("file" to "src/main/resources/jfr/object-allocations.jfr")
        )
        rootSchema.add("jfr", jfrSchema)
    }

    private fun initCalciteConnection(): CalciteConnection {
        // Initialize the JDBC driver
        Class.forName(Driver::class.java.name)
        DriverManager.registerDriver(Driver())
        return DriverManager
            .getConnection(
                "jdbc:calcite:",
                Properties().apply {
                    setProperty(CalciteConnectionProperty.FUN.camelName(), "postgresql")
                    setProperty(CalciteConnectionProperty.CASE_SENSITIVE.camelName(), "false")
                    setProperty(CalciteConnectionProperty.QUOTED_CASING.camelName(), Casing.UNCHANGED.name)
                    setProperty(CalciteConnectionProperty.UNQUOTED_CASING.camelName(), Casing.UNCHANGED.name)
                }
            )
            .unwrap(CalciteConnection::class.java)
    }
}
