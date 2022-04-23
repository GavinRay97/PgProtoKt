package utils

import utils.PgType.Category
import utils.PgType.Type

/**
 * oid: Row identifier
 * typname: Data type name
 * typarray: If typarray is not zero then it identifies another row in pg_type, which is the “true” array type having this type as element
 * typelem: If typelem is not zero then it identifies another row in pg_type, defining the type yielded by subscripting. This should be zero if typsubscript is zero. However, it can be zero when typsubscript isn't zero, if the handler doesn't need typelem to determine the subscripting result type. Note that a typelem dependency is considered to imply physical containment of the element type in this type; so DDL changes on the element type might be restricted by the presence of this type.
 * typlen: For a fixed-size type, typlen is the number of bytes in the internal representation of the type. But for a variable-length type, typlen is negative. -1 indicates a “varlena” type (one that has a length word), -2 indicates a null-terminated C string.
 * typtype: typtype is b for a base type, c for a composite type (e.g., a table's row type), d for a domain, e for an enum type, p for a pseudo-type, r for a range type, or m for a multirange type. See also typrelid and typbasetype.
 * typcategory: typcategory is an arbitrary classification of data types that is used by the parser to determine which implicit casts should be “preferred”. See Table 52.63.
 *
 *  Table 52.63. typcategory Codes
 *  Code	Category
 *  A	Array types
 *  B	Boolean types
 *  C	Composite types
 *  D	Date/time types
 *  E	Enum types
 *  G	Geometric types
 *  I	Network address types
 *  N	Numeric types
 *  P	Pseudo-types
 *  R	Range types
 *  S	String types
 *  T	Timespan types
 *  U	User-defined types
 *  V	Bit-string types
 *  X	unknown type
 */

// oid  |     typname      | typarray | typelem | typlen | typtype | typcategory
// ------+------------------+----------+---------+--------+---------+-------------
// 16 | bool             |     1000 |       0 |      1 | b       | B
// 17 | bytea            |     1001 |       0 |     -1 | b       | U
// 18 | char             |     1002 |       0 |      1 | b       | S
// 19 | name             |     1003 |      18 |     64 | b       | S
// 20 | int8             |     1016 |       0 |      8 | b       | N
// 21 | int2             |     1005 |       0 |      2 | b       | N
// 22 | int2vector       |     1006 |      21 |     -1 | b       | A
// 23 | int4             |     1007 |       0 |      4 | b       | N
// 24 | regproc          |     1008 |       0 |      4 | b       | N
// 25 | text             |     1009 |       0 |     -1 | b       | S
// 26 | oid              |     1028 |       0 |      4 | b       | N
// 27 | tid              |     1010 |       0 |      6 | b       | U
// 28 | xid              |     1011 |       0 |      4 | b       | U
// 29 | cid              |     1012 |       0 |      4 | b       | U
// 30 | oidvector        |     1013 |      26 |     -1 | b       | A
// 32 | pg_ddl_command   |        0 |       0 |      8 | p       | P
// 71 | pg_type          |      210 |       0 |     -1 | c       | C
// 75 | pg_attribute     |      270 |       0 |     -1 | c       | C
// 81 | pg_proc          |      272 |       0 |     -1 | c       | C
// 83 | pg_class         |      273 |       0 |     -1 | c       | C
// 114 | json             |      199 |       0 |     -1 | b       | U
// 142 | xml              |      143 |       0 |     -1 | b       | U
// 143 | _xml             |        0 |     142 |     -1 | b       | A
// 194 | pg_node_tree     |        0 |       0 |     -1 | b       | S
// 199 | _json            |        0 |     114 |     -1 | b       | A
// 210 | _pg_type         |        0 |      71 |     -1 | b       | A
// 269 | table_am_handler |        0 |       0 |      4 | p       | P
// 270 | _pg_attribute    |        0 |      75 |     -1 | b       | A
// 271 | _xid8            |        0 |    5069 |     -1 | b       | A
// 272 | _pg_proc         |        0 |      81 |     -1 | b       | A
// 273 | _pg_class        |        0 |      83 |     -1 | b       | A
// 325 | index_am_handler |        0 |       0 |      4 | p       | P
// 600 | point            |     1017 |     701 |     16 | b       | G
// 601 | lseg             |     1018 |     600 |     32 | b       | G
// 602 | path             |     1019 |       0 |     -1 | b       | G
// 603 | box              |     1020 |     600 |     32 | b       | G
// 604 | polygon          |     1027 |       0 |     -1 | b       | G
// 628 | line             |      629 |     701 |     24 | b       | G
// 629 | _line            |        0 |     628 |     -1 | b       | A
// 650 | cidr             |      651 |       0 |     -1 | b       | I
// 651 | _cidr            |        0 |     650 |     -1 | b       | A
// 700 | float4           |     1021 |       0 |      4 | b       | N
// 701 | float8           |     1022 |       0 |      8 | b       | N
// 705 | unknown          |        0 |       0 |     -2 | p       | X
// 718 | circle           |      719 |       0 |     24 | b       | G
// 719 | _circle          |        0 |     718 |     -1 | b       | A
// 774 | macaddr8         |      775 |       0 |      8 | b       | U
// 775 | _macaddr8        |        0 |     774 |     -1 | b       | A
// 790 | money            |      791 |       0 |      8 | b       | N
// 791 | _money           |        0 |     790 |     -1 | b       | A
// 829 | macaddr          |     1040 |       0 |      6 | b       | U
// 869 | inet             |     1041 |       0 |     -1 | b       | I
// 1000 | _bool            |        0 |      16 |     -1 | b       | A
// 1001 | _bytea           |        0 |      17 |     -1 | b       | A
// 1002 | _char            |        0 |      18 |     -1 | b       | A
// 1003 | _name            |        0 |      19 |     -1 | b       | A
// 1005 | _int2            |        0 |      21 |     -1 | b       | A
// 1006 | _int2vector      |        0 |      22 |     -1 | b       | A
// 1007 | _int4            |        0 |      23 |     -1 | b       | A
// 1008 | _regproc         |        0 |      24 |     -1 | b       | A
// 1009 | _text            |        0 |      25 |     -1 | b       | A
// 1010 | _tid             |        0 |      27 |     -1 | b       | A
// 1011 | _xid             |        0 |      28 |     -1 | b       | A
// 1012 | _cid             |        0 |      29 |     -1 | b       | A
// 1013 | _oidvector       |        0 |      30 |     -1 | b       | A
// 1014 | _bpchar          |        0 |    1042 |     -1 | b       | A
// 1015 | _varchar         |        0 |    1043 |     -1 | b       | A
// 1016 | _int8            |        0 |      20 |     -1 | b       | A
// 1017 | _point           |        0 |     600 |     -1 | b       | A
// 1018 | _lseg            |        0 |     601 |     -1 | b       | A
// 1019 | _path            |        0 |     602 |     -1 | b       | A
// 1020 | _box             |        0 |     603 |     -1 | b       | A
// 1021 | _float4          |        0 |     700 |     -1 | b       | A
// 1022 | _float8          |        0 |     701 |     -1 | b       | A
// 1027 | _polygon         |        0 |     604 |     -1 | b       | A
// 1028 | _oid             |        0 |      26 |     -1 | b       | A
// 1033 | aclitem          |     1034 |       0 |     12 | b       | U
// 1034 | _aclitem         |        0 |    1033 |     -1 | b       | A
// 1040 | _macaddr         |        0 |     829 |     -1 | b       | A
// 1041 | _inet            |        0 |     869 |     -1 | b       | A
// 1042 | bpchar           |     1014 |       0 |     -1 | b       | S
// 1043 | varchar          |     1015 |       0 |     -1 | b       | S
// 1082 | date             |     1182 |       0 |      4 | b       | D
// 1083 | time             |     1183 |       0 |      8 | b       | D
// 1114 | timestamp        |     1115 |       0 |      8 | b       | D
// 1115 | _timestamp       |        0 |    1114 |     -1 | b       | A
// 1182 | _date            |        0 |    1082 |     -1 | b       | A
// 1183 | _time            |        0 |    1083 |     -1 | b       | A
// 1184 | timestamptz      |     1185 |       0 |      8 | b       | D
// 1185 | _timestamptz     |        0 |    1184 |     -1 | b       | A
// 1186 | interval         |     1187 |       0 |     16 | b       | T
// 1187 | _interval        |        0 |    1186 |     -1 | b       | A
// 1231 | _numeric         |        0 |    1700 |     -1 | b       | A
// 1248 | pg_database      |    12052 |       0 |     -1 | c       | C
// 1263 | _cstring         |        0 |    2275 |     -1 | b       | A
// 1266 | timetz           |     1270 |       0 |     12 | b       | D
// 1270 | _timetz          |        0 |    1266 |     -1 | b       | A
// 1560 | bit              |     1561 |       0 |     -1 | b       | V
// 1561 | _bit             |        0 |    1560 |     -1 | b       | A
// 1562 | varbit           |     1563 |       0 |     -1 | b       | V
// 1563 | _varbit          |        0 |    1562 |     -1 | b       | A
// 1700 | numeric          |     1231 |       0 |     -1 | b       | N

data class PgType(
    val oid: Int,
    val name: String,
    val arrayType: Int,
    val elementType: Int,
    val byteLength: Int,
    val type: Type,
    val category: Category
) {
    enum class Type(val id: Char) {
        Base('b'),
        Composite('c'),
        Domain('d'),
        Enum('e'),
        Pseudo('p'),
        Range('r'),
        Multirange('m'),
    }

    enum class Category(val id: Char) {
        Array('A'),
        Boolean('B'),
        Composite('C'),
        DateTime('D'),
        Enum('E'),
        Geometric('G'),
        NetworkAddress('I'),
        Numeric('N'),
        Pseudo('P'),
        Range('R'),
        String('S'),
        Timespan('T'),
        UserDefined('U'),
        BitString('V'),
        Unknown('X'),
    }
}

object PgTypeTable {
    val bool = PgType(16, "bool", 1000, 0, 1, Type.Base, Category.Boolean)
    val bytea = PgType(17, "bytea", 1001, 0, -1, Type.Base, Category.UserDefined)
    val char = PgType(18, "char", 1002, 0, 1, Type.Base, Category.String)
    val name = PgType(19, "name", 1003, 18, 64, Type.Base, Category.String)
    val int8 = PgType(20, "int8", 1016, 0, 8, Type.Base, Category.Numeric)
    val int2 = PgType(21, "int2", 1005, 0, 2, Type.Base, Category.Numeric)
    val int2vector = PgType(22, "int2vector", 1006, 21, -1, Type.Base, Category.Array)
    val int4 = PgType(23, "int4", 1007, 0, 4, Type.Base, Category.Numeric)
    val regproc = PgType(24, "regproc", 1008, 0, 4, Type.Base, Category.Numeric)
    val text = PgType(25, "text", 1009, 0, -1, Type.Base, Category.String)
    val oid = PgType(26, "oid", 1028, 0, 4, Type.Base, Category.Numeric)

    // Fill in the rest of the uncommon ones later

    val json = PgType(114, "json", 199, 0, -1, Type.Base, Category.UserDefined)
    val xml = PgType(142, "xml", 143, 0, -1, Type.Base, Category.UserDefined)
    val bpchar = PgType(1042, "bpchar", 1014, 0, -1, Type.Base, Category.String)
    val varchar = PgType(1043, "varchar", 1015, 0, -1, Type.Base, Category.String)
    val date = PgType(1082, "date", 1182, 0, 4, Type.Base, Category.DateTime)
    val time = PgType(1083, "time", 1183, 0, 8, Type.Base, Category.DateTime)
    val timestamp = PgType(1114, "timestamp", 1114, 0, 8, Type.Base, Category.DateTime)
    val timestamptz = PgType(1184, "timestamptz", 1184, 0, 8, Type.Base, Category.DateTime)
    val interval = PgType(1186, "interval", 1186, 0, 16, Type.Base, Category.Timespan)
    val numeric = PgType(1231, "numeric", 1231, 0, -1, Type.Base, Category.Numeric)

    // Create a map by grouping via the variable name
    val typeMap = mapOf(
        "bool" to bool,
        "bytea" to bytea,
        "char" to char,
        "name" to name,
        "int8" to int8,
        "int2" to int2,
        "int2vector" to int2vector,
        "int4" to int4,
        "regproc" to regproc,
        "text" to text,
        "oid" to oid,
        "json" to json,
        "xml" to xml,
        "bpchar" to bpchar,
        "varchar" to varchar,
        "date" to date,
        "time" to time,
        "timestamp" to timestamp,
        "timestamptz" to timestamptz,
        "interval" to interval,
        "numeric" to numeric
    )

    val typeMapByOid = typeMap.map { it.value.oid to it.value }.toMap()
}
