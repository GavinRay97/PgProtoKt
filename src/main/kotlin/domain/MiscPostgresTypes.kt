package domain

enum class PostgresDataFormat(val id: Int) {
    TEXT(0),
    BINARY(1);

    companion object {
        fun fromId(id: Int): PostgresDataFormat {
            return when (id) {
                0 -> TEXT
                1 -> BINARY
                else -> throw IllegalArgumentException("Unknown PostgresDataFormat id: $id")
            }
        }
    }
}
